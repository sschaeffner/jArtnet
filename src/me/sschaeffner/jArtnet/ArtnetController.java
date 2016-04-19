/*
 * Copyright (C) 2015  Simon Schaeffner <simon.schaeffner@googlemail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package me.sschaeffner.jArtnet;

import me.sschaeffner.jArtnet.packets.ArtPollPacket;
import me.sschaeffner.jArtnet.packets.ArtPollReplyPacket;
import me.sschaeffner.jArtnet.packets.ArtnetPacket;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * An Art-Net Controller.
 *
 * This is typically a lighting console.
 * May be used to send Art-Net packets.
 *
 * @author sschaeffner
 */
public class ArtnetController {

    //controller style code
    private static final int STYLE_CODE = ArtnetStyleCodes.ST_CONTROLLER;

    //list of all connected nodes
    private final ArrayList<ArtnetNode> nodes;

    //server socket to send and receive Art-Net packets with
    private final DatagramSocket socket;

    //host address
    private NetworkAddress host;

    //listening port
    private int port;

    //receiver thread listening for Art-Net packets
    private final Thread receiverThread;

    //list of listeners for received Art-Net packets
    private final Set<ArtnetPacketListener> listeners;

    //whether to ignore packets sent from this controller
    private boolean ignoreOwnPackets = true;

    //whether the controller is currently running
    private boolean running = true;

    /**
     * Constructs a new instance of this class.
     */
    protected ArtnetController(NetworkAddress host, int port) throws IOException {
        this.host = host;
        this.port = port;
        this.nodes = new ArrayList<>();
        this.listeners = new HashSet<>();

        //open udp socket
        try {
            socket = new DatagramSocket(new InetSocketAddress(this.host.getInterfaceAddress().getAddress(), this.port));
        } catch (SocketException e) {
            e.printStackTrace();
            throw new IOException("cannot start ArtnetController: cannot open socket");
        }

        //start receiver thread
        this.receiverThread = new Thread(() -> {
            try {
                System.out.println("Listening on " + host.getInterfaceAddress().getAddress() + ":" + this.port);

                byte[] receiveData = new byte[600];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, 0, receiveData.length);

                while (running) {
                    if (!socket.isClosed()) {
                        try {
                            socket.receive(receivePacket);
                            byte[] data = receivePacket.getData();
                            InetAddress packetSender = receivePacket.getAddress();
                            int packetPort = receivePacket.getPort();
                            onPacketReceive(data, packetSender, packetPort);
                        } catch (SocketException e) {
                            //do nothing as the socket is just closed
                        } catch (MalformedArtnetPacketException e) {
                            e.printStackTrace();
                        }
                    }
                }

            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        });
        this.receiverThread.start();
    }

    /**
     * Tries to discover nodes by sending an ArtPoll packet.
     */
    public void discoverNodes() throws MalformedArtnetPacketException {
        ArtPollPacket pollPacket = new ArtPollPacket();
        broadcastPacket(pollPacket);
    }

    /**
     * Constructs an ArtPollReply packet matching this controller.
     *
     * @return an ArtPollReplyPacket matching this controller
     */
    private ArtPollReplyPacket constructArtPollReplyPacket() throws MalformedArtnetPacketException {
        InetAddress address = this.host.getInterfaceAddress().getAddress();
        byte versInfoH = (byte) 0;
        byte versInfoL = (byte) 1;
        byte netSwitch = 0, subSwitch = 0;
        byte oemHi = (byte)0xff, oem = (byte)0xFF;
        byte ubeaVersion = 0;
        byte status1 = (byte) 0b00110000;
        byte estaManLo = 0, estaManHi = 0;
        byte[] shortName = new byte[]{'j', 'A', 'r', 't', 'n', 'e', 't', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 0};
        byte[] longName = new byte[]{'j', 'A', 'r', 't', 'n', 'e', 't', ' ', '-', ' ', 'A', 'n', ' ', 'A', 'r', 't', '-',
                                     'N', 'e', 't', ' ', 'l', 'i', 'b', 'r', 'a', 'r', 'y', ' ', 'f', 'o', 'r', ' ', 'J',
                                     'a', 'v', 'a', ' ', 'b', 'y', ' ', 'S', 'i', 'm', 'o', 'n', ' ', 'S', 'c', 'h', 'a',
                                     'e', 'f', 'f', 'n', 'e', 'r', ' ', ' ', ' ', ' ', ' ', ' ', 0};
        byte[] nodeReport = new byte[]{'r', 'e', 'a', 'd', 'y', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
                                       ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
                                       ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
                                       ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 0};
        byte numPortsHi = 0, numPortsLo = 1;
        byte[] portTypes = new byte[]{0b001000101, 0b001000101, 0b001000101, 0b001000101};
        byte[] goodInput = new byte[]{0b0000, 0b0000, 0b0000, 0b0000}, goodOutput = new byte[]{(byte)0b10000000, (byte)0b10000000, (byte)0b10000000, (byte)0b10000000};
        byte[] swIn = new byte[]{0, 0, 0, 0}, swOut = new byte[]{0, 0, 0, 0};
        byte swVideo = 0;
        byte swMacro = 0;
        byte swRemote = 0;
        byte style = STYLE_CODE;
        byte[] mac = new byte[6];
        try {
            mac = this.host.getNetworkInterface().getHardwareAddress();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        byte[] bindIp = new byte[]{0, 0, 0, 0};
        byte bindIndex = 0;
        byte status2 = 0b00001110;

        return new ArtPollReplyPacket(address, versInfoH, versInfoL, netSwitch, subSwitch, oemHi, oem, ubeaVersion,
                status1, estaManLo, estaManHi, shortName, longName, nodeReport, numPortsHi, numPortsLo, portTypes,
                goodInput, goodOutput, swIn, swOut, swVideo, swMacro, swRemote, style, mac, bindIp, bindIndex, status2);
    }

    /**
     * Sends an Art-Net packet to a single node.
     *
     * @param artnetPacket packet to send
     */
    public void unicastPacket(ArtnetPacket artnetPacket, ArtnetNode node) throws MalformedArtnetPacketException {
        if (socket != null) {
            InetAddress nodeAddress = node.getInetAddress();
            if (nodeAddress != null) {

                byte[] data = artnetPacket.getPacketBytes();
                DatagramPacket packet = new DatagramPacket(data, data.length, nodeAddress, this.port);
            }
        }
    }

    /**
     * Sends an Art-Net packet to all nodes.
     *
     * @param artnetPacket packet to send
     */
    public void broadcastPacket(ArtnetPacket artnetPacket) throws MalformedArtnetPacketException {
        if (socket != null) {
            if (host != null) {
                byte[] data = artnetPacket.getPacketBytes();
                DatagramPacket packet = new DatagramPacket(data, data.length, this.host.getBroadcastAddress(), this.port);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("no broadcast address available");
            }
        } else {
            System.err.println("no socket available to broadcast");
        }
    }

    /**
     * Manages what to do when a packet is received.
     *
     * @param bytes     received data
     * @param sender    InetAddress of the packet's sender
     * @param port      packet sender's port
     */
    private void onPacketReceive(byte[] bytes, InetAddress sender, int port) throws MalformedArtnetPacketException {

        InetAddress localhost = null;
        try {
            localhost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //ignore packets sent from this controller
        if (!(ignoreOwnPackets && (this.host.getInterfaceAddress().getAddress().equals(sender) || (localhost != null && localhost.equals(sender))))) {

            ArtnetPacket artnetPacket = ArtnetOpCodes.fromBytes(bytes);
            if (artnetPacket != null) {
                //if ArtPollReply is sent, add all new nodes to list
                if (artnetPacket instanceof ArtPollReplyPacket) {
                    handleArtPollReplyPackets((ArtPollReplyPacket) artnetPacket, sender);
                } else {
                    //set sender node for other packets
                    ArtnetNode senderNode = getNodeFromInetAddress(sender);
                    if (senderNode != null) artnetPacket.setSender(senderNode);

                    //inform listeners
                    listeners.forEach(listener -> listener.onArtnetPacketReceive(artnetPacket));
                }
            }
        }
    }

    /**
     * Handles received ArtPollReply packets.
     *
     * Either adds a new node or updates the node's information.
     *
     * @param packet    ArtPollReply packet
     * @param sender    the sender's InetAddress
     */
    private void handleArtPollReplyPackets(ArtPollReplyPacket packet, InetAddress sender) {
        ArtnetNode senderNode = isNodeRegistered(sender);

        if (senderNode == null) {
            //create new node and add it to the list
            ArtnetNode node = new ArtnetNode(sender, packet);
            nodes.add(node);
            System.out.println("new node: " + node);
        } else {
            //update ArtnetNode information
            senderNode.setArtPollReplyPacket(packet);
        }
    }

    /**
     * Checks whether a node with a given InetAddress is already registered.
     *
     * @param sender    InetAddress to check
     * @return          whether a node is already registered
     */
    private ArtnetNode isNodeRegistered(InetAddress sender) {
        for (ArtnetNode node : nodes) {
            if (node.getInetAddress().equals(sender)) {
                return node;
            }
        }
        return null;
    }

    /**
     * Returns the node with a specific InetAddress.
     *
     * If no node is registered, null is returned.
     *
     * @param nodeAddress   the node's address
     * @return              node with matching InetAddress
     */
    private ArtnetNode getNodeFromInetAddress(InetAddress nodeAddress) {
        for (ArtnetNode node : nodes) if (node.getInetAddress().equals(nodeAddress)) return node;
        return null;
    }

    /**
     * Closes the server socket.
     *
     * Blocking until the receiver thread is dead.
     */
    public void closeSocket() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            if (socket.isConnected()) socket.disconnect();
            socket.close();
        }
        if (receiverThread != null) {
            try {
                receiverThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds an ArtnetPacketListener.
     *
     * @param listener ArtnetPacketListener instance
     */
    public void addArtnetPacketListener(ArtnetPacketListener listener) {
        listeners.add(listener);
    }

    /**
     * Returns all registered nodes.
     *
     * @return all registered nodes
     */
    public ArtnetNode[] getNodes() {
        return nodes.toArray(new ArtnetNode[nodes.size()]);
    }

    /**
     * Removes an ArtnetPacketListener.
     *
     * @param listener  ArtnetPacketListener instance
     * @return <tt>true</tt> if the listener was registered and successfully removed
     */
    public boolean removeArtnetPacketListener(ArtnetPacketListener listener) {
        return listeners.remove(listener);
    }

    /**
     * Whether or not to ignore own packets.
     *
     * @param ignoreOwnPackets whether or not to ignore own packets
     */
    public void setIgnoreOwnPackets(boolean ignoreOwnPackets) {
        this.ignoreOwnPackets = ignoreOwnPackets;
    }
}

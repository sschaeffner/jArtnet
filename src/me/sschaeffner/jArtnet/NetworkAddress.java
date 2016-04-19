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

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * One of the network addresses available for usage on this machine.
 *
 * An InterfaceAddress (e.g. 192.168.0.2) combined with its broadcasting InetAddress (e.g. 192.168.0.255).
 */
public class NetworkAddress {
    private final NetworkInterface networkInterface;
    private final InterfaceAddress interfaceAddress;//the address of the interface

    /**
     * Constructs a new instance of this class.
     */
    public NetworkAddress(NetworkInterface networkInterface, InterfaceAddress interfaceAddress) {
        this.networkInterface = networkInterface;
        this.interfaceAddress = interfaceAddress;
    }

    /**
     * Returns all the network addresses available on this machine (without loopback address).
     *
     * @return an array of all the network addresses available on the machine
     */
    public static NetworkAddress[] getNetworkAddresses() {
        ArrayList<NetworkAddress> bcAddresses = new ArrayList<>();

        //iterate trough all network interfaces
        try {
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaceEnumeration.hasMoreElements()) {
                NetworkInterface nwi = networkInterfaceEnumeration.nextElement();

                //check whether the network interface is a loopback type interface
                if (!nwi.isLoopback()) {

                    //get the interface's addresses
                    for (InterfaceAddress interfaceAddress : nwi.getInterfaceAddresses()) {

                        //get the address's broadcast address
                        InetAddress bcAddress = interfaceAddress.getBroadcast();

                        //check if broadcast address is available
                        if (bcAddress != null) {
                            bcAddresses.add(new NetworkAddress(nwi, interfaceAddress));
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return bcAddresses.toArray(new NetworkAddress[bcAddresses.size()]);
    }

    /**
     * Returns this machine's loopback address.
     *
     * @return the loopback address
     */
    public static NetworkAddress getLoopbackAddress() {
        //iterate trough all network interfaces
        try {
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaceEnumeration.hasMoreElements()) {
                NetworkInterface nwi = networkInterfaceEnumeration.nextElement();

                //check whether the network interface is a loopback type interface
                if (nwi.isLoopback()) {
                    //get the interface's addresses
                    for (InterfaceAddress interfaceAddress : nwi.getInterfaceAddresses()) {

                        //get the address's broadcast address
                        InetAddress bcAddress = interfaceAddress.getBroadcast();

                        return new NetworkAddress(nwi, interfaceAddress);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String toString() {
        return "NetworkAddress: " + getInterfaceAddress().getAddress().getHostAddress() + " => " + getBroadcastAddress().getHostAddress();
    }

    /**
     * Returns the NetworkInterface.
     *
     * @return NetworkInterface instance
     */
    public NetworkInterface getNetworkInterface() {
        return networkInterface;
    }

    /**
     * Returns the InterfaceAddress.
     *
     * @return InterfaceAddress instance
     */
    public InterfaceAddress getInterfaceAddress() {
        return interfaceAddress;
    }

    /**
     * Returns the broadcast address.
     *
     * @return broadcast address
     */
    public InetAddress getBroadcastAddress() {
        return interfaceAddress.getBroadcast();
    }
}

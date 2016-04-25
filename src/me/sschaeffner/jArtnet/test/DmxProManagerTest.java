/*
 * Copyright (C) 2015  Simon Schaeffner <simon.schaeffner@googlemail.com>
 *
 * This file is part of jArtnet.
 *
 * jArtnet is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * jArtnet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jArtnet.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.sschaeffner.jArtnet.test;

import me.sschaeffner.jArtnet.*;
import me.sschaeffner.jArtnet.packets.ArtDmxPacket;
import me.sschaeffner.jArtnet.packets.ArtnetPacket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author sschaeffner
 */
public class DmxProManagerTest implements ArtnetPacketListener, ArtnetNodeListener {
    public static void main(String[] args) throws MalformedArtnetPacketException, IOException {
        DmxProManagerTest t = new DmxProManagerTest();
        t.sendData();
        t.controller.discoverNodes();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*try {
            t.in();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        t.fade();

        t.controller.closeSocket();
    }

    private ArtnetController controller;
    private byte sequence;

    private DmxProManagerTest() throws IOException {
        NetworkAddress[] nwadd = NetworkAddress.getNetworkAddresses();

        System.out.println("available network addresses: ");

        for (int i = 0; i < nwadd.length; i++) {
            System.out.println(i + ": " + nwadd[i]);
        }

        System.out.print("network address# ");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String in = reader.readLine();
        int choice = -1;
        try {
            choice = Integer.valueOf(in);
        } catch (NumberFormatException e) {
            System.err.println("not a number");
        }

        if (choice != -1) {
            controller = ArtnetControllerFactory.getInstance(nwadd[choice], 6454);
        } else {
            throw new IllegalStateException("no controller");
        }
        controller.addArtnetPacketListener(this);
        controller.addArtnetNodeDiscoveryListener(this);

        sequence = 0;
    }


    private void sendData() throws MalformedArtnetPacketException {
        byte sequence = this.sequence++;
        byte physical = 0;
        byte subUni = 0;
        byte net = 0;
        byte[] data = new byte[512];

        ArtDmxPacket dmxPacket = new ArtDmxPacket(sequence, physical, subUni, net, data);
        controller.broadcastPacket(dmxPacket);
        try {
            Thread.sleep(23);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("empty packet send");
    }

    public void in() throws IOException, MalformedArtnetPacketException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String in;
        System.out.print("r: ");
        while (!(in = reader.readLine()).equals("exit")) {
            int r = Integer.valueOf(in);
            System.out.print("g: ");
            int g = Integer.valueOf(reader.readLine());
            System.out.print("b: ");
            int b = Integer.valueOf(reader.readLine());
            ArtDmxPacket dmxPacket = new ArtDmxPacket(this.sequence++, (byte)0, (byte)0, (byte)0, new byte[]{(byte)r, (byte)g, (byte)b});
            controller.broadcastPacket(dmxPacket);

            System.out.print("r: ");
        }
    }

    private void fade() throws MalformedArtnetPacketException {
        int rM = 1;
        int gM = 0;
        int bM = 0;

        for (int o = 0; o < 9; o++) {
            if (rM == 1) {
                rM = 0;
                gM = 1;
            } else if (gM == 1) {
                gM = 0;
                bM = 1;
            } else if (bM == 1) {
                bM = 0;
                rM = 1;
            }

            for (int i = 0; i < 256; i += 4) {
                int r = i * rM;
                int g = i * gM;
                int b = i * bM;
                ArtDmxPacket dmxPacket = new ArtDmxPacket(this.sequence++, (byte)0, (byte)0, (byte)0, new byte[]{(byte)r, (byte)g, (byte)b});
                controller.broadcastPacket(dmxPacket);
                try {
                    Thread.sleep(23);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 254; i >= 0; i -= 4) {
                int r = i * rM;
                int g = i * gM;
                int b = i * bM;
                ArtDmxPacket dmxPacket = new ArtDmxPacket(this.sequence++, (byte)0, (byte)0, (byte)0, new byte[]{(byte)r, (byte)g, (byte)b});
                controller.broadcastPacket(dmxPacket);
                try {
                    Thread.sleep(23);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        ArtDmxPacket dmxPacket = new ArtDmxPacket(this.sequence++, (byte)0, (byte)0, (byte)0, new byte[]{(byte)0, (byte)0, (byte)0});
        controller.broadcastPacket(dmxPacket);
    }

    @Override
    public void onArtnetPacketReceive(ArtnetPacketReceiveEvent event) {
        System.out.println("packet from: " + event.getReceivedPacket().getSender() + " -> " + event.getReceivedPacket());
    }

    @Override
    public void onArtnetNodeDiscovery(ArtnetNodeDiscoveryEvent event) {
        System.out.println("node discovered: " + event.getNewNode());
    }
}

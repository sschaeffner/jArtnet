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
package me.sschaeffner.jArtnet.test;

import me.sschaeffner.jArtnet.*;
import me.sschaeffner.jArtnet.packets.ArtAddressPacket;
import me.sschaeffner.jArtnet.packets.ArtIpProgReplyPacket;
import me.sschaeffner.jArtnet.packets.ArtnetPacket;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;

/**
 * @author sschaeffner
 */
public class ArtIpProgReplyPacketTest {
    @Before
    public void setup() {

    }

    @Test
    public void constructionTest() throws MalformedArtnetPacketException {
        byte[] progIp = new byte[]{(byte)192, (byte)168, (byte)0, (byte)10};
        byte[] progSm = new byte[]{(byte)255, (byte)255, (byte)0, (byte)0};
        byte progPortHi = (byte) 0x19;
        byte progPortLo = (byte) 0x36;
        byte status = (byte) 0b01000000;

        ArtIpProgReplyPacket pOrig = new ArtIpProgReplyPacket(progIp, progSm, progPortHi, progPortLo, status);
        byte[] bytes = pOrig.getPacketBytes();

        ArtIpProgReplyPacket p = ArtIpProgReplyPacket.fromBytes(bytes);

        Assert.assertArrayEquals(progIp, p.getProgIp());
        Assert.assertArrayEquals(progSm, p.getProgSm());
        Assert.assertEquals(progPortHi, p.getProgPortHi());
        Assert.assertEquals(progPortLo, p.getProgPortLo());
        Assert.assertEquals(status, p.getStatus());

        System.out.println(p);
    }

    @Test
    public void constructionTest2() throws MalformedArtnetPacketException {
        byte[] progIp = new byte[]{(byte)192, (byte)168, (byte)0, (byte)10};
        byte[] progSm = new byte[]{(byte)255, (byte)255, (byte)0, (byte)0};
        byte progPortHi = (byte) 0x19;
        byte progPortLo = (byte) 0x36;
        int progPort = 6454;
        byte status = (byte) 0b01000000;

        ArtIpProgReplyPacket pOrig = new ArtIpProgReplyPacket(progIp, progSm, progPort, status);
        byte[] bytes = pOrig.getPacketBytes();

        ArtIpProgReplyPacket p = ArtIpProgReplyPacket.fromBytes(bytes);

        Assert.assertArrayEquals(progIp, p.getProgIp());
        Assert.assertArrayEquals(progSm, p.getProgSm());
        Assert.assertEquals(progPortHi, p.getProgPortHi());
        Assert.assertEquals(progPortLo, p.getProgPortLo());
        Assert.assertEquals(status, p.getStatus());

        System.out.println(p);
    }

    boolean received;
    @Test
    public void sendPacketTest() throws MalformedArtnetPacketException, IOException {
        byte[] progIp = new byte[]{(byte)192, (byte)168, (byte)0, (byte)10};
        byte[] progSm = new byte[]{(byte)255, (byte)255, (byte)0, (byte)0};
        byte progPortHi = (byte) 0x19;
        byte progPortLo = (byte) 0x36;
        byte status = (byte) 0b01000000;

        ArtIpProgReplyPacket p = new ArtIpProgReplyPacket(progIp, progSm, progPortHi, progPortLo, status);

        ArtnetController controller = ArtnetControllerFactory.getTestingInstance();
        controller.addArtnetPacketListener(event -> {
            if (event.getReceivedPacket() instanceof ArtIpProgReplyPacket) {
                received = true;
            }
        });
        controller.unicastPacket(p, new ArtnetNode(InetAddress.getLoopbackAddress(), ArtnetStyleCodes.ST_CONTROLLER, "loopback", "loopback"));
        received = false;
        while (!received) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void opCodeRecognitionTest() throws MalformedArtnetPacketException {
        byte[] progIp = new byte[]{(byte)192, (byte)168, (byte)0, (byte)10};
        byte[] progSm = new byte[]{(byte)255, (byte)255, (byte)0, (byte)0};
        byte progPortHi = (byte) 0x19;
        byte progPortLo = (byte) 0x36;
        byte status = (byte) 0b01000000;

        ArtIpProgReplyPacket pOrig = new ArtIpProgReplyPacket(progIp, progSm, progPortHi, progPortLo, status);
        byte[] bytes = pOrig.getPacketBytes();

        ArtnetPacket p = ArtnetOpCodes.fromBytes(bytes);
        if (!(p instanceof ArtIpProgReplyPacket)) {
            Assert.fail("ArtIpProgReplyPacket not recognized by ArtnetOpCodes");
        }
    }

    @After
    public void teardown() {

    }

}

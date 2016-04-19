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
import me.sschaeffner.jArtnet.packets.ArtIpProgPacket;
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
public class ArtIpProgPacketTest {
    @Before
    public void setup() {

    }

    @Test
    public void constructionTest() throws MalformedArtnetPacketException {
        //enable programming, disable dhcp, do not return all parameters to default
        //program IP Address, program Subnet Mask, program Port
        byte command = (byte) 0b10000111;
        byte[] progIp = new byte[]{(byte)192, (byte)168, (byte)0, (byte)10};
        byte[] progSm = new byte[]{(byte)255, (byte)255, (byte)0, (byte)0};

        byte progPortHi = (byte) 0x19;
        byte progPortLo = (byte) 0x36;

        ArtIpProgPacket pOrig = new ArtIpProgPacket(command, progIp, progSm, progPortHi, progPortLo);
        byte[] bytes = pOrig.getPacketBytes();

        ArtIpProgPacket p = ArtIpProgPacket.fromBytes(bytes);

        Assert.assertEquals(command, p.getCommand());
        Assert.assertArrayEquals(progIp, p.getProgIp());
        Assert.assertArrayEquals(progSm, p.getProgSm());
        Assert.assertEquals(progPortHi, p.getProgPortHi());
        Assert.assertEquals(progPortLo, p.getProgPortLo());
    }

    @Test
    public void sendPacketTest() throws MalformedArtnetPacketException, IOException {
        //enable programming, disable dhcp, do not return all parameters to default
        //program IP Address, program Subnet Mask, program Port
        byte command = (byte) 0b10000111;

        byte[] progIp = new byte[]{(byte)192, (byte)168, (byte)0, (byte)10};
        byte[] progSm = new byte[]{(byte)255, (byte)255, (byte)0, (byte)0};
        byte progPortHi = (byte) 0x19;
        byte progPortLo = (byte) 0x36;

        ArtIpProgPacket p = new ArtIpProgPacket(command, progIp, progSm, progPortHi, progPortLo);

        ArtnetController controller = ArtnetControllerFactory.getTestingInstance();
        controller.unicastPacket(p, new ArtnetNode(InetAddress.getLoopbackAddress(), ArtnetStyleCodes.ST_CONTROLLER, "loopback", "loopback"));
    }

    @Test
    public void opCodeRecognitionTest() throws MalformedArtnetPacketException {
        byte command = (byte) 0b10000111;
        byte[] progIp = new byte[]{(byte)192, (byte)168, (byte)0, (byte)10};
        byte[] progSm = new byte[]{(byte)255, (byte)255, (byte)0, (byte)0};

        byte progPortHi = (byte) 0x19;
        byte progPortLo = (byte) 0x36;

        ArtIpProgPacket pOrig = new ArtIpProgPacket(command, progIp, progSm, progPortHi, progPortLo);
        byte[] bytes = pOrig.getPacketBytes();

        ArtnetPacket p = ArtnetOpCodes.fromBytes(bytes);

        if (!(p instanceof ArtIpProgPacket)) {
            Assert.fail("ArtIpProgPacket not recognized by ArtnetOpCodes");
        }
    }

    @After
    public void teardown() {

    }
}

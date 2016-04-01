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

import me.sschaeffner.jArtnet.ArtnetController;
import me.sschaeffner.jArtnet.ArtnetNode;
import me.sschaeffner.jArtnet.ArtnetStyleCodes;
import me.sschaeffner.jArtnet.packets.ArtIpProgPacket;
import me.sschaeffner.jArtnet.packets.MalformedArtnetPacketException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
        byte progIpHi = (byte)192;
        byte progIp2 = (byte)168;
        byte progIp1 = (byte)0;
        byte progIpLo = (byte)10;
        byte progSmHi = (byte)255;
        byte progSm2 = (byte)255;
        byte progSm1 = (byte)0;
        byte progSmLo = (byte)0;
        byte progPortHi = (byte) 0x19;
        byte progPortLo = (byte) 0x36;

        ArtIpProgPacket pOrig = new ArtIpProgPacket(command, progIpHi, progIp2, progIp1, progIpLo, progSmHi, progSm2, progSm1, progSmLo, progPortHi, progPortLo);
        byte[] bytes = pOrig.getPacketBytes();

        ArtIpProgPacket p = ArtIpProgPacket.fromBytes(bytes);

        Assert.assertEquals(command, p.getCommand());
        Assert.assertEquals(progIpHi, p.getProgIpHi());
        Assert.assertEquals(progIp2, p.getProgIp2());
        Assert.assertEquals(progIp1, p.getProgIp1());
        Assert.assertEquals(progIpLo, p.getProgIpLo());
        Assert.assertEquals(progSmHi, p.getProgSmHi());
        Assert.assertEquals(progSm2, p.getProgSm2());
        Assert.assertEquals(progSm1, p.getProgSm1());
        Assert.assertEquals(progSmLo, p.getProgSmLo());
        Assert.assertEquals(progPortHi, p.getProgPortHi());
        Assert.assertEquals(progPortLo, p.getProgPortLo());
    }

    @Test
    public void sendPacketTest() throws MalformedArtnetPacketException {
        //enable programming, disable dhcp, do not return all parameters to default
        //program IP Address, program Subnet Mask, program Port
        byte command = (byte) 0b10000111;
        byte progIpHi = (byte)192;
        byte progIp2 = (byte)168;
        byte progIp1 = (byte)0;
        byte progIpLo = (byte)10;
        byte progSmHi = (byte)255;
        byte progSm2 = (byte)255;
        byte progSm1 = (byte)0;
        byte progSmLo = (byte)0;
        byte progPortHi = (byte) 0x19;
        byte progPortLo = (byte) 0x36;

        ArtIpProgPacket p = new ArtIpProgPacket(command, progIpHi, progIp2, progIp1, progIpLo, progSmHi, progSm2, progSm1, progSmLo, progPortHi, progPortLo);

        ArtnetController controller = new ArtnetController(false, false);
        controller.unicastPacket(p, new ArtnetNode(InetAddress.getLoopbackAddress(), ArtnetStyleCodes.ST_CONTROLLER, "loopback", "loopback"));
    }

    @After
    public void teardown() {

    }
}

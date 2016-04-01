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
import me.sschaeffner.jArtnet.packets.ArtIpProgReplyPacket;
import me.sschaeffner.jArtnet.packets.MalformedArtnetPacketException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
    }

    @Test
    public void sendPacketTest() throws MalformedArtnetPacketException {
        byte[] progIp = new byte[]{(byte)192, (byte)168, (byte)0, (byte)10};
        byte[] progSm = new byte[]{(byte)255, (byte)255, (byte)0, (byte)0};
        byte progPortHi = (byte) 0x19;
        byte progPortLo = (byte) 0x36;
        byte status = (byte) 0b01000000;

        ArtIpProgReplyPacket p = new ArtIpProgReplyPacket(progIp, progSm, progPortHi, progPortLo, status);

        ArtnetController controller = new ArtnetController(false, false);
        controller.unicastPacket(p, new ArtnetNode(InetAddress.getLoopbackAddress(), ArtnetStyleCodes.ST_CONTROLLER, "loopback", "loopback"));
    }

    @After
    public void teardown() {

    }

}

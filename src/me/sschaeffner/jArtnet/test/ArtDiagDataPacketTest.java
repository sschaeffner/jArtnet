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
import me.sschaeffner.jArtnet.packets.ArtDiagDataPacket;
import me.sschaeffner.jArtnet.packets.ArtnetPacket;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;

/**
 * @author sschaeffner
 */
public class ArtDiagDataPacketTest {
    @Before
    public void setup() {

    }

    @Test
    public void constructionTest() throws MalformedArtnetPacketException {
        byte priority = ArtNetPriorityCodes.DP_CRITICAL;
        byte[] data = ArtDiagDataPacket.stringToAsciiArrayNullTerminated("Important Message");
        byte lengthHi = (byte) (data.length >> 8);
        byte lengthLo = (byte) data.length;

        ArtDiagDataPacket pOrig = new ArtDiagDataPacket(priority, lengthHi, lengthLo, data);
        byte[] bytes = pOrig.getPacketBytes();

        ArtDiagDataPacket p = ArtDiagDataPacket.fromBytes(bytes);
        Assert.assertEquals(pOrig.getPriority(), p.getPriority());
        Assert.assertEquals(pOrig.getLengthHi(), p.getLengthHi());
        Assert.assertEquals(pOrig.getLengthLo(), p.getLengthLo());
        Assert.assertArrayEquals(pOrig.getData(), p.getData());
        Assert.assertEquals(pOrig.getMessageAsString(), p.getMessageAsString());
    }

    @Test
    public void constructionTest2() throws MalformedArtnetPacketException {
        byte priority = ArtNetPriorityCodes.DP_CRITICAL;
        byte[] data = ArtDiagDataPacket.stringToAsciiArrayNullTerminated("Important Message");
        byte lengthHi = (byte) (data.length >> 8);
        byte lengthLo = (byte) data.length;

        ArtDiagDataPacket pOrig = new ArtDiagDataPacket(priority, data);
        byte[] bytes = pOrig.getPacketBytes();

        ArtDiagDataPacket p = ArtDiagDataPacket.fromBytes(bytes);
        Assert.assertEquals(pOrig.getPriority(), p.getPriority());
        Assert.assertEquals(pOrig.getLengthHi(), p.getLengthHi());
        Assert.assertEquals(pOrig.getLengthLo(), p.getLengthLo());
        Assert.assertArrayEquals(pOrig.getData(), p.getData());
        Assert.assertEquals(pOrig.getMessageAsString(), p.getMessageAsString());
    }

    @Test
    public void sendPacketTest() throws MalformedArtnetPacketException {
        ArtDiagDataPacket p = new ArtDiagDataPacket(ArtNetPriorityCodes.DP_CRITICAL, "hello world");
        ArtnetController controller = new ArtnetController(false, false);
        controller.unicastPacket(p, new ArtnetNode(InetAddress.getLoopbackAddress(), ArtnetStyleCodes.ST_CONTROLLER, "loopback", "loopback"));
    }

    @Test
    public void opCodeRecognitionTest() throws MalformedArtnetPacketException {
        byte priority = ArtNetPriorityCodes.DP_CRITICAL;
        byte[] data = ArtDiagDataPacket.stringToAsciiArrayNullTerminated("Important Message");
        byte lengthHi = (byte) (data.length >> 8);
        byte lengthLo = (byte) data.length;

        ArtDiagDataPacket pOrig = new ArtDiagDataPacket(priority, lengthHi, lengthLo, data);
        byte[] bytes = pOrig.getPacketBytes();
        ArtnetPacket p = ArtnetOpCodes.fromBytes(bytes);

        if (!(p instanceof ArtDiagDataPacket)) {
            Assert.fail("ArtDiagDataPacket not recognized by ArtnetOpCodes");
        }
    }

    @After
    public void teardown() {

    }
}

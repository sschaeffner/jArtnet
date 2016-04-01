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
import me.sschaeffner.jArtnet.packets.ArtDmxPacket;
import me.sschaeffner.jArtnet.packets.MalformedArtnetPacketException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;

/**
 * @author sschaeffner
 */
public class ArtDmxPacketTest {

    @Before
    public void setup() {

    }

    @Test
    public void constructionTest() throws MalformedArtnetPacketException {
        byte[] data = new byte[512];
        for (int i = 0; i < 512; i++) {
            data[i] = (byte) (i % 256);
        }

        ArtDmxPacket pOrig = new ArtDmxPacket((byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0x02, (byte) 0x00, data);
        byte[] bytes = pOrig.getPacketBytes();
        ArtDmxPacket p = ArtDmxPacket.fromBytes(bytes);

        Assert.assertEquals(pOrig.getSequence(), p.getSequence());
        Assert.assertEquals(pOrig.getPhysical(), p.getPhysical());
        Assert.assertEquals(pOrig.getSubUni(), p.getSubUni());
        Assert.assertEquals(pOrig.getNet(), p.getNet());
        Assert.assertEquals(pOrig.getLengthHi(), p.getLengthHi());
        Assert.assertEquals(pOrig.getLength(), p.getLength());
        Assert.assertArrayEquals(pOrig.getData(), p.getData());
    }

    @Test
    public void automatedLengthTest() {
        ArtDmxPacket p = new ArtDmxPacket((byte) 0, (byte) 0, (byte) 0, (byte) 0, new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        Assert.assertEquals(0x00, p.getLengthHi() & 0xFF);
        Assert.assertEquals(0x10, p.getLength() & 0xFF);
    }

    @Test
    public void sendPacketTest() throws MalformedArtnetPacketException {
        byte[] data = new byte[512];
        for (int i = 0; i < 512; i++) {
            data[i] = (byte) (i % 256);
        }

        ArtDmxPacket pOrig = new ArtDmxPacket((byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0x02, (byte) 0x00, data);
        byte[] bytes = pOrig.getPacketBytes();
        ArtDmxPacket p = ArtDmxPacket.fromBytes(bytes);

        ArtnetController controller = new ArtnetController(false, false);
        controller.unicastPacket(p, new ArtnetNode(InetAddress.getLoopbackAddress(), ArtnetStyleCodes.ST_CONTROLLER, "loopback", "loopback"));
    }

    @After
    public void teardown() {

    }
}

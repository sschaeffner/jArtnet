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
import me.sschaeffner.jArtnet.packets.ArtAddressPacket;
import me.sschaeffner.jArtnet.packets.ArtPollPacket;
import me.sschaeffner.jArtnet.packets.ArtnetPacket;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;

/**
 * @author sschaeffner
 */
public class ArtPollPacketTest {

    @Test
    public void constructionTest() {
        ArtPollPacket p = new ArtPollPacket();
        byte[] b = p.getPacketBytes();

        //Art-Net packet code
        for (int i = 0; i < ArtnetPacket.ID.length; i++) {
            Assert.assertEquals(ArtnetPacket.ID[i], b[i]);
        }

        //opcode (0x2000)
        Assert.assertEquals(0, b[8]);
        Assert.assertEquals(32, b[9]);

        //lo version
        Assert.assertEquals(0, b[10]);
        //hi version
        Assert.assertEquals(14, b[11]);

        //talk to me (0b00000110)
        Assert.assertEquals(6, b[12]);

        //priority (0xE0)
        Assert.assertEquals(ArtNetPriorityCodes.DP_LOW, b[13] & 0xFF);

        System.out.println(p);
    }

    @Test
    public void constructionTest2() {
        ArtPollPacket p = new ArtPollPacket((byte) 0, ArtNetPriorityCodes.DP_LOW);
        byte[] b = p.getPacketBytes();

        //Art-Net package code
        for (int i = 0; i < ArtnetPacket.ID.length; i++) {
            Assert.assertEquals(ArtnetPacket.ID[i], b[i]);
        }

        //opcode (0x2000)
        Assert.assertEquals(0, b[8]);
        Assert.assertEquals(32, b[9]);

        //lo version
        Assert.assertEquals(0, b[10]);
        //hi version
        Assert.assertEquals(14, b[11]);

        //talk to me (0b00000000)
        Assert.assertEquals(0, b[12]);

        //priority (0x10)
        Assert.assertEquals(0x10, b[13] & 0xFF);
    }

    @Test
    public void bytesFromPacketTest() throws MalformedArtnetPacketException {
        ArtPollPacket pOrig = new ArtPollPacket();
        byte[] data = pOrig.getPacketBytes();
        ArtPollPacket p = ArtPollPacket.fromBytes(data);

        Assert.assertNotNull(p);
        Assert.assertEquals(pOrig.getTalkToMe(), p.getTalkToMe());
        Assert.assertEquals(pOrig.getPriority(), p.getPriority());
    }

    boolean received;
    @Test
    public void sendPacketTest() throws MalformedArtnetPacketException, IOException {
        ArtPollPacket p = new ArtPollPacket();
        ArtnetController controller = ArtnetControllerFactory.getTestingInstance();
        controller.unicastPacket(p, new ArtnetNode(InetAddress.getLoopbackAddress(), ArtnetStyleCodes.ST_CONTROLLER, "loopback", "loopback"));
    }

    @Test
    public void opCodeRecognitionTest() throws MalformedArtnetPacketException {
        ArtPollPacket pOrig = new ArtPollPacket();
        byte[] bytes = pOrig.getPacketBytes();

        ArtnetPacket p = ArtnetOpCodes.fromBytes(bytes);
        if (!(p instanceof ArtPollPacket)) {
            Assert.fail("ArtPollPacket not recognized by ArtnetOpCodes");
        }
    }
}

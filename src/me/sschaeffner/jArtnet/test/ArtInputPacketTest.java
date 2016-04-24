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
import me.sschaeffner.jArtnet.packets.ArtInputPacket;
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
public class ArtInputPacketTest {
    @Before
    public void setup() {

    }

    @Test
    public void constructionTest() throws MalformedArtnetPacketException {
        byte numPortsHi = 0;
        byte numPortsLo = 4;
        byte[] input = new byte[]{0, 1, 0, 1};

        ArtInputPacket pOrig = new ArtInputPacket(numPortsHi, numPortsLo, input);
        byte[] bytes = pOrig.getPacketBytes();
        ArtInputPacket p = ArtInputPacket.fromBytes(bytes);

        Assert.assertEquals(numPortsHi, p.getNumPortsHi());
        Assert.assertEquals(numPortsLo, p.getNumPortsLo());
        Assert.assertArrayEquals(input, p.getInput());

        System.out.println(p);
    }

    @Test
    public void constructionTest2() throws MalformedArtnetPacketException {
        byte numPortsHi = 0;
        byte numPortsLo = 4;
        byte[] input = new byte[]{0, 1, 0, 1};

        ArtInputPacket pOrig = new ArtInputPacket(false, true, false, true);
        byte[] bytes = pOrig.getPacketBytes();
        ArtInputPacket p = ArtInputPacket.fromBytes(bytes);

        Assert.assertEquals(numPortsHi, p.getNumPortsHi());
        Assert.assertEquals(numPortsLo, p.getNumPortsLo());
        Assert.assertArrayEquals(input, p.getInput());
    }

    @Test
    public void packetSendTest() throws MalformedArtnetPacketException, IOException {
        byte numPortsHi = 0;
        byte numPortsLo = 4;
        byte[] input = new byte[]{0, 1, 0, 1};

        ArtInputPacket p = new ArtInputPacket(numPortsHi, numPortsLo, input);

        ArtnetController controller = ArtnetControllerFactory.getTestingInstance();
        controller.unicastPacket(p, new ArtnetNode(InetAddress.getLoopbackAddress(), ArtnetStyleCodes.ST_CONTROLLER, "loopback", "loopback"));
    }

    @Test
    public void opCodeRecognitionTest() throws MalformedArtnetPacketException {
        byte numPortsHi = 0;
        byte numPortsLo = 4;
        byte[] input = new byte[]{0, 1, 0, 1};

        ArtInputPacket pOrig = new ArtInputPacket(numPortsHi, numPortsLo, input);
        byte[] bytes = pOrig.getPacketBytes();

        ArtnetPacket p = ArtnetOpCodes.fromBytes(bytes);

        if (!(p instanceof ArtInputPacket)) {
            Assert.fail("ArtInputPacket not recognized by ArtnetOpCodes");
        }
    }

    @After
    public void teardown() {

    }
}

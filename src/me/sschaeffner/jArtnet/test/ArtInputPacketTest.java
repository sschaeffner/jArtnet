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

    boolean received;
    @Test
    public void packetSendTest() throws MalformedArtnetPacketException, IOException {
        byte numPortsHi = 0;
        byte numPortsLo = 4;
        byte[] input = new byte[]{0, 1, 0, 1};

        ArtInputPacket p = new ArtInputPacket(numPortsHi, numPortsLo, input);

        ArtnetController controller = ArtnetControllerFactory.getTestingInstance();
        controller.addArtnetPacketListener(event -> {
            if (event.getReceivedPacket() instanceof ArtInputPacket) {
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

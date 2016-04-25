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
import me.sschaeffner.jArtnet.packets.ArtDiagDataPacket;
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
public class ArtDiagDataPacketTest {
    @Before
    public void setup() {

    }

    @Test
    public void constructionTest() throws MalformedArtnetPacketException {
        byte priority = ArtNetPriorityCodes.DP_CRITICAL;
        byte[] data = ArtnetPacket.asASCIIArrayNullTerminated("Important Message");
        byte lengthHi = (byte) (data.length >> 8);
        byte lengthLo = (byte) data.length;

        ArtDiagDataPacket pOrig = new ArtDiagDataPacket(priority, lengthHi, lengthLo, data);
        byte[] bytes = pOrig.getPacketBytes();

        ArtDiagDataPacket p = ArtDiagDataPacket.fromBytes(bytes);
        Assert.assertEquals(pOrig.getPriority(), p.getPriority());
        Assert.assertEquals(pOrig.getLengthHi(), p.getLengthHi());
        Assert.assertEquals(pOrig.getLengthLo(), p.getLengthLo());
        Assert.assertArrayEquals(pOrig.getData(), p.getData());
        Assert.assertEquals(pOrig.getDataString(), p.getDataString());

        System.out.println(p);
    }

    @Test
    public void constructionTest2() throws MalformedArtnetPacketException {
        byte priority = ArtNetPriorityCodes.DP_CRITICAL;
        byte[] data = ArtnetPacket.asASCIIArrayNullTerminated("Important Message");
        byte lengthHi = (byte) (data.length >> 8);
        byte lengthLo = (byte) data.length;

        ArtDiagDataPacket pOrig = new ArtDiagDataPacket(priority, data);
        byte[] bytes = pOrig.getPacketBytes();

        ArtDiagDataPacket p = ArtDiagDataPacket.fromBytes(bytes);
        Assert.assertEquals(pOrig.getPriority(), p.getPriority());
        Assert.assertEquals(pOrig.getLengthHi(), p.getLengthHi());
        Assert.assertEquals(pOrig.getLengthLo(), p.getLengthLo());
        Assert.assertArrayEquals(pOrig.getData(), p.getData());
        Assert.assertEquals(pOrig.getDataString(), p.getDataString());
    }

    boolean received;
    @Test
    public void sendPacketTest() throws MalformedArtnetPacketException, IOException {
        ArtDiagDataPacket p = new ArtDiagDataPacket(ArtNetPriorityCodes.DP_CRITICAL, "hello world");
        ArtnetController controller = ArtnetControllerFactory.getTestingInstance();
        controller.addArtnetPacketListener(event -> {
            if (event.getReceivedPacket() instanceof ArtDiagDataPacket) {
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
        byte priority = ArtNetPriorityCodes.DP_CRITICAL;
        byte[] data = ArtnetPacket.asASCIIArrayNullTerminated("Important Message");
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

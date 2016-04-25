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
import me.sschaeffner.jArtnet.packets.ArtDmxPacket;
import me.sschaeffner.jArtnet.packets.ArtNzsPacket;
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
public class ArtNzsPacketTest {

    @Before
    public void setup() {

    }

    @Test
    public void constructionTest() throws MalformedArtnetPacketException {
        byte sequence = (byte) 0x10;
        byte startCode = (byte) 0x01;
        byte subUni = (byte) 0x02;
        byte net = (byte) 0x03;
        byte lengthHi = (byte) 0x00;
        byte length = (byte) 0x05;
        byte[] data = new byte[]{(byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x05};

        ArtNzsPacket pOrig = new ArtNzsPacket(sequence, startCode, subUni, net, lengthHi, length, data);
        byte[] bytes = pOrig.getPacketBytes();
        ArtNzsPacket p = ArtNzsPacket.fromBytes(bytes);

        Assert.assertEquals(sequence, p.getSequence());
        Assert.assertEquals(startCode, p.getStartCode());
        Assert.assertEquals(subUni, p.getSubUni());
        Assert.assertEquals(net, p.getNet());
        Assert.assertEquals(lengthHi, p.getLengthHi());
        Assert.assertEquals(length, p.getLength());
        Assert.assertArrayEquals(data, p.getData());

        System.out.println(p);
    }

    @Test
    public void constructionTest2() throws MalformedArtnetPacketException {
        byte sequence = (byte) 0x10;
        byte startCode = (byte) 0x01;
        byte subUni = (byte) 0x02;
        byte net = (byte) 0x03;
        byte[] data = new byte[]{(byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x05};

        ArtNzsPacket pOrig = new ArtNzsPacket(sequence, startCode, subUni, net, data);
        byte[] bytes = pOrig.getPacketBytes();
        ArtNzsPacket p = ArtNzsPacket.fromBytes(bytes);

        Assert.assertEquals(sequence, p.getSequence());
        Assert.assertEquals(startCode, p.getStartCode());
        Assert.assertEquals(subUni, p.getSubUni());
        Assert.assertEquals(net, p.getNet());
        Assert.assertEquals(0, p.getLengthHi());
        Assert.assertEquals(5, p.getLength());
        Assert.assertArrayEquals(data, p.getData());
    }

    boolean received;
    @Test
    public void sendPacketTest() throws MalformedArtnetPacketException, IOException {
        byte sequence = (byte) 0x10;
        byte startCode = (byte) 0x01;
        byte subUni = (byte) 0x02;
        byte net = (byte) 0x03;
        byte lengthHi = (byte) 0x00;
        byte length = (byte) 0x05;
        byte[] data = new byte[]{(byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x05};

        ArtNzsPacket p = new ArtNzsPacket(sequence, startCode, subUni, net, lengthHi, length, data);
        ArtnetController controller = ArtnetControllerFactory.getTestingInstance();
        controller.addArtnetPacketListener(event -> {
            if (event.getReceivedPacket() instanceof ArtNzsPacket) {
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
    public void opCodeTest() throws MalformedArtnetPacketException {
        byte sequence = (byte) 0x10;
        byte startCode = (byte) 0x01;
        byte subUni = (byte) 0x02;
        byte net = (byte) 0x03;
        byte lengthHi = (byte) 0x00;
        byte length = (byte) 0x05;
        byte[] data = new byte[]{(byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x05};

        ArtNzsPacket pOrig = new ArtNzsPacket(sequence, startCode, subUni, net, lengthHi, length, data);

        byte[] bytes = pOrig.getPacketBytes();

        ArtnetPacket p = ArtnetOpCodes.fromBytes(bytes);

        if (!(p instanceof ArtNzsPacket)) {
            Assert.fail("ArtNzsPacket not recognized by ArtnetOpCodes");
        }
    }

    @After
    public void teardown() {

    }
}

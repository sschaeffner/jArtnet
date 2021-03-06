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
import me.sschaeffner.jArtnet.packets.ArtTriggerPacket;
import me.sschaeffner.jArtnet.packets.ArtnetPacket;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * @author sschaeffner
 */
public class ArtTriggerPacketTest {
    @Before
    public void setup() {

    }

    @Test
    public void constructionTest() throws MalformedArtnetPacketException {
        byte oemCodeLo = (byte) 0xff;
        byte oemCodeHi = (byte) 0xff;
        byte key = (byte) 0x00;//key ascii
        byte subkey = (byte) 'h';
        byte[] data = new byte[512];

        ArtTriggerPacket pOrig = new ArtTriggerPacket(oemCodeHi, oemCodeLo, key, subkey, data);
        byte[] bytes = pOrig.getPacketBytes();

        ArtTriggerPacket p = ArtTriggerPacket.fromBytes(bytes);

        Assert.assertEquals(oemCodeHi, p.getOemCodeHi());
        Assert.assertEquals(oemCodeLo, p.getOemCodeLo());
        Assert.assertEquals(key, p.getKey());
        Assert.assertEquals(subkey, p.getSubKey());
        Assert.assertArrayEquals(data, p.getData());
    }

    @Test
    public void constructionTest2() throws MalformedArtnetPacketException {
        byte oemCodeHi = (byte) 0xFF;
        byte oemCodeLo = (byte) 0xFF;
        byte key = (byte) 0x00;
        byte subKey = (byte) 'h';
        String data = "hello world";

        ArtTriggerPacket pOrig = new ArtTriggerPacket(oemCodeHi, oemCodeLo, key, subKey, data);
        byte[] bytes = pOrig.getPacketBytes();
        ArtTriggerPacket p = ArtTriggerPacket.fromBytes(bytes);

        Assert.assertEquals(oemCodeHi, p.getOemCodeHi());
        Assert.assertEquals(oemCodeLo, p.getOemCodeLo());
        Assert.assertEquals(key, p.getKey());
        Assert.assertEquals(subKey, p.getSubKey());
        Assert.assertEquals(data, p.getMessageAsString());

        System.out.println(p);
    }

    @Test
    public void constructionTest3() throws MalformedArtnetPacketException {
        byte oemCodeHi = (byte) 0xFF;
        byte oemCodeLo = (byte) 0x00;
        int oemCode = 0xFF00;
        byte key = (byte) 0x00;
        byte subKey = (byte) 'h';
        String data = "hello world";

        ArtTriggerPacket pOrig = new ArtTriggerPacket(oemCode, key, subKey, data);
        byte[] bytes = pOrig.getPacketBytes();
        ArtTriggerPacket p = ArtTriggerPacket.fromBytes(bytes);

        Assert.assertEquals(oemCodeHi, p.getOemCodeHi());
        Assert.assertEquals(oemCodeLo, p.getOemCodeLo());
        Assert.assertEquals(key, p.getKey());
        Assert.assertEquals(subKey, p.getSubKey());
        Assert.assertEquals(data, p.getMessageAsString());

    }

    boolean received;

    @Test
    public void sendPacketTest() throws MalformedArtnetPacketException, IOException {
        byte oemCodeLo = (byte) 0xff;
        byte oemCodeHi = (byte) 0xff;
        byte key = (byte) 0x00;//key ascii
        byte subkey = (byte) 'h';
        String data = "hello world";

        ArtTriggerPacket p = new ArtTriggerPacket(oemCodeHi, oemCodeLo, key, subkey, data);
        ArtnetController controller = ArtnetControllerFactory.getTestingInstance();
        controller.addArtnetPacketListener(event -> {
            if (event.getReceivedPacket() instanceof ArtTriggerPacket) {
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
        byte oemCodeLo = (byte) 0xff;
        byte oemCodeHi = (byte) 0xff;
        byte key = (byte) 0x00;//key ascii
        byte subkey = (byte) 'h';
        byte[] data = new byte[512];

        ArtTriggerPacket pOrig = new ArtTriggerPacket(oemCodeHi, oemCodeLo, key, subkey, data);
        byte[] bytes = pOrig.getPacketBytes();
        ArtnetPacket p = ArtnetOpCodes.fromBytes(bytes);

        if (!(p instanceof ArtTriggerPacket)) {
            Assert.fail("ArtTriggerPacket not recognized by ArtnetOpCodes");
        }
    }

    @After
    public void teardown() {

    }
}

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
import me.sschaeffner.jArtnet.packets.ArtCommandPacket;
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
public class ArtCommandPacketTest {

    @Before
    public void setup() {

    }

    @Test
    public void constructionTest() throws MalformedArtnetPacketException {
        byte estaManHi = (byte) 0x00;
        byte estaManLo = (byte) 0xFF;
        byte[] data = new byte[]{(byte)'S', (byte)'w', (byte)'o', (byte)'u', (byte)'t', (byte)'T', (byte)'e', (byte)'x', (byte)'t',
                (byte)'=', (byte)'P', (byte)'l', (byte)'a', (byte)'y', (byte)'b', (byte)'a', (byte)'c', (byte)'k', (byte)'&', (byte)0};
        byte lengthHi = (byte)(data.length >> 8);
        byte lengthLo = (byte)data.length;

        ArtCommandPacket pOrig = new ArtCommandPacket(estaManHi, estaManLo, lengthHi, lengthLo, data);
        byte[] bytes = pOrig.getPacketBytes();

        ArtCommandPacket p = ArtCommandPacket.fromBytes(bytes);

        Assert.assertEquals(estaManHi, p.getEstaManHi());
        Assert.assertEquals(estaManLo, p.getEstaManLo());
        Assert.assertEquals(lengthHi, p.getLengthHi());
        Assert.assertEquals(lengthLo, p.getLengthLo());
        Assert.assertArrayEquals(data, p.getData());
    }

    @Test
    public void constructionTest2() throws MalformedArtnetPacketException {
        byte estaManHi = (byte) 0x00;
        byte estaManLo = (byte) 0xFF;
        String data = "SwoutText=Playback&";
        ArtCommandPacket pOrig = new ArtCommandPacket(estaManHi, estaManLo, data);
        byte[] bytes = pOrig.getPacketBytes();

        ArtCommandPacket p = ArtCommandPacket.fromBytes(bytes);

        Assert.assertEquals(estaManHi, p.getEstaManHi());
        Assert.assertEquals(estaManLo, p.getEstaManLo());
        Assert.assertEquals(data, p.getMessageAsString());
    }

    @Test
    public void sendPacketTest() throws MalformedArtnetPacketException, IOException {
        byte estaManHi = (byte) 0x00;
        byte estaManLo = (byte) 0xFF;
        byte[] data = new byte[]{(byte)'S', (byte)'w', (byte)'o', (byte)'u', (byte)'t', (byte)'T', (byte)'e', (byte)'x', (byte)'t',
                (byte)'=', (byte)'P', (byte)'l', (byte)'a', (byte)'y', (byte)'b', (byte)'a', (byte)'c', (byte)'k', (byte)'&', (byte)0};
        byte lengthHi = (byte)(data.length >> 8);
        byte lengthLo = (byte)data.length;

        ArtCommandPacket p = new ArtCommandPacket(estaManHi, estaManLo, lengthHi, lengthLo, data);
        ArtnetController controller = ArtnetController.getInstance(false, false);
        controller.unicastPacket(p, new ArtnetNode(InetAddress.getLoopbackAddress(), ArtnetStyleCodes.ST_CONTROLLER, "loopback", "loopback"));
    }

    @Test
    public void sendPacketTest2() throws MalformedArtnetPacketException, IOException {
        byte estaManHi = (byte) 0x00;
        byte estaManLo = (byte) 0xFF;
        String data = "SwoutText=Playback&";
        ArtCommandPacket p = new ArtCommandPacket(estaManHi, estaManLo, data);
        ArtnetController controller = ArtnetController.getInstance(false, false);
        controller.unicastPacket(p, new ArtnetNode(InetAddress.getLoopbackAddress(), ArtnetStyleCodes.ST_CONTROLLER, "loopback", "loopback"));
    }

    @Test
    public void opCodeRecognitionTest() throws MalformedArtnetPacketException {
        byte estaManHi = (byte) 0x00;
        byte estaManLo = (byte) 0xFF;
        byte[] data = new byte[]{(byte)'S', (byte)'w', (byte)'o', (byte)'t', (byte)'T', (byte)'e', (byte)'x', (byte)'t',
                (byte)'=', (byte)'P', (byte)'l', (byte)'a', (byte)'y', (byte)'b', (byte)'a', (byte)'c', (byte)'k', (byte)'&', (byte)0};
        byte lengthHi = (byte)(data.length >> 8);
        byte lengthLo = (byte)data.length;

        ArtCommandPacket pOrig = new ArtCommandPacket(estaManHi, estaManLo, lengthHi, lengthLo, data);
        byte[] bytes = pOrig.getPacketBytes();
        ArtnetPacket p = ArtnetOpCodes.fromBytes(bytes);

        if (!(p instanceof ArtCommandPacket)) {
            Assert.fail("ArtCommandPacket not recognized by ArtnetOpCodes");
        }
    }

    @After
    public void teardown() {

    }
}

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
import me.sschaeffner.jArtnet.packets.ArtAddressPacket;
import me.sschaeffner.jArtnet.packets.ArtCommandPacket;
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
public class ArtAddressPacketTest {

    @Before
    public void setup() {

    }

    @Test
    public void constructionTest() throws MalformedArtnetPacketException {
        byte netSwitch = (byte)0x42;
        byte[] shortName = new byte[]{(byte)'h', (byte)'e', (byte)'l', (byte)'l', (byte)'o', (byte)' ', (byte)'w', (byte)'o',
                (byte)'r', (byte)'l', (byte)'d', (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
        byte[] longName = new byte[64];
        System.arraycopy(shortName, 0, longName, 0, 18);
        byte[] swIn = new byte[]{(byte)0x0A, (byte)0x0B, (byte)0x0C, (byte)0x0D};
        byte[] swOut = new byte[]{(byte)0x0E, (byte)0x0F, (byte)0x01, (byte)0x02};
        byte subSwitch = (byte) 0x21;
        byte swVideo = (byte) 0x00;
        byte command = (byte) 0x04;//AcLedLocate: flash front panel indicators rapidly

        ArtAddressPacket pOrig = new ArtAddressPacket(netSwitch, shortName, longName, swIn, swOut, subSwitch, swVideo, command);
        byte[] bytes = pOrig.getPacketBytes();
        ArtAddressPacket p = ArtAddressPacket.fromBytes(bytes);

        Assert.assertEquals(netSwitch, p.getNetSwitch());
        Assert.assertArrayEquals(shortName, p.getShortName());
        Assert.assertArrayEquals(longName, p.getLongName());
        Assert.assertArrayEquals(swIn, p.getSwIn());
        Assert.assertArrayEquals(swOut, p.getSwOut());
        Assert.assertEquals(subSwitch, p.getSubSwitch());
        Assert.assertEquals(swVideo, p.getSwVideo());
        Assert.assertEquals(command, p.getCommand());
    }

    @Test
    public void sendPacketTest() throws MalformedArtnetPacketException, IOException {
        byte netSwitch = (byte)0x42;
        byte[] shortName = new byte[]{(byte)'h', (byte)'e', (byte)'l', (byte)'l', (byte)'o', (byte)' ', (byte)'w', (byte)'o',
                (byte)'r', (byte)'l', (byte)'d', (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
        byte[] longName = new byte[64];
        System.arraycopy(shortName, 0, longName, 0, 18);
        byte[] swIn = new byte[]{(byte)0x0A, (byte)0x0B, (byte)0x0C, (byte)0x0D};
        byte[] swOut = new byte[]{(byte)0x0E, (byte)0x0F, (byte)0x01, (byte)0x02};
        byte subSwitch = (byte) 0x21;
        byte swVideo = (byte) 0x00;
        byte command = (byte) 0x04;//AcLedLocate: flash front panel indicators rapidly

        ArtAddressPacket p = new ArtAddressPacket(netSwitch, shortName, longName, swIn, swOut, subSwitch, swVideo, command);
        ArtnetController controller = ArtnetControllerFactory.getTestingInstance();
        controller.unicastPacket(p, new ArtnetNode(InetAddress.getLoopbackAddress(), ArtnetStyleCodes.ST_CONTROLLER, "loopback", "loopback"));
    }

    @Test
    public void opCodeRecognitionTest() throws MalformedArtnetPacketException {
        byte netSwitch = (byte)0x42;
        byte[] shortName = new byte[]{(byte)'h', (byte)'e', (byte)'l', (byte)'l', (byte)'o', (byte)' ', (byte)'w', (byte)'o',
                (byte)'r', (byte)'l', (byte)'d', (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
        byte[] longName = new byte[64];
        System.arraycopy(shortName, 0, longName, 0, 18);
        byte[] swIn = new byte[]{(byte)0x0A, (byte)0x0B, (byte)0x0C, (byte)0x0D};
        byte[] swOut = new byte[]{(byte)0x0E, (byte)0x0F, (byte)0x01, (byte)0x02};
        byte subSwitch = (byte) 0x21;
        byte swVideo = (byte) 0x00;
        byte command = (byte) 0x04;//AcLedLocate: flash front panel indicators rapidly

        ArtAddressPacket pOrig = new ArtAddressPacket(netSwitch, shortName, longName, swIn, swOut, subSwitch, swVideo, command);
        byte[] bytes = pOrig.getPacketBytes();
        ArtnetPacket p = ArtnetOpCodes.fromBytes(bytes);

        if (!(p instanceof ArtAddressPacket)) {
            Assert.fail("ArtAddressPacket not recognized by ArtnetOpCodes");
        }
    }

    @After
    public void teardown() {

    }
}

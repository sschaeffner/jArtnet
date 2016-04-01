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
import me.sschaeffner.jArtnet.MalformedArtnetPacketException;
import me.sschaeffner.jArtnet.packets.ArtTimeCodePacket;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;

/**
 * @author sschaeffner
 */
public class ArtTimeCodePacketTest {

    @Before
    public void setup() {

    }

    @Test
    public void constructionTest() throws MalformedArtnetPacketException {
        byte frames = 1;
        byte seconds = 2;
        byte minutes = 3;
        byte hours = 4;
        byte type = (byte)0b0010;

        ArtTimeCodePacket pOrig = new ArtTimeCodePacket(frames, seconds, minutes, hours, type);
        byte[] bytes = pOrig.getPacketBytes();
        ArtTimeCodePacket p = ArtTimeCodePacket.fromBytes(bytes);

        Assert.assertEquals(frames, p.getFrames());
        Assert.assertEquals(seconds, p.getSeconds());
        Assert.assertEquals(minutes, p.getMinutes());
        Assert.assertEquals(hours, p.getHours());
        Assert.assertEquals(type, p.getType());
    }

    @Test
    public void sendPacketTest() throws MalformedArtnetPacketException {
        byte frames = 1;
        byte seconds = 2;
        byte minutes = 3;
        byte hours = 4;
        byte type = (byte)0b0010;

        ArtTimeCodePacket p = new ArtTimeCodePacket(frames, seconds, minutes, hours, type);
        ArtnetController controller = new ArtnetController(false, false);
        controller.unicastPacket(p, new ArtnetNode(InetAddress.getLoopbackAddress(), ArtnetStyleCodes.ST_CONTROLLER, "loopback", "loopback"));
    }

    @After
    public void teardown() {

    }
}

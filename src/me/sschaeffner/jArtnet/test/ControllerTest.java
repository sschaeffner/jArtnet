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
import me.sschaeffner.jArtnet.packets.ArtPollPacket;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;

/**
 * @author sschaeffner
 */
public class ControllerTest {

    ArtnetController controller;

    @Before
    public void setup() throws IOException {
        controller = ArtnetControllerFactory.getTestingInstance();
    }

    @Test
    public void packetSendTest() throws MalformedArtnetPacketException, IOException {
        ArtPollPacket pollPacket = new ArtPollPacket();
        controller.unicastPacket(pollPacket, new ArtnetNode(InetAddress.getLoopbackAddress(), ArtnetStyleCodes.ST_CONTROLLER, "loopback", "loopback"));
    }

    @After
    public void teardown() {
        controller.closeSocket();
    }
}

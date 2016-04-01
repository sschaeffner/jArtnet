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
import me.sschaeffner.jArtnet.ArtnetNodeReportCodes;
import me.sschaeffner.jArtnet.ArtnetStyleCodes;
import me.sschaeffner.jArtnet.packets.ArtPollReplyPacket;
import me.sschaeffner.jArtnet.packets.MalformedArtnetPacketException;
import org.junit.Assert;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author sschaeffner
 */
public class ArtPollReplyPacketTest {

    @Test
    public void constructionTest() throws UnknownHostException, MalformedArtnetPacketException {
        ArtPollReplyPacket p = new ArtPollReplyPacket(InetAddress.getLocalHost(), (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r'}, new byte[64], new byte[64], (byte)0, (byte)0, new byte[4], new byte[4], new byte[4], new byte[4], new byte[4], (byte)0, (byte)0, (byte)1, ArtnetStyleCodes.ST_CONTROLLER, new byte[6], new byte[4], (byte)0, (byte)255);
        Assert.assertNotNull(p);

        byte[] data = p.getPacketBytes();
        Assert.assertEquals(239, data.length);
    }

    @Test
    public void constructionTest2() throws UnknownHostException, MalformedArtnetPacketException {
        ArtPollReplyPacket pOrig = new ArtPollReplyPacket(InetAddress.getByAddress(InetAddress.getLocalHost().getAddress()), (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r'}, new byte[64], new byte[64], (byte)0, (byte)0, new byte[4], new byte[4], new byte[4], new byte[4], new byte[4], (byte)0, (byte)0, (byte)1, ArtnetStyleCodes.ST_CONTROLLER, new byte[6], new byte[4], (byte)0, (byte)255);

        byte[] data = pOrig.getPacketBytes();
        ArtPollReplyPacket p = ArtPollReplyPacket.fromBytes(data);

        Assert.assertEquals(pOrig.getAddress(), p.getAddress());
        Assert.assertEquals(pOrig.getBindIndex(), p.getBindIndex());
        Assert.assertEquals(pOrig.getStatus2(), p.getStatus2());
    }

    @Test
    public void constructionTest3() throws MalformedArtnetPacketException {
        InetAddress address = InetAddress.getLoopbackAddress();
        byte versInfoH = (byte)0x1;
        byte versInfoL = (byte)0x2;
        byte netSwitch = (byte)0x1;
        byte subSwitch = (byte)0x2;
        byte oemHi = (byte)0x0;
        byte oem = (byte)0xff;
        byte ubeaVersion = (byte)0x0;
        byte status1 = (byte)0b11010000;
        byte estaManLo = (byte) 0xff;
        byte estaManHi = (byte) 0x00;
        byte[] shortName = new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', (byte)0x0};//j = 10
        byte[] longName = new byte[64];
        for (int i = 'a'; i < 'z'; i++) longName[i - 0x61] = (byte) i;

        byte[] nodeReport = new byte[64];
        nodeReport[0] = '#';
        byte[] reportCodeAscii = ArtnetNodeReportCodes.toASCIIByteArray(ArtnetNodeReportCodes.RC_DEBUG);
        nodeReport[1] = reportCodeAscii[0];
        nodeReport[2] = reportCodeAscii[1];
        nodeReport[3] = reportCodeAscii[2];
        nodeReport[4] = reportCodeAscii[3];
        nodeReport[5] = ' ';
        nodeReport[6] = '0';
        nodeReport[7] = ' ';
        nodeReport[8] = 's';
        nodeReport[9] = 't';
        nodeReport[10] = 'a';
        nodeReport[11] = 't';
        nodeReport[12] = 'u';
        nodeReport[13] = 's';

        byte numPortsHi = 0x0;
        byte numPortsLo = 4;
        byte[] portTypes = new byte[]{(byte) 0, (byte) 0, (byte) 5, (byte) 5};
        byte[] goodInput = new byte[]{(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80};
        byte[] goodOutput = new byte[]{(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80};
        byte[] swIn = new byte[]{(byte) 0x1, (byte) 0x2, (byte) 0x3, (byte) 0x4};
        byte[] swOut = new byte[]{(byte) 0x1, (byte) 0x2, (byte) 0x3, (byte) 0x4};
        byte swVideo = 0x0;
        byte swMacro = 0x0;
        byte swRemote = 0x0;
        byte style = ArtnetStyleCodes.ST_CONTROLLER;
        byte[] mac = new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
        byte[] bindIp = InetAddress.getLoopbackAddress().getAddress();
        byte bindIndex = 1;
        byte status2 = (byte)0b1100;

        ArtPollReplyPacket pOrig = new ArtPollReplyPacket(address, versInfoH, versInfoL, netSwitch, subSwitch, oemHi,
                oem, ubeaVersion, status1, estaManLo, estaManHi, shortName, longName, nodeReport, numPortsHi,
                numPortsLo, portTypes, goodInput, goodOutput, swIn, swOut, swVideo, swMacro, swRemote, style,
                mac, bindIp, bindIndex, status2);
        byte[] bytes = pOrig.getPacketBytes();
        ArtPollReplyPacket p = ArtPollReplyPacket.fromBytes(bytes);

        Assert.assertEquals(address, p.getAddress());
        Assert.assertEquals(versInfoH, p.getVersInfoH());
        Assert.assertEquals(versInfoL, p.getVersInfoL());
        Assert.assertEquals(netSwitch, p.getNetSwitch());
        Assert.assertEquals(subSwitch, p.getSubSwitch());
        Assert.assertEquals(oemHi, p.getOemHi());
        Assert.assertEquals(oem, p.getOem());
        Assert.assertEquals(ubeaVersion, p.getUbeaVersion());
        Assert.assertEquals(status1, p.getStatus1());
        Assert.assertEquals(estaManLo, p.getEstaManLo());
        Assert.assertEquals(estaManHi, p.getEstaManHi());
        Assert.assertArrayEquals(shortName, p.getShortName());
        Assert.assertArrayEquals(longName, p.getLongName());
        Assert.assertArrayEquals(nodeReport, p.getNodeReport());
        Assert.assertEquals(numPortsHi, p.getNumPortsHi());
        Assert.assertEquals(numPortsLo, p.getNumPortsLo());
        Assert.assertArrayEquals(portTypes, p.getPortTypes());
        Assert.assertArrayEquals(goodInput, p.getGoodInput());
        Assert.assertArrayEquals(goodOutput, p.getGoodOutput());
        Assert.assertArrayEquals(swIn, p.getSwIn());
        Assert.assertArrayEquals(swOut, p.getSwOut());
        Assert.assertEquals(swVideo, p.getSwVideo());
        Assert.assertEquals(swMacro, p.getSwMacro());
        Assert.assertEquals(style, p.getStyle());
        Assert.assertArrayEquals(mac, p.getMac());
        Assert.assertArrayEquals(bindIp, p.getBindIp());
        Assert.assertEquals(bindIndex, p.getBindIndex());
        Assert.assertEquals(status2, p.getStatus2());
    }

    @Test
    public void sendPacketTest() throws MalformedArtnetPacketException {
        InetAddress address = InetAddress.getLoopbackAddress();
        byte versInfoH = (byte)0x1;
        byte versInfoL = (byte)0x2;
        byte netSwitch = (byte)0x1;
        byte subSwitch = (byte)0x2;
        byte oemHi = (byte)0x0;
        byte oem = (byte)0xff;
        byte ubeaVersion = (byte)0x0;
        byte status1 = (byte)0b11010000;
        byte estaManLo = (byte) 0xff;
        byte estaManHi = (byte) 0x00;
        byte[] shortName = new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', (byte)0x0};//j = 10
        byte[] longName = new byte[64];
        for (int i = 'a'; i < 'z'; i++) longName[i - 0x61] = (byte) i;

        byte[] nodeReport = new byte[64];
        nodeReport[0] = '#';
        byte[] reportCodeAscii = ArtnetNodeReportCodes.toASCIIByteArray(ArtnetNodeReportCodes.RC_DEBUG);
        nodeReport[1] = reportCodeAscii[0];
        nodeReport[2] = reportCodeAscii[1];
        nodeReport[3] = reportCodeAscii[2];
        nodeReport[4] = reportCodeAscii[3];
        nodeReport[5] = ' ';
        nodeReport[6] = '0';
        nodeReport[7] = ' ';
        nodeReport[8] = 's';
        nodeReport[9] = 't';
        nodeReport[10] = 'a';
        nodeReport[11] = 't';
        nodeReport[12] = 'u';
        nodeReport[13] = 's';

        byte numPortsHi = 0x0;
        byte numPortsLo = 4;
        byte[] portTypes = new byte[]{(byte) 0, (byte) 0, (byte) 5, (byte) 5};
        byte[] goodInput = new byte[]{(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80};
        byte[] goodOutput = new byte[]{(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80};
        byte[] swIn = new byte[]{(byte) 0x1, (byte) 0x2, (byte) 0x3, (byte) 0x4};
        byte[] swOut = new byte[]{(byte) 0x1, (byte) 0x2, (byte) 0x3, (byte) 0x4};
        byte swVideo = 0x0;
        byte swMacro = 0x0;
        byte swRemote = 0x0;
        byte style = ArtnetStyleCodes.ST_CONTROLLER;
        byte[] mac = new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
        byte[] bindIp = InetAddress.getLoopbackAddress().getAddress();
        byte bindIndex = 1;
        byte status2 = (byte)0b1100;

        ArtPollReplyPacket p = new ArtPollReplyPacket(address, versInfoH, versInfoL, netSwitch, subSwitch, oemHi,
                oem, ubeaVersion, status1, estaManLo, estaManHi, shortName, longName, nodeReport, numPortsHi,
                numPortsLo, portTypes, goodInput, goodOutput, swIn, swOut, swVideo, swMacro, swRemote, style,
                mac, bindIp, bindIndex, status2);
        ArtnetController controller = new ArtnetController(false, false);
        controller.unicastPacket(p, new ArtnetNode(InetAddress.getLoopbackAddress(), ArtnetStyleCodes.ST_CONTROLLER, "loopback", "loopback"));
    }
}

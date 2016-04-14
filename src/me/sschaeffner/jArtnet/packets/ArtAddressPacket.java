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
package me.sschaeffner.jArtnet.packets;

import me.sschaeffner.jArtnet.ArtnetOpCodes;
import me.sschaeffner.jArtnet.MalformedArtnetPacketException;

/**
 * @author sschaeffner
 */
public class ArtAddressPacket extends ArtnetPacket {

    private final byte netSwitch;
    private final byte[] shortName;
    private final byte[] longName;
    private final byte[] swIn;
    private final byte[] swOut;
    private final byte subSwitch;
    private final byte swVideo;//reserved
    private final byte command;

    /**
     * Constructs a new instance of this class.
     *
     * @param netSwitch Bits 14-8 of the 15 bit Port-Address are encoded into the bottom 7 bits of this field
     * @param shortName short name of the node as a null terminated ASCII array
     * @param longName  long name of the node as a null terminated ASCII array
     * @param swIn      Bits 3-0 of the 15 bit Port-Address for a given input port are encoded into the bottom 4 bits of this field
     * @param swOut     Bits 3-0 of the 15 bit Port-Address for a given output port are encoded into the bottom 4 bits of this field
     * @param subSwitch Bits 7-4 of the 15 bit Port-Address are encoded into the bottom 4 bits of this field
     * @param swVideo   reserved
     * @param command   node configuration command
     * @throws MalformedArtnetPacketException
     */
    public ArtAddressPacket(byte netSwitch, byte[] shortName, byte[] longName, byte[] swIn, byte[] swOut,
                            byte subSwitch, byte swVideo, byte command) throws MalformedArtnetPacketException {
        this.netSwitch = netSwitch;

        if (shortName.length != 18) throw new MalformedArtnetPacketException("cannot construct ArtAddressPacket: shortName has to be 18 bytes long");
        this.shortName = shortName;

        if (longName.length != 64) throw new MalformedArtnetPacketException("cannot construct ArtAddressPacket: longName has to be 64 bytes long");
        this.longName = longName;

        if (swIn.length != 4) throw new MalformedArtnetPacketException("cannot construct ArtAddressPacket: swIn has to be 4 bytes long");
        this.swIn = swIn;

        if (swOut.length != 4) throw new MalformedArtnetPacketException("cannot construct ArtAddressPacket: swOut has to be 4 bytes long");
        this.swOut = swOut;

        this.subSwitch = subSwitch;
        this.swVideo = swVideo;
        this.command = command;
    }

    @Override
    public byte[] getPacketBytes() throws MalformedArtnetPacketException {
        int byteArrayLength = ArtnetPacket.ID.length + 2 + 1+1 + 1 + 1 + 18 + 64 + 4 + 4 + 1 + 1 + 1;
        byte[] bytes = new byte[byteArrayLength];

        //Art-Net package ID
        System.arraycopy(ArtnetPacket.ID, 0, bytes, 0, ArtnetPacket.ID.length);

        //opcode
        byte[] opCode = ArtnetOpCodes.toByteArray(ArtnetOpCodes.OP_ADDRESS);
        System.arraycopy(opCode, 0, bytes, ArtnetPacket.ID.length, 2);

        //protocol version
        bytes[10] = protVerHi;
        bytes[11] = protVerLo;

        bytes[12] = this.netSwitch;
        bytes[13] = 0;//Filler 2

        System.arraycopy(this.shortName, 0, bytes, 14, 18);//32
        System.arraycopy(this.longName, 0, bytes, 32, 64);//96

        System.arraycopy(this.swIn, 0, bytes, 96, 4);//100
        System.arraycopy(this.swOut, 0, bytes, 100, 4);//104

        bytes[104] = this.subSwitch;
        bytes[105] = this.swVideo;
        bytes[106] = this.command;

        return bytes;
    }

    public static ArtAddressPacket fromBytes(byte[] bytes) throws MalformedArtnetPacketException {
        //check for minimum length
        int byteArrayLength = ArtnetPacket.ID.length + 2 + 1+1 + 1 + 1 + 18 + 64 + 4 + 4 + 1 + 1 + 1;
        if (bytes.length != byteArrayLength) {
            throw new MalformedArtnetPacketException("cannot construct ArtAddressPacket from bytes: bytes length not compatible");
        }

        //check for correct opcode
        byte[] opCode = ArtnetOpCodes.toByteArray(ArtnetOpCodes.OP_ADDRESS);

        byte rOpCodeLo = bytes[8];
        byte rOpCodeHi = bytes[9];
        if (rOpCodeLo != opCode[0] || rOpCodeHi != opCode[1]) {
            throw new MalformedArtnetPacketException("cannot construct ArtAddressPacket from data: wrong opcode");
        }

        //check protocol version
        byte rProtVerHi = bytes[10];
        byte rProtVerLo = bytes[11];
        if (rProtVerHi < protVerHi || rProtVerLo < protVerLo) {
            throw new MalformedArtnetPacketException("cannot construct ArtAddressPacket from data: protVer not compatible");
        }

        byte rNetSwitch = bytes[12];

        byte[] rShortName = new byte[18];
        System.arraycopy(bytes, 14, rShortName, 0, 18);

        byte[] rLongName = new byte[64];
        System.arraycopy(bytes, 32, rLongName, 0, 64);

        byte[] rSwIn = new byte[4];
        System.arraycopy(bytes, 96, rSwIn, 0, 4);

        byte[] rSwOut = new byte[4];
        System.arraycopy(bytes, 100, rSwOut, 0, 4);

        byte rSubSwitch = bytes[104];
        byte rSwVideo = bytes[105];
        byte rCommand = bytes[106];

        return new ArtAddressPacket(rNetSwitch, rShortName, rLongName, rSwIn, rSwOut, rSubSwitch, rSwVideo, rCommand);
    }

    public byte getNetSwitch() {
        return netSwitch;
    }

    public byte[] getShortName() {
        return shortName;
    }

    public byte[] getLongName() {
        return longName;
    }

    public byte[] getSwIn() {
        return swIn;
    }

    public byte[] getSwOut() {
        return swOut;
    }

    public byte getSubSwitch() {
        return subSwitch;
    }

    public byte getSwVideo() {
        return swVideo;
    }

    public byte getCommand() {
        return command;
    }
}

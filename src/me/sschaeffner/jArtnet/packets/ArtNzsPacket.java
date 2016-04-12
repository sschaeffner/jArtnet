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
public class ArtNzsPacket extends ArtnetPacket {

    private final byte sequence;
    private final byte startCode;
    private final byte subUni;
    private final byte net;
    private final byte lengthHi;
    private final byte length;
    private final byte[] data;

    public ArtNzsPacket(byte sequence, byte startCode, byte subUni, byte net, byte lengthHi, byte length, byte[] data) throws MalformedArtnetPacketException {
        this.sequence = sequence;
        this.startCode = startCode;
        this.subUni = subUni;
        this.net = net;
        this.lengthHi = lengthHi;
        this.length = length;
        this.data = data;
        if (data.length > 512) {
            throw new MalformedArtnetPacketException("Cannot construct ArtDmxPacket: data too long");
        }
    }

    public ArtNzsPacket(byte sequence, byte startCode, byte subUni, byte net, byte[] data) throws MalformedArtnetPacketException {
        this.sequence = sequence;
        this.startCode = startCode;
        this.subUni = subUni;
        this.net = net;
        this.lengthHi = (byte) (data.length >> 8);
        this.length = (byte) data.length;
        this.data = data;
        if (data.length > 512) {
            throw new MalformedArtnetPacketException("Cannot construct ArtDmxPacket: data too long");
        }
    }

    @Override
    public byte[] getPacketBytes() throws MalformedArtnetPacketException {
        int length = (this.lengthHi << 8) + this.length;

        int byteArrayLength = ArtnetPacket.ID.length + 2 + 1+1 + 1 + 1 + 1 + 1 + 1+1 + length;
        byte[] bytes = new byte[byteArrayLength];

        //Art-Net package ID
        System.arraycopy(ArtnetPacket.ID, 0, bytes, 0, ArtnetPacket.ID.length);

        //opcode
        byte[] opCode = ArtnetOpCodes.toByteArray(ArtnetOpCodes.OP_NZS);
        System.arraycopy(opCode, 0, bytes, ArtnetPacket.ID.length, 2);

        bytes[10] = protVerHi;
        bytes[11] = protVerLo;

        bytes[12] = this.sequence;
        bytes[13] = this.startCode;
        bytes[14] = this.subUni;
        bytes[15] = this.net;
        bytes[16] = this.lengthHi;
        bytes[17] = this.length;

        if (bytes.length >= length + 18) {
            System.arraycopy(data, 0, bytes, 18, length);
        } else {
            throw new MalformedArtnetPacketException("cannot get packet bytes for ArtNzsPacket: not enough data for length available");
        }

        return bytes;
    }

    public static ArtNzsPacket fromBytes(byte[] bytes) throws MalformedArtnetPacketException {
        //check for minimum length
        int byteArrayLength = ArtnetPacket.ID.length + 2 + 1 + 1 + 1 + 1 + 1+1 + 1;
        if (bytes.length < byteArrayLength) {
            throw new MalformedArtnetPacketException("cannot construct ArtNzsPacket from bytes: bytes length not compatible");
        }

        //check for correct opcode
        byte[] opCode = ArtnetOpCodes.toByteArray(ArtnetOpCodes.OP_NZS);

        byte rOpCodeLo = bytes[8];
        byte rOpCodeHi = bytes[9];
        if (rOpCodeLo != opCode[0] || rOpCodeHi != opCode[1]) {
            throw new MalformedArtnetPacketException("cannot construct ArtNzsPacket from data: wrong opcode");
        }

        //check protocol version
        byte rProtVerHi = bytes[10];
        byte rProtVerLo = bytes[11];
        if (rProtVerHi < protVerHi || rProtVerLo < protVerLo) {
            throw new MalformedArtnetPacketException("cannot construct ArtNzsPacket from data: protVer not compatible");
        }

        //read information
        byte rSequence = bytes[12];

        byte rStartCode = bytes[13];

        byte rSubUni = bytes[14];
        byte rNet = bytes[15];

        byte rLengthHi = bytes[16];
        byte rLength = bytes[17];

        int lengthI = (rLengthHi << 8) + rLength;

        byte[] data = new byte[lengthI];
        if (bytes.length >= lengthI + 18) {
            System.arraycopy(bytes, 18, data, 0, lengthI);
        } else {
            throw new MalformedArtnetPacketException("cannot construct ArtNzsPacket from bytes: data too short (is " + (bytes.length - 18)
                    + "; should be " + lengthI + ")");
        }

        return new ArtNzsPacket(rSequence, rStartCode, rSubUni, rNet, rLengthHi, rLength, data);
    }

    public byte getSequence() {
        return sequence;
    }

    public byte getStartCode() {
        return startCode;
    }

    public byte getSubUni() {
        return subUni;
    }

    public byte getNet() {
        return net;
    }

    public byte getLengthHi() {
        return lengthHi;
    }

    public byte getLength() {
        return length;
    }

    public byte[] getData() {
        return data;
    }
}

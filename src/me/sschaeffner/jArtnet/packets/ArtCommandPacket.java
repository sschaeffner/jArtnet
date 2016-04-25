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
package me.sschaeffner.jArtnet.packets;

import me.sschaeffner.jArtnet.ArtnetOpCodes;
import me.sschaeffner.jArtnet.MalformedArtnetPacketException;

import java.util.Arrays;

/**
 * An implementation of the ArtCommand packet as defined by the Art-Net standard.
 *
 * @author sschaeffner
 */
public class ArtCommandPacket extends ArtnetPacket {

    private final byte estaManHi;
    private final byte estaManLo;
    private final byte lengthHi;
    private final byte lengthLo;
    private final byte[] data;

    /**
     * Constructs a new instance of this class.
     *
     * @param estaManHi high byte of the ESTA manufacturer code
     * @param estaManLo low byte of the ESTA manufacturer code
     * @param lengthHi  high byte of the length of the data array
     * @param lengthLo  low byte of the length of the data array
     * @param data      text command as a null terminated ASCII byte array
     * @throws MalformedArtnetPacketException when data is too long
     */
    public ArtCommandPacket(byte estaManHi, byte estaManLo, byte lengthHi, byte lengthLo, byte[] data) throws MalformedArtnetPacketException {
        this.estaManHi = estaManHi;
        this.estaManLo = estaManLo;
        this.lengthHi = lengthHi;
        this.lengthLo = lengthLo;

        if (data.length > 512) throw new MalformedArtnetPacketException("Cannot construct ArtCommandPacket: data too long");
        this.data = data;
    }

    /**
     * Constructs a new instance of this class.
     *
     * @param estaManHi high byte of the ESTA manufacturer code
     * @param estaManLo low byte of the ESTA manufacturer code
     * @param data      text command as null terminated ASCII byte array
     * @throws MalformedArtnetPacketException when data is too long
     */
    public ArtCommandPacket(byte estaManHi, byte estaManLo, byte[] data) throws MalformedArtnetPacketException {
        this(estaManHi, estaManLo, (byte)(data.length >> 8), (byte)data.length, data);
    }

    /**
     * Constructs a new instance of this class.
     *
     * @param estaManHi high byte of the ESTA manufacturer code
     * @param estaManLo low byte of the ESTA manufacturer code
     * @param data      text command as a String
     * @throws MalformedArtnetPacketException when data is too long
     */
    public ArtCommandPacket(byte estaManHi, byte estaManLo, String data) throws MalformedArtnetPacketException {
        this(estaManHi, estaManLo, asASCIIArrayNullTerminated(data));
    }

    /**
     * Constructs a new instance of this class.
     *
     * @param estaMan   the ESTA manufacturer code
     * @param data      text command as a String
     * @throws MalformedArtnetPacketException when data is too long
     */
    public ArtCommandPacket(int estaMan, String data) throws MalformedArtnetPacketException {
        this((byte)(estaMan >> 8), (byte)estaMan, data);
    }

    @Override
    public byte[] getPacketBytes() throws MalformedArtnetPacketException {
        int length = (this.lengthHi << 8) + this.lengthLo;
        int byteArrayLength = ArtnetPacket.ID.length + 2 + 1+1 + 1+1 + 1+1 + length;
        byte[] bytes = new byte[byteArrayLength];

        //Art-Net package ID
        System.arraycopy(ArtnetPacket.ID, 0, bytes, 0, ArtnetPacket.ID.length);

        //opcode
        byte[] opCode = ArtnetOpCodes.toByteArray(ArtnetOpCodes.OP_COMMAND);
        System.arraycopy(opCode, 0, bytes, ArtnetPacket.ID.length, 2);

        //protocol version
        bytes[10] = protVerHi;
        bytes[11] = protVerLo;

        bytes[12] = this.estaManHi;
        bytes[13] = this.estaManLo;

        bytes[14] = this.lengthHi;
        bytes[15] = this.lengthLo;

        if (bytes.length >= length + 16) {
            System.arraycopy(data, 0, bytes, 16, length);
        } else {
            throw new MalformedArtnetPacketException("cannot get packet bytes for ArtCommandPacket: not enough data for length available");
        }

        return bytes;
    }

    public static ArtCommandPacket fromBytes(byte[] bytes) throws MalformedArtnetPacketException {
        //check for minimum length
        int byteArrayLength = ArtnetPacket.ID.length + 2 + 1+1 + 1+1 + 1+1 + 1;
        if (bytes.length < byteArrayLength) {
            throw new MalformedArtnetPacketException("cannot construct ArtCommandPacket from bytes: bytes length not compatible");
        }

        //check for correct opcode
        byte[] opCode = ArtnetOpCodes.toByteArray(ArtnetOpCodes.OP_COMMAND);

        byte rOpCodeLo = bytes[8];
        byte rOpCodeHi = bytes[9];
        if (rOpCodeLo != opCode[0] || rOpCodeHi != opCode[1]) {
            throw new MalformedArtnetPacketException("cannot construct ArtCommandPacket from data: wrong opcode");
        }

        //check protocol version
        byte rProtVerHi = bytes[10];
        byte rProtVerLo = bytes[11];
        if (rProtVerHi < protVerHi || rProtVerLo < protVerLo) {
            throw new MalformedArtnetPacketException("cannot construct ArtCommandPacket from data: protVer not compatible");
        }

        byte rEstaManHi = bytes[12];
        byte rEstaManLo = bytes[13];

        byte rLengthHi = bytes[14];
        byte rLengthLo = bytes[15];

        int lengthI = (rLengthHi << 8) + rLengthLo;
        byte[] data = new byte[lengthI];
        if (bytes.length >= lengthI + 16) {
            System.arraycopy(bytes, 16, data, 0, lengthI);
        } else {
            throw new MalformedArtnetPacketException("cannot construct ArtCommandPacket from bytes: data too short (is " + (bytes.length - 16)
                    + "; should be " + lengthI + ")");
        }

        return new ArtCommandPacket(rEstaManHi, rEstaManLo, rLengthHi, rLengthLo, data);
    }

    public byte getEstaManHi() {
        return estaManHi;
    }

    public byte getEstaManLo() {
        return estaManLo;
    }

    public int getEstaMan() {
        return (Byte.toUnsignedInt(getEstaManHi()) << 8) + Byte.toUnsignedInt(getEstaManLo());
    }

    public byte getLengthHi() {
        return lengthHi;
    }

    public byte getLengthLo() {
        return lengthLo;
    }

    public int getLength() {
        return (Byte.toUnsignedInt(getLengthHi()) << 8) + Byte.toUnsignedInt(getLengthLo());
    }

    public byte[] getData() {
        return data;
    }

    public String getDataString() {
        return asString(this.data);
    }

    @Override
    public String toString() {
        return "ArtCommandPacket{" +
                "estaMan=" + asHex(getEstaMan(), 4) +
                ", length=" + getLength() +
                ", data=" + getDataString() +
                '}';
    }
}

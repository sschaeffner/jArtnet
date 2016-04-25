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
 * An implementation of the ArtTrigger packet as defined by the Art-Net standard.
 *
 * @author sschaeffner
 */
public class ArtTriggerPacket extends ArtnetPacket {

    private final byte oemCodeHi;
    private final byte oemCodeLo;
    private final byte key;
    private final byte subKey;
    private final byte[] data;

    /**
     * Constructs a new instance of this class.
     *
     * @param oemCodeHi high byte of the oem code
     * @param oemCodeLo low byte of the oem code
     * @param key       the trigger key
     * @param subKey    the trigger subkey
     * @param data      additional data
     * @throws MalformedArtnetPacketException when data is too long
     */
    public ArtTriggerPacket(byte oemCodeHi, byte oemCodeLo, byte key, byte subKey, byte[] data) throws MalformedArtnetPacketException {
        this.oemCodeHi = oemCodeHi;
        this.oemCodeLo = oemCodeLo;
        this.key = key;
        this.subKey = subKey;

        if (data.length != 512) throw new MalformedArtnetPacketException("Cannot construct ArtTriggerPacket: data has to be 512 byte long");
        this.data = data;
    }

    /**
     * Constructs a new instance of this class.
     *
     * @param oemCodeHi high byte of the oem code
     * @param oemCodeLo low byte of the oem code
     * @param key       the trigger key
     * @param subKey    the trigger subkey
     * @param data      additional data
     * @throws MalformedArtnetPacketException when data is too long
     */
    public ArtTriggerPacket(byte oemCodeHi, byte oemCodeLo, byte key, byte subKey, String data) throws MalformedArtnetPacketException {
        this(oemCodeHi, oemCodeLo, key, subKey, asASCIIArrayNullTerminated(data, 512));
    }

    /**
     * Constructs a new instance of this class.
     *
     * @param oemCode   the oem code
     * @param key       the trigger key
     * @param subKey    the trigger subkey
     * @param data      additional data
     * @throws MalformedArtnetPacketException when data is too long
     */
    public ArtTriggerPacket(int oemCode, byte key, byte subKey, String data) throws MalformedArtnetPacketException {
        this((byte)(oemCode >> 8), (byte)oemCode, key, subKey, asASCIIArrayNullTerminated(data, 512));
    }

    @Override
    public byte[] getPacketBytes() throws MalformedArtnetPacketException {
        int byteArrayLength = ArtnetPacket.ID.length + 2 + 1+1 + 1+1 + 1 + 1 + data.length;
        byte[] bytes = new byte[byteArrayLength];

        //Art-Net package ID
        System.arraycopy(ArtnetPacket.ID, 0, bytes, 0, ArtnetPacket.ID.length);

        //opcode
        byte[] opCode = ArtnetOpCodes.toByteArray(ArtnetOpCodes.OP_TRIGGER);
        System.arraycopy(opCode, 0, bytes, ArtnetPacket.ID.length, 2);

        //protocol version
        bytes[10] = protVerHi;
        bytes[11] = protVerLo;

        bytes[12] = oemCodeHi;
        bytes[13] = oemCodeLo;

        bytes[14] = key;
        bytes[15] = subKey;

        if (bytes.length >= data.length + 16) {
            System.arraycopy(data, 0, bytes, 16, data.length);
        } else {
            throw new MalformedArtnetPacketException("cannot get packet bytes for ArtTriggerPacket: not enough data for length available");
        }
        return bytes;
    }

    public static ArtTriggerPacket fromBytes(byte[] bytes) throws MalformedArtnetPacketException {
        //check for minimum length
        int byteArrayLength = ArtnetPacket.ID.length + 2 + 1+1 + 1+1 + 1 + 1 + 512;
        if (bytes.length < byteArrayLength) {
            throw new MalformedArtnetPacketException("cannot construct ArtTriggerPacket from bytes: too short");
        }

        //check for correct opcode
        byte[] opCode = ArtnetOpCodes.toByteArray(ArtnetOpCodes.OP_TRIGGER);

        byte rOpCodeLo = bytes[8];
        byte rOpCodeHi = bytes[9];
        if (rOpCodeLo != opCode[0] || rOpCodeHi != opCode[1]) {
            throw new MalformedArtnetPacketException("cannot construct ArtTriggerPacket from data: wrong opcode");
        }

        //check protocol version
        byte rProtVerHi = bytes[10];
        byte rProtVerLo = bytes[11];
        if (rProtVerHi < protVerHi || rProtVerLo < protVerLo) {
            throw new MalformedArtnetPacketException("cannot construct ArtTriggerPacket from data: protVer not compatible");
        }


        byte rOemCodeHi = bytes[12];
        byte rOemCodeLo = bytes[13];

        byte rKey = bytes[14];
        byte rSubKey = bytes[15];


        int lengthData = 16;
        for (int i = 16; i < bytes.length; i++) {
            if (bytes[i] == (byte) 0x00) {
                lengthData = i - 15;
                break;
            }
        }

        byte[] data = new byte[512];
        if (bytes.length >= 512 + 15) {
            System.arraycopy(bytes, 16, data, 0, lengthData);
        } else {
            throw new MalformedArtnetPacketException("cannot construct ArtTriggerPacket from bytes: data too short (is " + (bytes.length - 16)
                    + "; should be " + lengthData + ")");
        }

        return new ArtTriggerPacket(rOemCodeHi, rOemCodeLo, rKey, rSubKey, data);
    }

    public String getMessageAsString() {
        return asString(data);
    }

    /**
     * Translates a String into an ascii array with a null termination.
     *
     * @param message   String to translate
     * @return          message as ascii array with null termination
     */
    public static byte[] stringToAsciiArrayNullTerminated(String message) {
        char[] chars = message.toCharArray();
        byte[] ascii = new byte[chars.length + 1];//null termination
        for (int i = 0; i < chars.length; i++) {
            ascii[i] = (byte) chars[i];
        }

        return ascii;
    }

    public byte getOemCodeHi() {
        return oemCodeHi;
    }

    public byte getOemCodeLo() {
        return oemCodeLo;
    }

    public int getOemCode() {
        return (Byte.toUnsignedInt(getOemCodeHi()) << 8) + Byte.toUnsignedInt(getOemCodeLo());
    }

    public byte getKey() {
        return key;
    }

    public byte getSubKey() {
        return subKey;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ArtTriggerPacket{" +
                "oemCodeHi=" + asHex(getOemCode(), 4) +
                ", key=" + asHex(key) +
                ", subKey=" + asHex(subKey) +
                ", data=" + asString(data) +
                '}';
    }
}

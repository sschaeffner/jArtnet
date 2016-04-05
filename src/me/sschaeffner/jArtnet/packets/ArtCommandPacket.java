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
public class ArtCommandPacket extends ArtnetPacket {
    private final byte estaManHi;
    private final byte estaManLo;
    private final byte lengthHi;
    private final byte lengthLo;
    private final byte[] data;


    public ArtCommandPacket(byte estaManHi, byte estaManLo, byte lengthHi, byte lengthLo, byte[] data) throws MalformedArtnetPacketException {
        this.estaManHi = estaManHi;
        this.estaManLo = estaManLo;
        this.lengthHi = lengthHi;
        this.lengthLo = lengthLo;

        if (data.length > 512) throw new MalformedArtnetPacketException("Cannot construct ArtCommandPacket: data too long");
        this.data = data;
    }

    public ArtCommandPacket(byte estaManHi, byte estaManLo, String data) {
        this.estaManHi = estaManHi;
        this.estaManLo = estaManLo;
        this.data = stringToAsciiArrayNullTerminated(data);
        this.lengthHi = (byte)(this.data.length >> 8);
        this.lengthLo = (byte)this.data.length;
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

    public String getMessageAsString() {
        String message = "";

        byte[] data = getData();
        for (int i = 0; i < data.length && data[i] != 0; i++) {
            message += (char) data[i];
        }

        return message;
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

    public byte getEstaManHi() {
        return estaManHi;
    }

    public byte getEstaManLo() {
        return estaManLo;
    }

    public byte getLengthHi() {
        return lengthHi;
    }

    public byte getLengthLo() {
        return lengthLo;
    }

    public byte[] getData() {
        return data;
    }
}

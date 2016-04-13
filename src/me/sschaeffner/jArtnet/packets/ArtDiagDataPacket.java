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
 * An implementation of the ArtDiagData packet as defined by the Art-Net standard.
 *
 * @author sschaeffner
 */
public class ArtDiagDataPacket extends ArtnetPacket {

    private static final byte protVerHi = 3;
    private static final byte protVerLo = 14;

    private final byte priority;
    private final byte lengthHi;
    private final byte lengthLo;
    private final byte[] data;


    /**
     * Constructs a new instance of this class.
     *
     * @param priority  priority of this diagnostic data
     * @param lengthHi  high byte of the length of the data array
     * @param lengthLo  low byte of the length of the data array
     * @param data      data as a null terminated ASCII array
     * @throws MalformedArtnetPacketException when data is too long
     */
    public ArtDiagDataPacket(byte priority, byte lengthHi, byte lengthLo, byte[] data) throws MalformedArtnetPacketException {
        this.priority = priority;
        this.lengthHi = lengthHi;
        this.lengthLo = lengthLo;

        if (data.length > 512) throw new MalformedArtnetPacketException("Cannot construct ArtDiagDataPacket: data too long");
        this.data = data;
    }

    /**
     * Constructs a new instance of this class.
     *
     * @param priority  priority of this diagnostic data
     * @param data      data as a null terminated ASCII array
     * @throws MalformedArtnetPacketException when data is too long
     */
    public ArtDiagDataPacket(byte priority, byte[] data) throws MalformedArtnetPacketException {
        this(priority, (byte) (data.length >> 8), (byte) data.length, data);
    }

    /**
     * Constructs a new instance of this class.
     *
     * @param priority  priority of this diagnostic data
     * @param message   message as a String
     * @throws MalformedArtnetPacketException when message is too long
     */
    public ArtDiagDataPacket(byte priority, String message) throws MalformedArtnetPacketException {
        this.priority = priority;
        this.data = stringToAsciiArrayNullTerminated(message);
        if (data.length > 512) {
            throw new MalformedArtnetPacketException("Cannot construct ArtDiagDataPacket: data too long");
        }
        this.lengthHi = (byte) (this.data.length >> 8);
        this.lengthLo = (byte) this.data.length;
    }

    /**
     * Returns the whole packet's data as byte array.
     *
     * @return the packet's data as byte array
     */
    @Override
    public byte[] getPacketBytes() throws MalformedArtnetPacketException {
        int length = (this.lengthHi << 8) + this.lengthLo;
        int byteArrayLength = ArtnetPacket.ID.length + 2 + 1+1 + 1 + 1 + 1+1 + 1+1 + length;
        byte[] bytes = new byte[byteArrayLength];

        //Art-Net package ID
        System.arraycopy(ArtnetPacket.ID, 0, bytes, 0, ArtnetPacket.ID.length);

        //opcode
        byte[] opCode = ArtnetOpCodes.toByteArray(ArtnetOpCodes.OP_DIAG_DATA);
        System.arraycopy(opCode, 0, bytes, ArtnetPacket.ID.length, 2);

        //protocol version
        bytes[10] = protVerHi;
        bytes[11] = protVerLo;

        //filler 1
        bytes[12] = 0;

        //priority
        bytes[13] = this.priority;

        //filler 2 + 3
        bytes[14] = 0;
        bytes[15] = 0;

        //length of ASCII text array
        bytes[16] = this.lengthHi;
        bytes[17] = this.lengthLo;

        if (bytes.length >= length + 18) {
            System.arraycopy(data, 0, bytes, 18, length);
        } else {
            throw new MalformedArtnetPacketException("cannot get packet bytes for ArtDiagDataPacket: not enough data for length available");
        }

        return bytes;
    }

    public static ArtDiagDataPacket fromBytes(byte[] bytes) throws MalformedArtnetPacketException {
        //check for minimum length
        int byteArrayLength = ArtnetPacket.ID.length + 2 + 1+1 + 1 + 1 + 1+1 + 1+1 + 1;
        if (bytes.length < byteArrayLength) {
            throw new MalformedArtnetPacketException("cannot construct ArtDiagDataPacket from bytes: bytes length not compatible");
        }

        //check for correct opcode
        byte[] opCode = ArtnetOpCodes.toByteArray(ArtnetOpCodes.OP_DIAG_DATA);

        byte rOpCodeLo = bytes[8];
        byte rOpCodeHi = bytes[9];
        if (rOpCodeLo != opCode[0] || rOpCodeHi != opCode[1]) {
            throw new MalformedArtnetPacketException("cannot construct ArtDiagDataPacket from data: wrong opcode");
        }

        //check protocol version
        byte rProtVerHi = bytes[10];
        byte rProtVerLo = bytes[11];
        if (rProtVerHi < protVerHi || rProtVerLo < protVerLo) {
            throw new MalformedArtnetPacketException("cannot construct ArtDiagDataPacket from data: protVer not compatible");
        }

        //priority
        byte rPriority = bytes[13];

        //length of ASCII text array
        byte rLengthHi = bytes[16];
        byte rLengthLo = bytes[17];
        int lengthI = (rLengthHi << 8) + rLengthLo;

        byte[] data = new byte[lengthI];
        if (bytes.length >= lengthI + 18) {
            System.arraycopy(bytes, 18, data, 0, lengthI);
        } else {
            throw new MalformedArtnetPacketException("cannot construct ArtDiagDataPacket from bytes: data too short (is " + (bytes.length - 18)
                    + "; should be " + lengthI + ")");
        }

        return new ArtDiagDataPacket(rPriority, rLengthHi, rLengthLo, data);
    }

    public byte getPriority() {
        return priority;
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
}

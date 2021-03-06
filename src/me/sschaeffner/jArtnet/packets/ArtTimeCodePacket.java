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

/**
 * An implementation of the ArtTimeCode packet as defined by the Art-Net standard.
 *
 * @author sschaeffner
 */
public class ArtTimeCodePacket extends ArtnetPacket {

    private final byte frames;
    private final byte seconds;
    private final byte minutes;
    private final byte hours;
    private final byte type;


    /**
     * Constructs a new instance of this class.
     *
     * @param frames    frames time
     * @param seconds   seconds time
     * @param minutes   minutes time
     * @param hours     hours time
     * @param type      time code type
     */
    public ArtTimeCodePacket(byte frames, byte seconds, byte minutes, byte hours, byte type) {
        this.frames = frames;
        this.seconds = seconds;
        this.minutes = minutes;
        this.hours = hours;
        this.type = type;
    }

    @Override
    public byte[] getPacketBytes() throws MalformedArtnetPacketException {
        int byteArrayLength = ArtnetPacket.ID.length + 2 + 1+1 + 1+1 + 1 + 1 + 1 + 1 + 1;
        byte[] bytes = new byte[byteArrayLength];

        //Art-Net package ID
        System.arraycopy(ArtnetPacket.ID, 0, bytes, 0, ArtnetPacket.ID.length);

        //opcode
        byte[] opCode = ArtnetOpCodes.toByteArray(ArtnetOpCodes.OP_TIME_CODE);
        System.arraycopy(opCode, 0, bytes, ArtnetPacket.ID.length, 2);

        //protocol version
        bytes[10] = protVerHi;
        bytes[11] = protVerLo;

        //filler 1+2
        bytes[12] = 0;
        bytes[13] = 0;

        bytes[14] = this.frames;
        bytes[15] = this.seconds;
        bytes[16] = this.minutes;
        bytes[17] = this.hours;
        bytes[18] = this.type;

        return bytes;
    }

    public static ArtTimeCodePacket fromBytes(byte[] bytes) throws MalformedArtnetPacketException {
        //check for correct length
        int byteArrayLength = ArtnetPacket.ID.length + 2 + 1+1 + 1+1 + 1 + 1 + 1 + 1 + 1;
        if (bytes.length < byteArrayLength) {
            throw new MalformedArtnetPacketException("cannot construct ArtTimeCodePacket from bytes: bytes length not compatible");
        }

        //check for correct opcode
        byte[] opCode = ArtnetOpCodes.toByteArray(ArtnetOpCodes.OP_TIME_CODE);

        byte rOpCodeLo = bytes[8];
        byte rOpCodeHi = bytes[9];
        if (rOpCodeLo != opCode[0] || rOpCodeHi != opCode[1]) {
            throw new MalformedArtnetPacketException("cannot construct ArtTimeCodePacket from data: wrong opcode");
        }

        //check protocol version
        byte rProtVerHi = bytes[10];
        byte rProtVerLo = bytes[11];
        if (rProtVerHi < protVerHi || rProtVerLo < protVerLo) {
            throw new MalformedArtnetPacketException("cannot construct ArtTimeCodePacket from data: protVer not compatible");
        }

        byte rFrames = bytes[14];
        byte rSeconds = bytes[15];
        byte rMinutes = bytes[16];
        byte rHours = bytes[17];
        byte rType = bytes[18];

        return new ArtTimeCodePacket(rFrames, rSeconds, rMinutes, rHours, rType);
    }

    public byte getFrames() {
        return frames;
    }

    public byte getSeconds() {
        return seconds;
    }

    public byte getMinutes() {
        return minutes;
    }

    public byte getHours() {
        return hours;
    }

    public byte getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ArtTimeCodePacket{" +
                "frames=" + Byte.toUnsignedInt(frames) +
                ", seconds=" + Byte.toUnsignedInt(seconds) +
                ", minutes=" + Byte.toUnsignedInt(minutes) +
                ", hours=" + Byte.toUnsignedInt(hours) +
                ", type=" + asHex(type) +
                '}';
    }
}

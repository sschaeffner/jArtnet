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

/**
 * @author sschaeffner
 */
public class ArtIpProgPacket extends ArtnetPacket {

    private static final byte protVerHi = 3;
    private static final byte protVerLo = 14;

    private final byte command;
    private final byte progIpHi;
    private final byte progIp2;
    private final byte progIp1;
    private final byte progIpLo;
    private final byte progSmHi;
    private final byte progSm2;
    private final byte progSm1;
    private final byte progSmLo;
    private final byte progPortHi;
    private final byte progPortLo;


    /**
     * Constructs a new instance of this class.
     */
    public ArtIpProgPacket(byte command, byte progIpHi, byte progIp2, byte progIp1, byte progIpLo,
                           byte progSmHi, byte progSm2, byte progSm1, byte progSmLo, byte progPortHi, byte progPortLo) {
        this.command = command;
        this.progIpHi = progIpHi;
        this.progIp2 = progIp2;
        this.progIp1 = progIp1;
        this.progIpLo = progIpLo;
        this.progSmHi = progSmHi;
        this.progSm2 = progSm2;
        this.progSm1 = progSm1;
        this.progSmLo = progSmLo;
        this.progPortHi = progPortHi;
        this.progPortLo = progPortLo;
    }


    @Override
    public byte[] getPackageBytes() {
        int byteArrayLength = ArtnetPacket.ID.length + 2 + 1+1 + 1+1 + 1 + 1 + 4 + 4 + 2 + 8;
        byte[] bytes = new byte[byteArrayLength];

        //Art-Net package ID
        System.arraycopy(ArtnetPacket.ID, 0, bytes, 0, ArtnetPacket.ID.length);

        //opcode
        byte[] opCode = ArtnetOpCodes.toByteArray(ArtnetOpCodes.OP_IP_PROG);
        System.arraycopy(opCode, 0, bytes, ArtnetPacket.ID.length, 2);

        //protocol version
        bytes[10] = protVerHi;
        bytes[11] = protVerLo;

        //filler 1+2
        bytes[12] = 0;
        bytes[13] = 0;

        //command
        bytes[14] = this.command;

        //filler 4
        bytes[15] = 0;

        //progIp
        bytes[16] = this.progIpHi;
        bytes[17] = this.progIp2;
        bytes[18] = this.progIp1;
        bytes[19] = this.progIpLo;

        //progSm
        bytes[20] = this.progSmHi;
        bytes[21] = this.progSm2;
        bytes[22] = this.progSm1;
        bytes[23] = this.progSmLo;

        //progPort
        bytes[24] = this.progPortHi;
        bytes[25] = this.progPortLo;

        //spare (8byte)
        bytes[26] = 0;
        bytes[27] = 0;
        bytes[28] = 0;
        bytes[29] = 0;
        bytes[30] = 0;
        bytes[31] = 0;
        bytes[32] = 0;
        bytes[33] = 0;

        return bytes;
    }

    public static ArtIpProgPacket fromBytes(byte[] bytes) throws MalformedArtnetPacketException {
        //check for correct length
        int byteArrayLength = ArtnetPacket.ID.length + 2 + 1+1 + 1+1 + 1 + 1 + 4 + 4 + 2 + 8;
        if (bytes.length != byteArrayLength) {
            throw new MalformedArtnetPacketException("cannot construct ArtIpProgPacket from bytes: bytes length not compatible");
        }

        //check for correct opcode
        byte[] opCode = ArtnetOpCodes.toByteArray(ArtnetOpCodes.OP_IP_PROG);

        byte rOpCodeLo = bytes[8];
        byte rOpCodeHi = bytes[9];
        if (rOpCodeLo != opCode[0] || rOpCodeHi != opCode[1]) {
            throw new MalformedArtnetPacketException("cannot construct ArtIpProgPacket from data: wrong opcode");
        }

        //check protocol version
        byte rProtVerHi = bytes[10];
        byte rProtVerLo = bytes[11];
        if (rProtVerHi < protVerHi || rProtVerLo < protVerLo) {
            throw new MalformedArtnetPacketException("cannot construct ArtIpProgPacket from data: protVer not compatible");
        }

        byte rCommand = bytes[14];

        byte rProgIpHi = bytes[16];
        byte rProgIp2 = bytes[17];
        byte rProgIp1 = bytes[18];
        byte rProgIpLo = bytes[19];

        byte rProgSmHi = bytes[20];
        byte rProgSm2 = bytes[21];
        byte rProgSm1 = bytes[22];
        byte rProgSmLo = bytes[23];

        byte rProgPortHi = bytes[24];
        byte rProgPortLo = bytes[25];

        return new ArtIpProgPacket(rCommand, rProgIpHi, rProgIp2, rProgIp1, rProgIpLo, rProgSmHi, rProgSm2, rProgSm1, rProgSmLo, rProgPortHi, rProgPortLo);
    }

    public static byte getProtVerHi() {
        return protVerHi;
    }

    public static byte getProtVerLo() {
        return protVerLo;
    }

    public byte getCommand() {
        return command;
    }

    public byte getProgIpHi() {
        return progIpHi;
    }

    public byte getProgIp2() {
        return progIp2;
    }

    public byte getProgIp1() {
        return progIp1;
    }

    public byte getProgIpLo() {
        return progIpLo;
    }

    public byte getProgSmHi() {
        return progSmHi;
    }

    public byte getProgSm2() {
        return progSm2;
    }

    public byte getProgSm1() {
        return progSm1;
    }

    public byte getProgSmLo() {
        return progSmLo;
    }

    public byte getProgPortHi() {
        return progPortHi;
    }

    public byte getProgPortLo() {
        return progPortLo;
    }
}

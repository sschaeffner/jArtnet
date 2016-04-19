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
 * An implementation of the ArtIpProg packet as defined by the Art-Net standard.
 *
 * @author sschaeffner
 */
public class ArtIpProgPacket extends ArtnetPacket {

    private final byte command;
    private final byte[] progIp;
    private final byte[] progSm;
    private final byte progPortHi;
    private final byte progPortLo;


    /**
     * Constructs a new instance of this class.
     *
     * @param command       actions of this packet
     * @param progIp        IP address to be programmed into the node if enabled by command
     * @param progSm        subnet mask to be programmed into the node if enabled by command
     * @param progPortHi    high byte of the port to be programmed into the node if enabled by command
     * @param progPortLo    low byte of the port to be programmed into the node if enabled by command
     * @throws MalformedArtnetPacketException
     */
    public ArtIpProgPacket(byte command, byte[] progIp, byte[] progSm, byte progPortHi, byte progPortLo) throws MalformedArtnetPacketException {
        this.command = command;

        if (progIp.length != 4) throw new MalformedArtnetPacketException("Cannot construct ArtIpProgPacket: wrong progIp length");
        this.progIp = progIp;

        if (progSm.length != 4) throw new MalformedArtnetPacketException("Cannot construct ArtIpProgPacket: wrong progSm length");
        this.progSm = progSm;

        this.progPortHi = progPortHi;
        this.progPortLo = progPortLo;
    }


    @Override
    public byte[] getPacketBytes() {
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
        bytes[16] = this.progIp[0];
        bytes[17] = this.progIp[1];
        bytes[18] = this.progIp[2];
        bytes[19] = this.progIp[3];

        //progSm
        bytes[20] = this.progSm[0];
        bytes[21] = this.progSm[1];
        bytes[22] = this.progSm[2];
        bytes[23] = this.progSm[3];

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
        if (bytes.length < byteArrayLength) {
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

        byte[] rProgIp = new byte[]{bytes[16], bytes[17], bytes[18], bytes[19]};
        byte[] rProgSm = new byte[]{bytes[20], bytes[21], bytes[22], bytes[23]};

        byte rProgPortHi = bytes[24];
        byte rProgPortLo = bytes[25];

        return new ArtIpProgPacket(rCommand, rProgIp, rProgSm, rProgPortHi, rProgPortLo);
    }

    public byte getCommand() {
        return command;
    }

    public byte[] getProgIp() {
        return progIp;
    }

    public byte[] getProgSm() {
        return progSm;
    }

    public byte getProgPortHi() {
        return progPortHi;
    }

    public byte getProgPortLo() {
        return progPortLo;
    }
}

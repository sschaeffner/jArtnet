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
 * An implementation of the ArtInput packet as defined by the Art-Net standard.
 *
 * @author sschaeffner
 */
public class ArtInputPacket extends ArtnetPacket {

    private final byte numPortsHi;//for future expansion, currently zero
    private final byte numPortsLo;
    private final byte[] input;

    /**
     * Constructs a new instance of this class.
     *
     * @param numPortsHi high byte of the word describing the number of in-/output ports of the node
     * @param numPortsLo low byte of the word describing the number of in-/output ports of the node
     * @param input      array defining the disable status of each input
     * @throws MalformedArtnetPacketException
     */
    public ArtInputPacket(byte numPortsHi, byte numPortsLo, byte[] input) throws MalformedArtnetPacketException {
        this.numPortsHi = numPortsHi;
        this.numPortsLo = numPortsLo;
        this.input = input;
        if (this.input.length != 4) {
            throw new MalformedArtnetPacketException("Cannot construct ArtInputPacket: input array has to have a length of 4");
        }
    }

    /**
     * Constructs a new instance of this class.
     *
     * @param input1 disable status of input 1
     * @param input2 disable status of input 2
     * @param input3 disable status of input 3
     * @param input4 disable status of input 4
     */
    public ArtInputPacket(boolean input1, boolean input2, boolean input3, boolean input4) {
        this.numPortsHi = 0;
        this.numPortsLo = 4;
        byte input1Byte = input1 ? (byte)1 : (byte)0;
        byte input2Byte = input2 ? (byte)1 : (byte)0;
        byte input3Byte = input3 ? (byte)1 : (byte)0;
        byte input4Byte = input4 ? (byte)1 : (byte)0;
        input = new byte[]{input1Byte, input2Byte, input3Byte, input4Byte};
    }

    @Override
    public byte[] getPacketBytes() throws MalformedArtnetPacketException {
        int byteArrayLength = ArtnetPacket.ID.length + 2 + 1+1 + 1+1 + 1+1 + 4;
        byte[] bytes = new byte[byteArrayLength];

        //Art-Net package ID
        System.arraycopy(ArtnetPacket.ID, 0, bytes, 0, ArtnetPacket.ID.length);

        //opcode
        byte[] opCode = ArtnetOpCodes.toByteArray(ArtnetOpCodes.OP_INPUT);
        System.arraycopy(opCode, 0, bytes, ArtnetPacket.ID.length, 2);

        //protocol version
        bytes[10] = protVerHi;
        bytes[11] = protVerLo;

        //filler 1+2
        bytes[12] = 0;
        bytes[13] = 0;

        bytes[14] = this.numPortsHi;
        bytes[15] = this.numPortsLo;

        bytes[16] = this.input[0];
        bytes[17] = this.input[1];
        bytes[18] = this.input[2];
        bytes[19] = this.input[3];

        return bytes;
    }

    public static ArtInputPacket fromBytes(byte[] bytes) throws MalformedArtnetPacketException {
        //check for correct length
        int byteArrayLength = ArtnetPacket.ID.length + 2 + 1+1 + 1+1 + 1+1 + 4;
        if (bytes.length < byteArrayLength) {
            throw new MalformedArtnetPacketException("cannot construct ArtInputPacket from bytes: bytes length not compatible");
        }

        //check for correct opcode
        byte[] opCode = ArtnetOpCodes.toByteArray(ArtnetOpCodes.OP_INPUT);

        byte rOpCodeLo = bytes[8];
        byte rOpCodeHi = bytes[9];
        if (rOpCodeLo != opCode[0] || rOpCodeHi != opCode[1]) {
            throw new MalformedArtnetPacketException("cannot construct ArtInputPacket from data: wrong opcode");
        }

        //check protocol version
        byte rProtVerHi = bytes[10];
        byte rProtVerLo = bytes[11];
        if (rProtVerHi < protVerHi || rProtVerLo < protVerLo) {
            throw new MalformedArtnetPacketException("cannot construct ArtInputPacket from data: protVer not compatible");
        }

        byte rNumPortsHi = bytes[14];
        byte rNumPortsLo = bytes[15];

        byte rInput1 = bytes[16];
        byte rInput2 = bytes[17];
        byte rInput3 = bytes[18];
        byte rInput4 = bytes[19];

        byte[] rInput = new byte[]{rInput1, rInput2, rInput3, rInput4};

        return new ArtInputPacket(rNumPortsHi, rNumPortsLo, rInput);
    }

    public byte getNumPortsHi() {
        return numPortsHi;
    }

    public byte getNumPortsLo() {
        return numPortsLo;
    }

    public byte[] getInput() {
        return input;
    }
}

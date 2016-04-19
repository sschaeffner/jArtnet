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

import me.sschaeffner.jArtnet.ArtnetNode;
import me.sschaeffner.jArtnet.MalformedArtnetPacketException;

/**
 * An Art-Net packet.
 *
 * @author sschaeffner
 */
public abstract class ArtnetPacket {
    static final byte protVerHi = 3;
    static final byte protVerLo = 14;

    public static final byte[] ID = new byte[]{'A', 'r', 't', '-', 'N', 'e', 't', 0x00};

    private ArtnetNode sender;

    public void setSender(ArtnetNode sender) {
        this.sender = sender;
    }

    /**
     * Returns the packet's sender.
     * @return  the packet's sender
     */
    public ArtnetNode getSender() {
        return this.sender;
    }

    /**
     * Returns the whole packet's data as byte array.
     *
     * @return the packet's data as byte array
     */
    public abstract byte[] getPacketBytes() throws MalformedArtnetPacketException;

    /**
     * Returns the packet in the form of a byte[] as a subclass of ArtnetPacket.
     *
     * @param bytes packet data
     * @return      the packet as an ArtnetPacket
     * @throws MalformedArtnetPacketException when the packet data does not match the requirements for the packet type
     */
    public static ArtnetPacket fromBytes(byte[] bytes) throws MalformedArtnetPacketException {
        throw new MalformedArtnetPacketException("Subclass has not implemented fromBytes() method.");
    }
}

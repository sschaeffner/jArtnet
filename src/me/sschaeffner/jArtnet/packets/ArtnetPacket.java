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
    static final byte protVerHi = 0;
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

    /**
     * Returns a null terminated ASCII byte array as a String.
     *
     * @param bytes a null terminated ASCII byte array
     * @return      null terminated ASCII byte array as String
     */
    public static String asString(byte[] bytes) {
        String s = "";
        for (int i = 0; i < bytes.length && bytes[i] != 0; i++) s += (char)bytes[i];
        return s;
    }

    /**
     * Returns a byte as a hex formatted unsigned integer string.
     *
     * Adds a 0 in front of the hex number if it is shorter than 2 characters.
     *
     * @param b a byte
     * @return  the byte as a hex formatted unsigned integer string
     */
    public static String asHex(byte b) {
        String s = Integer.toHexString(Byte.toUnsignedInt(b)) + "";
        if (s.length() <= 1) s = "0" + s;
        return "0x" + s;
    }

    /**
     * Returns an int as a hex formatted unsigned integer string.
     * @param i an int
     * @return  the int as a hex formatted unsigned integer string.
     */
    public static String asHex(int i) {
        return "0x" + Integer.toHexString(i);
    }

    /**
     * Returns an int as a hex formatted unsigned integer string with a certain length
     * @param i         an int
     * @param length    supposed length
     * @return          the int as a hex formatted unsigned integer string
     */
    public static String asHex(int i, int length) {
        String h = Integer.toHexString(i);
        while (h.length() < length) {
            h = "0" + h;
        }
        return "0x" + h;
    }

    /**
     * Returns a byte array as a hex formatted unsigned integer string
     * @param bytes a byte array
     * @return      the byte array as a hex formatted unsigned integer string
     */
    public static String asHexArray(byte[] bytes) {
        String s = "[";
        for (int i = 0; i < bytes.length; i++) {
            if (i != 0) s += ", ";
            s += asHex(bytes[i]);
        }
        s += "]";
        return s;
    }

    /**
     * Returns a byte array as an IP formatted string.
     * @param bytes a byte array
     * @return      the byte array as an IP formatted string
     */
    public static String asIP(byte[] bytes) {
        if (bytes.length == 4) {
            String s = "";
            for (int i = 0; i < 4; i++) {
                s += Byte.toUnsignedInt(bytes[i]);
                if (i != 3) s += ".";
            }
            return s;
        } else {
            return "NOT.AN.IP";
        }
    }

    /**
     * Returns a byte array as a mac address formatted string.
     * @param bytes a byte array
     * @return      the byte array as a mac address formatted string
     */
    public static String asMac(byte[] bytes) {
        if (bytes.length == 6) {
            String s = "";
            for (int i = 0; i < 6; i++){
                String part = Integer.toHexString(Byte.toUnsignedInt(bytes[i])) + "";
                if (part.length() <= 1) part = "0" + part;
                s += part;

                if (i != 5) s+= ":";
            }
            return s;
        } else {
            return "not.a.mac.address";
        }
    }

    /**
     * Returns a String as a null terminated ASCII byte array.
     * @param s a String
     * @return  the String as a null terminated ASCII byte array
     */
    public static byte[] asASCIIArrayNullTerminated(String s) {
        char[] cs = s.toCharArray();
        byte[] chars = new byte[cs.length + 1];
        for (int i = 0; i < cs.length; i++) {
            chars[i] = (byte) cs[i];
        }
        return chars;
    }

    /**
     * Returns a String as a null terminated ASCII byte array with a given fixed length.
     * @param s             a String
     * @param fixedLength   fixed length of the ASCII byte array
     * @return              the String as a null terminated ASCII byte array
     */
    public static byte[] asASCIIArrayNullTerminated(String s, int fixedLength) {
        char[] cs = s.toCharArray();
        byte[] chars = new byte[fixedLength];
        for (int i = 0; i < cs.length && i < fixedLength - 1; i++) {
            chars[i] = (byte) cs[i];
        }
        return chars;
    }
}

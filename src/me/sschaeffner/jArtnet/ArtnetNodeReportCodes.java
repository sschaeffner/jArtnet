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
package me.sschaeffner.jArtnet;

/**
 * A collection of all NodeReport Codes defined by the Art-Net 3 specification.
 *
 * These Codes are used in ArtPollReply packets.
 *
 * Art-Net specification page 18.
 */
public class ArtnetNodeReportCodes {
    public static final int RC_DEBUG = 0x0000;
    public static final int RC_POWER_OK = 0x0001;
    public static final int RC_POWER_FAIL = 0x0002;
    public static final int RC_SOCKET_WR1 = 0x0003;
    public static final int RC_PARSE_FAIL = 0x0004;
    public static final int RC_UDP_FAIL = 0x0005;
    public static final int RC_SH_NAME_OK = 0x0006;
    public static final int RC_LO_NAME_OK = 0x0007;
    public static final int RC_DMX_ERROR = 0x0008;
    public static final int RC_DMX_UDP_FULL = 0x0009;
    public static final int RC_DMX_RX_FULL = 0x000a;
    public static final int RC_SWITCH_ERROR = 0x000b;
    public static final int RC_CONFIG_ERROR = 0x000c;
    public static final int RC_DMX_SHORT = 0x000d;
    public static final int RC_FIRMWARE_FAIL = 0x000e;
    public static final int RC_USER_FAIL = 0x000f;

    /**
     * Converts a NodeReportCode integer into a byte array.
     *
     * @param nodeReportCode    NodeReportCode to convert
     * @return                  converted NodeReportCode as byte array
     */
    public static byte[] toByteArray(int nodeReportCode) {
        byte highByte = (byte)((nodeReportCode >>> 8) & 0xFF);
        byte lowByte = (byte) (nodeReportCode & 0xFF);
        return new byte[]{highByte, lowByte};
    }

    /**
     * Converts a NodeReportCode integer into a ascii byte array.
     *
     * Puts high byte first, then low byte.
     *
     * @param nodeReportCode NodeReportCode to convert
     * @return
     */
    public static byte[] toASCIIByteArray(int nodeReportCode) {
        byte highByte1 = (byte)((nodeReportCode >>> 16) & 0xF);
        byte highByte2 = (byte)((nodeReportCode >>>  8) & 0xF);
        byte lowByte1 =  (byte)((nodeReportCode >>>  4) & 0xF);
        byte lowByte2 =  (byte)((nodeReportCode       ) & 0xF);

        byte ascii1 = (byte) (highByte1 + "").toCharArray()[0];
        byte ascii2 = (byte) (highByte2 + "").toCharArray()[0];
        byte ascii3 = (byte) (lowByte1  + "").toCharArray()[0];
        byte ascii4 = (byte) (lowByte2  + "").toCharArray()[0];
        return new byte[]{ascii1, ascii2, ascii3, ascii4};
    }
}

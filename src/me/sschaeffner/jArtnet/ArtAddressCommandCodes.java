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
 * @author sschaeffner
 */
public class ArtAddressCommandCodes {
    public static final byte AC_NONE = (byte)0x00;
    public static final byte AC_CANCEL_MERGE = (byte)0x01;
    public static final byte AC_LED_NORMAL = (byte)0x02;
    public static final byte AC_LED_MUTE = (byte)0x03;
    public static final byte AC_LED_LOCATE = (byte)0x04;
    public static final byte AC_RESET_RX_FLAGS = (byte)0x05;
    public static final byte AC_MERGE_LTP_0 = (byte)0x10;
    public static final byte AC_MERGE_LTP_1 = (byte)0x11;
    public static final byte AC_MERGE_LTP_2 = (byte)0x12;
    public static final byte AC_MERGE_LTP_3 = (byte)0x13;
    public static final byte AC_MERGE_HTP_0 = (byte)0x50;
    public static final byte AC_MERGE_HTP_1 = (byte)0x51;
    public static final byte AC_MERGE_HTP_2 = (byte)0x52;
    public static final byte AC_MERGE_HTP_3 = (byte)0x53;
    public static final byte AC_CLEAR_OP_0 = (byte)0x90;
    public static final byte AC_CLEAR_OP_1 = (byte)0x91;
    public static final byte AC_CLEAR_OP_2 = (byte)0x92;
    public static final byte AC_CLEAR_OP_3 = (byte)0x93;
}

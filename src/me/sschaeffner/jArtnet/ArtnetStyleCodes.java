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
package me.sschaeffner.jArtnet;

/**
 * A collection of all Style Codes defined by the Art-Net 3 specification.
 *
 * These Codes are used in ArtPollReply packages.
 *
 * Art-Net specification page 19.
 */
public class ArtnetStyleCodes {
    public static final byte ST_NODE = 0x00;
    public static final byte ST_CONTROLLER = 0x01;
    public static final byte ST_MEDIA = 0x02;
    public static final byte ST_ROUTE = 0x03;
    public static final byte ST_BACKUP = 0x04;
    public static final byte ST_CONFIG = 0x05;
    public static final byte ST_VISUAL = 0x06;

    public static String toName(byte code) {
        switch (code) {
            case ST_NODE:
                return "NODE";
            case ST_CONTROLLER:
                return "CONTROLLER";
            case ST_MEDIA:
                return "MEDIA";
            case ST_ROUTE:
                return "ROUTE";
            case ST_BACKUP:
                return "BACKUP";
            case ST_CONFIG:
                return "CONFIG";
            case ST_VISUAL:
                return "VISUAL";
            default:
                return "UNKNOWN";
        }
    }
}

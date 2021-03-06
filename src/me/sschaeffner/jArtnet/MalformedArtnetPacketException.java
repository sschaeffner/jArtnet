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
 * An Exception that is triggered when an error regarding ArtnetPackets occurs.
 *
 * @author sschaeffner
 */
public class MalformedArtnetPacketException extends Exception {
    private final String detailMessage;

    /**
     * Constructs a new instance of this class.
     *
     * @param detailMessage a detailed message describing the exception
     */
    public MalformedArtnetPacketException(String detailMessage) {
        this.detailMessage = detailMessage;
    }

    @Override
    public String getMessage() {
        return this.detailMessage;
    }
}

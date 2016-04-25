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
 * An event that is fired upon discovery of a new ArtnetNode.
 *
 * @author sschaeffner
 */
public class ArtnetNodeDiscoveryEvent {
    private ArtnetNode newNode;

    /**
     * Constructs a new instance of this class.
     *
     * @param newNode the newly discovered node
     */
    ArtnetNodeDiscoveryEvent(ArtnetNode newNode) {
        this.newNode = newNode;
    }

    public ArtnetNode getNewNode() {
        return newNode;
    }
}

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
 * A listener for ArtnetNodeDiscoveryEvents.
 *
 * @author sschaeffner
 */
public interface ArtnetNodeListener {
    /**
     * Called upon discovery of a new ArtnetNode.
     *
     * @param event event for the newly discovered ArtnetNode
     */
    void onArtnetNodeDiscovery(ArtnetNodeDiscoveryEvent event);
}

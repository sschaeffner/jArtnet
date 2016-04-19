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

import java.io.IOException;
import java.util.HashMap;

/**
 * Factory class for ArtnetController.
 *
 * @author sschaeffner
 */
public class ArtnetControllerFactory {

    private static ArtnetController testingInstance;
    private static HashMap<String, ArtnetController> instances;

    static {
        instances = new HashMap<>();
    }

    /**
     * Returns an instance of ArtnetController for the given host address and port.
     *
     * @param host host IP for the ArtnetController
     * @param port port for the ArtnetController
     * @return     an instance of ArtnetController
     * @throws IOException when the port cannot be opened
     */
    public static ArtnetController getInstance(NetworkAddress host, int port) throws IOException {
        if (host == null) {
            throw new IllegalArgumentException("cannot getInstance of ArtnetController: host is null");
        }
        if (port <= 0) {
            throw new IllegalArgumentException("cannot getInstance of ArtnetController: port has to be greater than 0");
        }

        String identifier = host.getInterfaceAddress().getAddress() + ":" + port;

        if (instances.containsKey(identifier)) {
            return instances.get(identifier);
        } else {
            ArtnetController artnetController = new ArtnetController(host, port);
            instances.put(identifier, artnetController);
            return artnetController;
        }
    }

    public static ArtnetController getTestingInstance() throws IOException {
        if (testingInstance == null) testingInstance = new ArtnetController(NetworkAddress.getLoopbackAddress(), 6454);
        return testingInstance;
    }
}

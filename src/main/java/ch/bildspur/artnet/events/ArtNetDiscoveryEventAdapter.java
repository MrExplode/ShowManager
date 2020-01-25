/*
 * This file is part of artnet4j.
 * 
 * Copyright 2009 Karsten Schmidt (PostSpectacular Ltd.)
 * 
 * artnet4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * artnet4j is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with artnet4j. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.bildspur.artnet.events;

import java.util.List;

import ch.bildspur.artnet.ArtNetNode;

public class ArtNetDiscoveryEventAdapter implements ArtNetDiscoveryListener {

    @Override
    public void discoveredNewNode(ArtNetNode node) {

    }

    @Override
    public void discoveredNodeDisconnected(ArtNetNode node) {

    }

    @Override
    public void discoveryCompleted(List<ArtNetNode> nodes) {

    }

    @Override
    public void discoveryFailed(Throwable t) {

    }
}

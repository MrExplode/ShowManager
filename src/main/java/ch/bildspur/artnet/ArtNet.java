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

package ch.bildspur.artnet;

import ch.bildspur.artnet.events.ArtNetServerEventAdapter;
import ch.bildspur.artnet.events.ArtNetServerListener;
import ch.bildspur.artnet.packets.ArtNetPacket;
import ch.bildspur.artnet.packets.ArtPollReplyPacket;
import ch.bildspur.artnet.packets.PacketType;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArtNet {

    public static final Logger logger =
            Logger.getLogger(ArtNet.class.getClass().getName());

    protected static final long ARTPOLL_REPLY_TIMEOUT = 3000;

    protected static final String VERSION = "0.2.0";

    protected ArtNetServer server;
    protected ArtNetNodeDiscovery discovery;

    public ArtNet() {
        logger.info("Art-Net v" + VERSION);
    }

    public void addServerListener(ArtNetServerListener l) {
        server.addListener(l);
    }

    public void broadcastPacket(ArtNetPacket packet) {
        server.broadcastPacket(packet);
    }

    public ArtNetNodeDiscovery getNodeDiscovery() {
        if (discovery == null) {
            discovery = new ArtNetNodeDiscovery(this);
        }
        return discovery;
    }

    public void init() {
        server = new ArtNetServer();
        server.addListener(new ArtNetServerEventAdapter() {

            @Override
            public void artNetPacketReceived(ArtNetPacket packet) {
                logger.fine("packet received: " + packet.getType());
                if (discovery != null
                        && packet.getType() == PacketType.ART_POLL_REPLY) {
                    discovery.discoverNode((ArtPollReplyPacket) packet);
                }
            }

            @Override
            public void artNetServerStarted(ArtNetServer artNetServer) {
                logger.fine("server started callback");
            }

            @Override
            public void artNetServerStopped(ArtNetServer artNetServer) {
                logger.info("server stopped");
            }
        });
    }

    public void removeServerListener(ArtNetServerListener l) {
        server.removeListener(l);
    }

    public void setBroadCastAddress(String ip) {
        server.setBroadcastAddress(ip);
    }

    /**
     * Starts the Artnet client.
     *
     * @throws SocketException Socket Exception.
     * @throws ArtNetException ArtNet Exception.
     */
    public void start() throws SocketException, ArtNetException {
        start(null);
    }

    /**
     * Starts the Artnet client.
     *
     * @param networkAddress Network address to bind to
     * @throws SocketException Socket Exception.
     * @throws ArtNetException ArtNet Exception.
     */
    public void start(InetAddress networkAddress) throws SocketException, ArtNetException {
        if (server == null) {
            init();
        }
        server.start(networkAddress);
    }

    public void startNodeDiscovery() throws ArtNetException {
        getNodeDiscovery().start();
    }

    public void stop() {
        if (discovery != null) {
            discovery.stop();
        }
        if (server != null) {
            server.stop();
        }
    }

    /**
     * Sends the given packet to the specified Art-Net node.
     *
     * @param packet Packet to send.
     * @param node Node to send to.
     */
    public void unicastPacket(ArtNetPacket packet, ArtNetNode node) {
        server.unicastPacket(packet, node.getIPAddress());
    }

    /**
     * Sends the given packet to the specified IP address.
     *
     * @param packet Packet to send.
     * @param adr Node to send to.
     */
    public void unicastPacket(ArtNetPacket packet, InetAddress adr) {
        server.unicastPacket(packet, adr);
    }

    /**
     * Sends the given packet to the specified IP address.
     *
     * @param packet Packet to send.
     * @param adr Node to send to.
     */
    public void unicastPacket(ArtNetPacket packet, String adr) {
        InetAddress targetAdress;
        try {
            targetAdress = InetAddress.getByName(adr);
            server.unicastPacket(packet, targetAdress);
        } catch (UnknownHostException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
    }
}
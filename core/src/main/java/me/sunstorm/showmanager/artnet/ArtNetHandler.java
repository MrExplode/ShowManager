package me.sunstorm.showmanager.artnet;

import ch.bildspur.artnet.ArtNetBuffer;
import ch.bildspur.artnet.ArtNetException;
import ch.bildspur.artnet.ArtNetServer;
import ch.bildspur.artnet.PortDescriptor;
import ch.bildspur.artnet.events.ArtNetServerEventAdapter;
import ch.bildspur.artnet.packets.*;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.terminable.Terminable;
import me.sunstorm.showmanager.util.Timecode;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.net.SocketException;

@Slf4j
public class ArtNetHandler implements Terminable {
    private final ArtNetServer server;
    private final ArtTimePacket packet;
    private final ArtNetBuffer buffer;
    private final InetAddress address;

    public ArtNetHandler(InetAddress address) {
        register();
        server = new ArtNetServer();
        packet = new ArtTimePacket();
        buffer = new ArtNetBuffer();
        this.address = address;

        server.addListener(new ArtNetServerEventAdapter() {
            @Override
            public void artNetPacketReceived(ArtNetPacket packet) {
                if (packet.getType() != PacketType.ART_OUTPUT)
                    return;

                ArtDmxPacket dmxPacket = (ArtDmxPacket) packet;
                int subnet = dmxPacket.getSubnetID();
                int universe = dmxPacket.getUniverseID();

                buffer.setDmxData((short) subnet, (short) universe, dmxPacket.getDmxData());
            }
        });
        setReplyPacket();
        try {
            server.start(address);
        } catch (SocketException | ArtNetException e) {
            log.error("Failed to start ArtNet server", e);
        }
    }

    public void setTime(@NotNull Timecode time) {
        packet.setTime(time.getHour(), time.getMin(), time.getSec(), time.getFrame());
    }

    public byte[] getData(int subnet, int universe) {
        return buffer.getDmxData((short) subnet, (short) universe);
    }

    public void broadcast() {
        server.broadcastPacket(packet);
    }

    @Override
    public void shutdown() {
        log.info("Shutting down ArtNet...");
        server.stop();
    }

    private void setReplyPacket() {
        ArtPollReplyPacket replyPacket = new ArtPollReplyPacket();
        replyPacket.setIp(address);
        replyPacket.setShortName("ShowManager Node");
        replyPacket.setLongName("ShowManager Node by MrExplode");
        replyPacket.setVersionInfo(1);
        replyPacket.setSubSwitch(1);
        replyPacket.setOemCode(5);
        PortDescriptor port = new PortDescriptor();
        port.setCanInput(true);
        port.setCanOutput(true);
        replyPacket.setPorts(new PortDescriptor[] {port});

        replyPacket.translateData();
        server.setDefaultReplyPacket(replyPacket);
    }
}

package me.sunstorm.showmanager.artnet;

import ch.bildspur.artnet.ArtNetBuffer;
import ch.bildspur.artnet.ArtNetException;
import ch.bildspur.artnet.ArtNetServer;
import ch.bildspur.artnet.PortDescriptor;
import ch.bildspur.artnet.events.ArtNetServerEventAdapter;
import ch.bildspur.artnet.packets.*;
import com.google.gson.JsonObject;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.injection.DependencyInjection;
import me.sunstorm.showmanager.injection.InjectRecipient;
import me.sunstorm.showmanager.settings.SettingsHolder;
import me.sunstorm.showmanager.terminable.Terminable;
import me.sunstorm.showmanager.util.Timecode;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

@Slf4j
public class ArtNetHandler extends SettingsHolder implements Terminable {
    @Setter private InetAddress address;
    @Setter private boolean enabled = false;

    private final ArtNetServer server;
    private final ArtTimePacket packet;
    private final ArtNetBuffer buffer;

    public ArtNetHandler() {
        super("art-net");
        load();
        register();
        DependencyInjection.registerProvider(ArtNetHandler.class, () -> this);
        server = new ArtNetServer();
        packet = new ArtTimePacket();
        buffer = new ArtNetBuffer();

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
        if (enabled) {
            server.broadcastPacket(packet);
        }
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

    @Override
    public JsonObject getData() {
        JsonObject data = new JsonObject();
        data.addProperty("enabled", enabled);
        data.addProperty("interface", address == null ? "127.0.0.1" : address.getHostAddress());
        return data;
    }

    @Override
    public void onLoad(JsonObject object) {
        enabled = object.get("enabled").getAsBoolean();
        try {
            address = InetAddress.getByName(object.get("interface").getAsString());
        } catch (UnknownHostException e) {
            log.error("Failed to load ArtNet net interface", e);
        }
    }
}

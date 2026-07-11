package me.sunstorm.showmanager.modules.artnet;

import ch.bildspur.artnet.ArtNetBuffer;
import ch.bildspur.artnet.ArtNetException;
import ch.bildspur.artnet.ArtNetServer;
import ch.bildspur.artnet.PortDescriptor;
import ch.bildspur.artnet.events.ArtNetServerEventAdapter;
import ch.bildspur.artnet.packets.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.sunstorm.showmanager.cluster.OutputType;
import me.sunstorm.showmanager.cluster.Ownership;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.eventsystem.EventCall;
import me.sunstorm.showmanager.eventsystem.events.time.TimecodeChangeEvent;
import me.sunstorm.showmanager.modules.ToggleableModule;
import me.sunstorm.showmanager.modules.remote.DmxRemoteModule;
import me.sunstorm.showmanager.util.Timecode;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

@Singleton
public class ArtNetModule extends ToggleableModule {
    private static final Logger log = LoggerFactory.getLogger(ArtNetModule.class);

    private InetAddress address;

    private final ArtNetServer server;
    private final ArtTimePacket packet;
    private final ArtNetBuffer buffer;
    private final DmxRemoteModule dmxRemote;
    private final Ownership ownership;
    private boolean serverStarted = false;

    @Inject
    public ArtNetModule(EventBus eventBus, DmxRemoteModule dmxRemote, Ownership ownership) {
        super(eventBus);
        this.dmxRemote = dmxRemote;
        this.ownership = ownership;
        init();

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
                if (subnet == dmxRemote.getAddress().subnet() && universe == dmxRemote.getAddress().universe())
                    dmxRemote.handleData(dmxPacket.getDmxData());
            }
        });
        if (ownership.owns(OutputType.ARTNET)) {
            setReplyPacket();
            try {
                server.start(address);
                serverStarted = true;
            } catch (SocketException | ArtNetException e) {
                log.error("Failed to start ArtNet server", e);
            }
        } else {
            log.info("Art-Net not owned by this node, server not started");
        }
    }

    @EventCall
    public void onTimeChange(@NotNull TimecodeChangeEvent event) {
        setTime(event.getTime());
        broadcast();
    }

    public void setTime(@NotNull Timecode time) {
        packet.setTime(time.getHour(), time.getMin(), time.getSec(), time.getFrame());
    }

    public byte[] getData(int subnet, int universe) {
        return buffer.getDmxData((short) subnet, (short) universe);
    }

    public void broadcast() {
        if (isEnabled() && ownership.owns(OutputType.ARTNET)) {
            server.broadcastPacket(packet);
        }
    }

    @Override
    public void shutdown() {
        log.info("Shutting down ArtNet...");
        if (serverStarted)
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

    @NotNull
    @Override
    public JsonObject getData() {
        JsonObject data = new JsonObject();
        data.addProperty("enabled", isEnabled());
        data.addProperty("interface", address == null ? "127.0.0.1" : address.getHostAddress());
        return data;
    }

    @Override
    public void onLoad(@NotNull JsonElement element) {
        var object = element.getAsJsonObject();
        setEnabled(object.get("enabled").getAsBoolean());
        try {
            address = InetAddress.getByName(object.get("interface").getAsString());
        } catch (UnknownHostException e) {
            log.error("Failed to load ArtNet net interface", e);
        }
    }

    @Override
    public String getName() {
        return "art-net";
    }

    // generated

    public void setAddress(InetAddress address) {
        this.address = address;
    }
}

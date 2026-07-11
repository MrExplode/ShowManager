package me.sunstorm.showmanager.cluster;

import me.sunstorm.showmanager.settings.config.ClusterConfig;
import me.sunstorm.showmanager.terminable.Terminable;
import org.jgroups.BytesMessage;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.jgroups.protocols.TP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

public class ClusterService implements Terminable {
    private static final Logger log = LoggerFactory.getLogger(ClusterService.class);

    private final ClusterConfig config;
    private JChannel channel;
    private volatile Consumer<byte[]> messageListener;

    public ClusterService(ClusterConfig config) {
        this.config = config;
        register();
    }

    public void connect() {
        if (!config.isEnabled())
            return;
        try {
            if (!config.isUseMulticast() && !config.getSeedNodes().isEmpty())
                System.setProperty("jgroups.tcpping.initial_hosts", String.join(",", config.getSeedNodes()));

            channel = new JChannel(config.isUseMulticast() ? "udp.xml" : "tcp.xml");
            TP transport = channel.getProtocolStack().getTransport();
            if (!config.getBindAddress().isEmpty())
                transport.setValue("bind_addr", InetAddress.getByName(config.getBindAddress()));
            if (!config.isUseMulticast())
                transport.setValue("bind_port", config.getPort());

            channel.setDiscardOwnMessages(true);
            channel.setReceiver(receiver);
            if (!config.getNodeName().isEmpty())
                channel.name(config.getNodeName());
            channel.connect(config.getClusterName());
            log.info("[cluster] connected as '{}' to '{}'", channel.getAddressAsString(), config.getClusterName());
        } catch (Exception e) {
            log.error("Failed to start cluster", e);
        }
    }

    private final Receiver receiver = new Receiver() {
        @Override
        public void receive(Message msg) {
            Consumer<byte[]> listener = messageListener;
            if (listener != null)
                listener.accept(Arrays.copyOfRange(msg.getArray(), msg.getOffset(), msg.getOffset() + msg.getLength()));
        }

        @Override
        public void viewAccepted(View view) {
            log.info("[cluster] view: {} members, coordinator {}", view.size(), view.getCoord());
        }
    };

    public void send(byte[] payload) {
        if (channel == null || !channel.isConnected())
            return;
        try {
            channel.send(new BytesMessage(null, payload));
        } catch (Exception e) {
            log.error("Failed to send cluster message", e);
        }
    }

    public void setMessageListener(Consumer<byte[]> listener) {
        this.messageListener = listener;
    }

    public boolean isConnected() {
        return channel != null && channel.isConnected();
    }

    public boolean isCoordinator() {
        if (!isConnected())
            return true;
        return Objects.equals(channel.getAddress(), channel.getView().getCoord());
    }

    public String selfId() {
        return channel != null ? channel.getAddressAsString() : "local";
    }

    @Override
    public void shutdown() {
        if (channel != null) {
            log.info("Shutting down cluster...");
            channel.close();
        }
    }
}

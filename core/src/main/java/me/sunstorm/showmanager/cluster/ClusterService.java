package me.sunstorm.showmanager.cluster;

import me.sunstorm.showmanager.settings.config.ClusterConfig;
import me.sunstorm.showmanager.terminable.Terminable;
import org.jgroups.Address;
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

    public static final byte MSG_EVENT = 1;
    public static final byte MSG_SYNC_REQ = 2;
    public static final byte MSG_SYNC_RESP = 3;

    public interface SyncHandler {
        void handle(Address src, byte type, byte[] body);
    }

    private final ClusterConfig config;
    private volatile JChannel channel;
    private volatile Consumer<byte[]> messageListener;
    private volatile SyncHandler syncHandler;
    private volatile Runnable viewHandler;

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
            byte[] raw = Arrays.copyOfRange(msg.getArray(), msg.getOffset(), msg.getOffset() + msg.getLength());
            if (raw.length == 0)
                return;
            byte type = raw[0];
            byte[] body = Arrays.copyOfRange(raw, 1, raw.length);
            if (type == MSG_EVENT) {
                Consumer<byte[]> listener = messageListener;
                if (listener != null)
                    listener.accept(body);
            } else {
                SyncHandler handler = syncHandler;
                if (handler != null)
                    handler.handle(msg.getSrc(), type, body);
            }
        }

        @Override
        public void viewAccepted(View view) {
            log.info("[cluster] view: {} members, coordinator {}", view.size(), view.getCoord());
            Runnable handler = viewHandler;
            if (handler != null)
                handler.run();
        }
    };

    public void send(byte[] payload) {
        send(payload, false);
    }

    public void send(byte[] payload, boolean oob) {
        sendRaw(null, MSG_EVENT, payload, oob);
    }

    public void sendSync(Address dest, byte type, byte[] body) {
        sendRaw(dest, type, body, true);
    }

    private void sendRaw(Address dest, byte type, byte[] body, boolean oob) {
        JChannel ch = channel;
        if (ch == null || !ch.isConnected())
            return;
        byte[] payload = new byte[body.length + 1];
        payload[0] = type;
        System.arraycopy(body, 0, payload, 1, body.length);
        Message msg = new BytesMessage(dest, payload);
        if (oob)
            msg.setFlag(Message.Flag.OOB);
        try {
            ch.send(msg);
        } catch (Exception e) {
            log.error("Failed to send cluster message", e);
        }
    }

    public void setMessageListener(Consumer<byte[]> listener) {
        this.messageListener = listener;
    }

    public void setSyncHandler(SyncHandler handler) {
        this.syncHandler = handler;
    }

    public void setViewHandler(Runnable handler) {
        this.viewHandler = handler;
    }

    public boolean isConnected() {
        JChannel ch = channel;
        return ch != null && ch.isConnected();
    }

    public boolean isCoordinator() {
        if (!isConnected())
            return true;
        return Objects.equals(channel.getAddress(), channel.getView().getCoord());
    }

    public Address getAddress() {
        JChannel ch = channel;
        return ch != null ? ch.getAddress() : null;
    }

    public Address getCoordinator() {
        JChannel ch = channel;
        return ch != null && ch.getView() != null ? ch.getView().getCoord() : null;
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

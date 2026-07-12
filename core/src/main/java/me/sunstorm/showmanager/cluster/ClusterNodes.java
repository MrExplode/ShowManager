package me.sunstorm.showmanager.cluster;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.sunstorm.showmanager.Worker;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.eventsystem.events.cluster.ClusterStateChangeEvent;
import me.sunstorm.showmanager.settings.config.ClusterConfig;
import me.sunstorm.showmanager.settings.config.Config;
import me.sunstorm.showmanager.terminable.Terminable;
import org.jgroups.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Cluster-wide node directory. A JGroups view carries <i>who</i> is a member, but not what each node
 * is configured to do, so every node gossips its own identity, framerate, owned outputs and
 * clock-sync quality, and caches what it hears from its peers.
 */
@Singleton
public class ClusterNodes implements Terminable {
    private static final Logger log = LoggerFactory.getLogger(ClusterNodes.class);
    private static final long HEARTBEAT_MS = 2000;
    private static final long STALE_AFTER_MS = 6000;

    private final ClusterService cluster;
    private final ClusterConfig config;
    private final Ownership ownership;
    private final ClockSync clockSync;
    private final Worker worker;
    private final EventBus eventBus;
    private final int framerate;

    private final Map<String, NodeInfo> peers = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "cluster-nodes");
        t.setDaemon(true);
        return t;
    });

    @Inject
    public ClusterNodes(ClusterService cluster, Ownership ownership, ClockSync clockSync, Worker worker,
                        EventBus eventBus, Config config, @Named("framerate") int framerate) {
        this.cluster = cluster;
        this.config = config.getClusterConfig();
        this.ownership = ownership;
        this.clockSync = clockSync;
        this.worker = worker;
        this.eventBus = eventBus;
        this.framerate = framerate;
        cluster.addSyncHandler(this::onMessage);
        cluster.addViewHandler(this::onView);
        register();
    }

    public void start() {
        if (!config.isEnabled())
            return;
        scheduler.scheduleAtFixedRate(this::heartbeat, 0, HEARTBEAT_MS, TimeUnit.MILLISECONDS);
    }

    private NodeInfo self() {
        return new NodeInfo(
                cluster.selfId(),
                config.getNodeName().isEmpty() ? cluster.selfId() : config.getNodeName(),
                framerate,
                ownership.getOwned().stream().map(Enum::name).collect(Collectors.toCollection(ArrayList::new)),
                worker.isPlaying(),
                clockSync.synced(),
                clockSync.offsetNanos() / 1000,
                clockSync.delayNanos() / 2000,
                System.currentTimeMillis()
        );
    }

    private void heartbeat() {
        try {
            if (!cluster.isConnected())
                return;
            cluster.broadcastSync(ClusterService.MSG_NODE_INFO, encode(self()).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.debug("[cluster] node info heartbeat failed", e);
        }
    }

    private void onMessage(Address src, byte type, byte[] body) {
        if (type != ClusterService.MSG_NODE_INFO)
            return;
        try {
            NodeInfo info = decode(JsonParser.parseString(new String(body, StandardCharsets.UTF_8)).getAsJsonObject());
            NodeInfo previous = peers.put(info.id(), info);
            if (previous == null) {
                log.info("[cluster] node '{}' joined ({} fps, outputs {})", info.name(), info.framerate(), info.outputs());
                changed();
            } else if (previous.differsStructurally(info)) {
                changed();
            }
        } catch (Exception e) {
            log.debug("[cluster] malformed node info from {}", src, e);
        }
    }

    private void onView() {
        Set<String> members = Set.copyOf(cluster.getMembers());
        boolean dropped = peers.keySet().removeIf(id -> !members.contains(id));
        if (dropped)
            log.info("[cluster] pruned departed nodes, {} peers left", peers.size());
        changed();
        // a fresh member knows nothing about us until we announce
        heartbeat();
    }

    private void changed() {
        new ClusterStateChangeEvent().call(eventBus);
    }

    public JsonObject state() {
        JsonObject data = new JsonObject();
        boolean enabled = config.isEnabled();
        data.addProperty("enabled", enabled);
        data.addProperty("connected", cluster.isConnected());
        data.addProperty("master", worker.isMaster());
        data.addProperty("clusterName", config.getClusterName());
        data.addProperty("useMulticast", config.isUseMulticast());
        data.addProperty("self", cluster.selfId());
        data.addProperty("coordinator", cluster.coordinatorId());
        data.addProperty("framerate", framerate);

        NodeInfo self = self();
        String coordinator = cluster.coordinatorId();
        List<String> members = cluster.getMembers();

        JsonArray nodes = new JsonArray();
        nodes.add(node(self, true, worker.isMaster(), members.isEmpty() || members.contains(self.id())));
        peers.values().stream()
                .filter(peer -> !peer.id().equals(self.id()))
                .sorted((a, b) -> a.name().compareToIgnoreCase(b.name()))
                .forEach(peer -> nodes.add(node(peer, false, Objects.equals(peer.id(), coordinator), members.contains(peer.id()))));
        data.add("nodes", nodes);

        JsonArray warnings = new JsonArray();
        peers.values().stream()
                .filter(peer -> !peer.id().equals(self.id()))
                .filter(peer -> peer.framerate() != 0 && peer.framerate() != framerate)
                .forEach(peer -> warnings.add("Node '" + peer.name() + "' runs at " + peer.framerate() + " fps but this node is "
                        + framerate + " fps — timecode will diverge. Align the framerate across nodes."));
        data.add("warnings", warnings);
        return data;
    }

    private JsonObject node(NodeInfo info, boolean self, boolean master, boolean inView) {
        JsonObject data = new JsonObject();
        data.addProperty("id", info.id());
        data.addProperty("name", info.name());
        data.addProperty("self", self);
        data.addProperty("master", master);
        data.addProperty("framerate", info.framerate());
        data.addProperty("playing", info.playing());
        data.addProperty("synced", master || info.synced());
        data.addProperty("offsetUs", info.offsetUs());
        data.addProperty("delayUs", info.delayUs());
        data.addProperty("inView", inView);
        data.addProperty("stale", !self && System.currentTimeMillis() - info.lastSeen() > STALE_AFTER_MS);
        JsonArray outputs = new JsonArray();
        info.outputs().forEach(outputs::add);
        data.add("outputs", outputs);
        return data;
    }

    static String encode(NodeInfo info) {
        JsonObject data = new JsonObject();
        data.addProperty("id", info.id());
        data.addProperty("name", info.name());
        data.addProperty("framerate", info.framerate());
        data.addProperty("playing", info.playing());
        data.addProperty("synced", info.synced());
        data.addProperty("offsetUs", info.offsetUs());
        data.addProperty("delayUs", info.delayUs());
        JsonArray outputs = new JsonArray();
        info.outputs().forEach(outputs::add);
        data.add("outputs", outputs);
        return data.toString();
    }

    static NodeInfo decode(JsonObject data) {
        List<String> outputs = new ArrayList<>();
        if (data.has("outputs"))
            data.getAsJsonArray("outputs").forEach(element -> outputs.add(element.getAsString()));
        return new NodeInfo(
                data.get("id").getAsString(),
                data.get("name").getAsString(),
                data.get("framerate").getAsInt(),
                outputs,
                data.get("playing").getAsBoolean(),
                data.get("synced").getAsBoolean(),
                data.get("offsetUs").getAsLong(),
                data.get("delayUs").getAsLong(),
                System.currentTimeMillis()
        );
    }

    @Override
    public void shutdown() {
        scheduler.shutdownNow();
    }

    /**
     * {@code lastSeen} is stamped by the receiver, so staleness never depends on peers' wall clocks
     * agreeing.
     */
    record NodeInfo(String id, String name, int framerate, List<String> outputs, boolean playing, boolean synced,
                    long offsetUs, long delayUs, long lastSeen) {

        /**
         * Ignores clock jitter, which would otherwise push an update to the UI on every heartbeat.
         */
        boolean differsStructurally(NodeInfo other) {
            return framerate != other.framerate
                    || playing != other.playing
                    || synced != other.synced
                    || !outputs.equals(other.outputs)
                    || !name.equals(other.name);
        }
    }
}

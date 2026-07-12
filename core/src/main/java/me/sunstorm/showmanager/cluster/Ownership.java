package me.sunstorm.showmanager.cluster;

import me.sunstorm.showmanager.settings.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Which output types this node may physically drive. Backed by the per-node {@code cluster.outputs}
 * config list; empty (or cluster disabled) means this node owns everything, so standalone behaviour
 * is unchanged. Output modules call {@link #owns} right before acting.
 */
@Singleton
public class Ownership {
    private static final Logger log = LoggerFactory.getLogger(Ownership.class);

    private final Set<OutputType> owned = EnumSet.noneOf(OutputType.class);

    @Inject
    public Ownership(Config config) {
        var cluster = config.getClusterConfig();
        if (!cluster.isEnabled() || cluster.getOutputs().isEmpty()) {
            owned.addAll(EnumSet.allOf(OutputType.class));
        } else {
            for (String name : cluster.getOutputs()) {
                try {
                    owned.add(OutputType.valueOf(name.trim().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    log.warn("[cluster] unknown output type '{}'", name);
                }
            }
        }
        log.info("[cluster] owned outputs: {}", owned);
    }

    public boolean owns(OutputType type) {
        return owned.contains(type);
    }

    public Set<OutputType> getOwned() {
        return Collections.unmodifiableSet(owned);
    }
}

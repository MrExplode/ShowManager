package me.sunstorm.showmanager.settings.config;

import java.util.ArrayList;
import java.util.List;

public class ClusterConfig {
    private boolean enabled = false;
    private String clusterName = "showmanager";
    private String nodeName = "";
    private String bindAddress = "";
    private int port = 7800;
    private boolean useMulticast = true;
    private List<String> seedNodes = new ArrayList<>();
    private List<String> outputs = new ArrayList<>();

    public boolean isEnabled() {
        return enabled;
    }

    public String getClusterName() {
        return clusterName;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getBindAddress() {
        return bindAddress;
    }

    public int getPort() {
        return port;
    }

    public boolean isUseMulticast() {
        return useMulticast;
    }

    public List<String> getSeedNodes() {
        return seedNodes;
    }

    public List<String> getOutputs() {
        return outputs;
    }
}

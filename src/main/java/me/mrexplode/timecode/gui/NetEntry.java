package me.mrexplode.timecode.gui;

import java.net.InetAddress;

public class NetEntry {
    
    private InetAddress networkAddress;
    private String name;
    
    public NetEntry(InetAddress networkAddress, String name) {
        this.networkAddress = networkAddress;
        this.name = name;
    }

    
    public InetAddress getNetworkAddress() {
        return networkAddress;
    }

    
    public void setNetworkAddress(InetAddress networkAddress) {
        this.networkAddress = networkAddress;
    }

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }

}

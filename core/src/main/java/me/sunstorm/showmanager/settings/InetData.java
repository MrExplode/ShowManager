package me.sunstorm.showmanager.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.InetAddress;

@Getter
@AllArgsConstructor
public class InetData {
    private final String name;
    private final InetAddress address;
}

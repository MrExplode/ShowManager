package me.sunstorm.showmanager.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.net.InetAddress;

@Getter
@ToString
@AllArgsConstructor
public class InetData {
    private final String name;
    private final InetAddress address;
}

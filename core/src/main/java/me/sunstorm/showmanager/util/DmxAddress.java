package me.sunstorm.showmanager.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DmxAddress {
    private final int universe;
    private final int subnet;
    private final int address;
}

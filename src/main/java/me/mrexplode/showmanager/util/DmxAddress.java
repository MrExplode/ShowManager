package me.mrexplode.showmanager.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

public @Getter
@AllArgsConstructor
class DmxAddress {
    private final int universe;
    private final int subnet;
    private final int address;
}

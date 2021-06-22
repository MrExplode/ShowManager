package me.sunstorm.showmanager.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Tuple<K, V> {
    private final K first;
    private final V second;
}

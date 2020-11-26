package me.mrexplode.showmanager.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Tuple<K, V> {
    K first;
    V second;
}

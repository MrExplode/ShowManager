package me.sunstorm.showmanager.cluster.serial;

public interface Codec<T> {

    byte[] encode(T message);

    T decode(byte[] message);
}

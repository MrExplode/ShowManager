package me.sunstorm.showmanager.redis.converter;

public interface Converter<T> {

    byte[] encode(T message);

    T decode(byte[] message);
}

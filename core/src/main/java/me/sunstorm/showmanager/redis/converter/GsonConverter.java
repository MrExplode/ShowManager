package me.sunstorm.showmanager.redis.converter;

public class GsonConverter<T> implements Converter<T> {
    @Override
    public byte[] encode(T message) {
        return new byte[0];
    }

    @Override
    public T decode(byte[] message) {
        return null;
    }
}

package me.sunstorm.showmanager.redis;

import me.sunstorm.showmanager.redis.converter.Converter;
import me.sunstorm.showmanager.redis.converter.GsonConverter;

public interface MessageHandler<T> {

    String getChannel();

    default Converter<T> getConverter() {
        return new GsonConverter<>();
    }

    void handleMessage(T message);
}

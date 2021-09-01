package me.sunstorm.showmanager.redis.impl;

import me.sunstorm.showmanager.redis.MessageHandler;
import me.sunstorm.showmanager.redis.Redis;

public class DummyRedisImpl implements Redis {
    @Override
    public void addMessageHandler(MessageHandler<?> handler) {

    }

    @Override
    public void removeHandler(MessageHandler<?> handler) {

    }

    @Override
    public <T> void sendMessage(T message, MessageHandler<T> handler) {

    }
}

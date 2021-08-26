package me.sunstorm.showmanager.redis;

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

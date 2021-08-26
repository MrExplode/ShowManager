package me.sunstorm.showmanager.redis;

public interface Redis {

    void addMessageHandler(MessageHandler<?> handler);

    void removeHandler(MessageHandler<?> handler);

    <T> void sendMessage(T message, MessageHandler<T> handler);
}

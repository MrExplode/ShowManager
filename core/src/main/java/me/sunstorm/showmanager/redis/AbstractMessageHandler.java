package me.sunstorm.showmanager.redis;

public abstract class AbstractMessageHandler<T> implements MessageHandler<T> {
    private final String channel;

    public AbstractMessageHandler(String channel) {
        this.channel = channel;
    }

    @Override
    public String getChannel() {
        return channel;
    }
}

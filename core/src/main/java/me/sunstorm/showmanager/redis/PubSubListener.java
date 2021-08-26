package me.sunstorm.showmanager.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.BinaryJedisPubSub;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public class PubSubListener extends BinaryJedisPubSub {
    private final Set<String> subscribed = ConcurrentHashMap.newKeySet();
    private final RedisImpl redis;

    @Override
    public void subscribe(byte[]... channels) {
        for (byte[] channel : channels) {
            String channelName = new String(channel, StandardCharsets.UTF_8).intern();
            if (this.subscribed.add(channelName)) {
                super.subscribe(channel);
            }
        }
    }

    @Override
    public void onSubscribe(byte[] channel, int subscribedChannels) {
        String channelName = new String(channel, StandardCharsets.UTF_8);
        log.info("[redis] Subscribed to channel: {}", channelName);
    }

    @Override
    public void onUnsubscribe(byte[] channel, int subscribedChannels) {
        String channelName = new String(channel, StandardCharsets.UTF_8);
        log.info("[redis] Unsubscribed from channel: {}", channelName);
        subscribed.remove(channelName);
    }

    @Override
    public void onMessage(byte[] channel, byte[] message) {
        String channelName = new String(channel, StandardCharsets.UTF_8);
        redis.incomingMessage(channelName, message);
    }
}

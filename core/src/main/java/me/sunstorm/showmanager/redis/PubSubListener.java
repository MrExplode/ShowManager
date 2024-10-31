package me.sunstorm.showmanager.redis;

import me.sunstorm.showmanager.redis.impl.RedisImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.BinaryJedisPubSub;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PubSubListener extends BinaryJedisPubSub {
    private static final Logger log = LoggerFactory.getLogger(PubSubListener.class);

    private final Set<String> subscribed = ConcurrentHashMap.newKeySet();
    private final RedisImpl redis;

    public PubSubListener(RedisImpl redis) {
        this.redis = redis;
    }

    // why did they make this final?
//    @Override
//    public void subscribe(byte[]... channels) {
//        for (byte[] channel : channels) {
//            String channelName = new String(channel, StandardCharsets.UTF_8).intern();
//            if (this.subscribed.add(channelName)) {
//                super.subscribe(channel);
//            }
//        }
//    }

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

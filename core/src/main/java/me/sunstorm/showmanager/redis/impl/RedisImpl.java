package me.sunstorm.showmanager.redis.impl;

import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.redis.converter.Converter;
import me.sunstorm.showmanager.redis.converter.GzipConverter;
import me.sunstorm.showmanager.redis.MessageHandler;
import me.sunstorm.showmanager.redis.PubSubListener;
import me.sunstorm.showmanager.redis.Redis;
import me.sunstorm.showmanager.redis.RedisCredentials;
import me.sunstorm.showmanager.terminable.Terminable;
import me.sunstorm.showmanager.util.Tuple;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.*;

// the redis handling design is loosely based on lucko helper-redis
// https://github.com/lucko/helper/tree/master/helper-redis
@Slf4j
public class RedisImpl implements Redis, Terminable {
    private final JedisPool jedisPool;
    private PubSubListener listener = null;
    private final Map<String, MessageHandler<?>> handlers = new ConcurrentHashMap<>();
    private final BlockingQueue<Tuple<byte[], byte[]>> sendQueue = new LinkedBlockingDeque<>();
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    public RedisImpl(@NotNull RedisCredentials credentials) {
        register();
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(16);

        jedisPool = new JedisPool(config, credentials.getAddress(), credentials.getPort(), 2000, credentials.getPassword());

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.ping();
        }

        executor.execute(new Runnable() {
            private boolean broken = false;

            @Override
            public void run() {
                if (broken) {
                    log.info("[redis] Retrying subscription...");
                    broken = false;
                }

                try (Jedis jedis = jedisPool.getResource()) {
                    try {
                        listener = new PubSubListener(RedisImpl.this);
                        jedis.psubscribe(listener, "showmanager".getBytes(StandardCharsets.UTF_8));
                    } catch (Exception e) {
                        log.error("Error subscribing to listener", e);
                        try {
                            listener.unsubscribe();
                        } catch (Exception ignored) {}
                        listener = null;
                        broken = true;
                    }
                }

                if (broken)
                    executor.schedule(this, 1, TimeUnit.SECONDS);
            }
        });

        executor.scheduleAtFixedRate(() -> {
            if (listener == null || !listener.isSubscribed())
                return;

            handlers.forEach((channel, handler) -> listener.subscribe(channel.getBytes(StandardCharsets.UTF_8)));
        }, 100, 100, TimeUnit.MILLISECONDS);

        startSendThread();
    }

    @Override
    public void addMessageHandler(MessageHandler<?> handler) {
        handlers.put(handler.getChannel(), handler);
        listener.subscribe(handler.getChannel().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void removeHandler(@NotNull MessageHandler<?> handler) {
        handlers.remove(handler.getChannel());
        listener.unsubscribe(handler.getChannel().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public <T> void sendMessage(T message, MessageHandler<T> handler) {
        if (!handlers.containsValue(handler)) {
            log.warn("Tried to send unknown message type: {}", message.getClass().getSimpleName());
            return;
        }
        Converter<T> converter = new GzipConverter<>(handler.getConverter());
        byte[] data = converter.encode(message);
        sendQueue.offer(new Tuple<>(handler.getChannel().getBytes(StandardCharsets.UTF_8), data));
    }

    public <T> void incomingMessage(String channel, byte[] message) {
        if (!handlers.containsKey(channel)) {
            log.warn("Received message on unknown channel");
            return;
        }
        MessageHandler<T> handler = (MessageHandler<T>) handlers.get(channel);
        try {
            T data = new GzipConverter<>(handler.getConverter()).decode(message);
            handler.handleMessage(data);
        } catch (Exception e) {
            log.error("Failed to handle redis message", e);
        }
    }

    private void startSendThread() {
        Thread t = new Thread(() -> {
            while (true) {
                try (Jedis jedis = jedisPool.getResource()) {
                    Tuple<byte[], byte[]> data = sendQueue.take();
                    jedis.publish(data.getFirst(), data.getSecond());
                } catch (InterruptedException e) {
                    log.error("SendQueue wait interrupted", e);
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void shutdown() throws InterruptedException {
        log.info("Shutting down Redis...");
        executor.shutdown();
        executor.awaitTermination(3, TimeUnit.SECONDS);
        jedisPool.close();
        sendQueue.clear();
        handlers.clear();
    }
}

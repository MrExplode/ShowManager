package me.sunstorm.showmanager.modules.http;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.google.gson.JsonObject;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Custom Logback appender, responsible for sending log entries to the frontend through websocket.
 * Entries are shipped structured rather than pre-rendered, so the UI can filter and fold them.
 */
public class WebSocketLogger extends UnsynchronizedAppenderBase<ILoggingEvent> {
    // when a frontend instance connects, it receives the last entries too (mainly used to see startup logs)
    private static final int CACHE_SIZE = 250;
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss.SSS").withZone(ZoneId.systemDefault());
    private static final List<JsonObject> logCache = new CopyOnWriteArrayList<>();
    private static final AtomicLong counter = new AtomicLong();

    @Override
    protected void append(ILoggingEvent event) {
        JsonObject entry = toJson(event);
        logCache.add(entry);
        while (logCache.size() > CACHE_SIZE)
            logCache.removeFirst();
        if (WebSocketHandler.INSTANCE != null)
            WebSocketHandler.INSTANCE.consumeLog(entry);
    }

    private static JsonObject toJson(ILoggingEvent event) {
        JsonObject entry = new JsonObject();
        entry.addProperty("id", counter.incrementAndGet());
        entry.addProperty("time", TIME.format(Instant.ofEpochMilli(event.getTimeStamp())));
        entry.addProperty("level", event.getLevel().toString());
        entry.addProperty("thread", event.getThreadName());
        entry.addProperty("logger", shortName(event.getLoggerName()));
        entry.addProperty("message", event.getFormattedMessage());
        if (event.getThrowableProxy() != null)
            entry.addProperty("throwable", ThrowableProxyUtil.asString(event.getThrowableProxy()));
        return entry;
    }

    /**
     * {@code me.sunstorm.showmanager.cluster.ClusterNodes} -> {@code ClusterNodes}.
     */
    private static String shortName(String logger) {
        if (logger == null)
            return "";
        int dot = logger.lastIndexOf('.');
        return dot == -1 ? logger : logger.substring(dot + 1);
    }

    public static List<JsonObject> getLogCache() {
        return logCache;
    }
}

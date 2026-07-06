package me.sunstorm.showmanager.modules.http;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Custom Logback appender, responsible for sending log entries to the frontend through websocket.
 */
public class WebSocketLogger extends UnsynchronizedAppenderBase<ILoggingEvent> {
    // when a frontend instance connects, it receives the last 100 log entries too (mainly used to see startup logs)
    private static final List<String> logCache = new CopyOnWriteArrayList<>();
    private Encoder<ILoggingEvent> encoder;

    @Override
    public void start() {
        if (encoder == null) {
            addError("No encoder set for the appender named [" + name + "].");
            return;
        }
        super.start();
    }

    @Override
    protected void append(ILoggingEvent event) {
        String log = new String(encoder.encode(event), StandardCharsets.UTF_8);
        logCache.add(log);
        if (logCache.size() > 100)
            logCache.removeFirst();
        if (WebSocketHandler.INSTANCE != null)
            WebSocketHandler.INSTANCE.consumeLog(log);
    }

    public void setEncoder(Encoder<ILoggingEvent> encoder) {
        this.encoder = encoder;
    }

    public static List<String> getLogCache() {
        return logCache;
    }
}

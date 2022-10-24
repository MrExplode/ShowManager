package me.sunstorm.showmanager.modules.http;

import lombok.Getter;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Custom Log4j2 appender, responsible for sending log entries to the frontend through websocket.
 */
@Plugin(name = WebSocketLogger.PLUGIN_NAME, category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class WebSocketLogger extends AbstractAppender {
    public static final String PLUGIN_NAME = "WebSocketLogger";
    // when a frontend instance connects, it receives the last 100 log entries too (mainly used to see startup logs)
    @Getter private static final List<String> logCache = new CopyOnWriteArrayList<>();

    protected WebSocketLogger(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    @Override
    public void append(LogEvent event) {
        String log = getLayout().toSerializable(event).toString();
        logCache.add(log);
        if (logCache.size() > 100)
            logCache.remove(0);
        if (WebSocketHandler.INSTANCE != null)
            WebSocketHandler.INSTANCE.consumeLog(log);
    }

    @PluginFactory
    public static WebSocketLogger createAppender(
            @Required(message = "No name provided for WebSocketLogger") @PluginAttribute("name") String name,
            @PluginElement("Filter") Filter filter,
            @PluginElement("Layout") @Nullable Layout<? extends Serializable> layout,
            @PluginAttribute(value = "ignoreExceptions", defaultBoolean = true) boolean ignoreExceptions) {
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }

        return new WebSocketLogger(name, filter, layout, ignoreExceptions);
    }
}

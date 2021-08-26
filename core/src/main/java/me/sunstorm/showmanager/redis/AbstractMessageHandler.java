package me.sunstorm.showmanager.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class AbstractMessageHandler<T> implements MessageHandler<T> {
    private final String channel;
}

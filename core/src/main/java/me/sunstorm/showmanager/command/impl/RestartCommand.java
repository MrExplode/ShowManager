package me.sunstorm.showmanager.command.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.command.AbstractCommand;
import me.sunstorm.showmanager.terminable.Terminables;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RestartCommand extends AbstractCommand {

    public RestartCommand() {
        super("restart");
    }

    @Override
    @SneakyThrows
    public void execute(List<String> args) {
        log.info("Performing restart...");
        Terminables.shutdownAll();
        System.gc();
        for (int i = 1; i <= 5; i++) {
            TimeUnit.SECONDS.sleep(1);
            log.info(". ".repeat(i));
        }
        invokeBoostrap();
    }

    private void invokeBoostrap() {
        try {
            Class<?> bClass = Class.forName("me.sunstorm.showmanager.Bootstrap");
            Field lastArgField = bClass.getDeclaredField("lastArgs");
            Method mainMethod = bClass.getDeclaredMethod("main", String[].class);
            mainMethod.invoke(null, lastArgField.get(null));
        } catch (ReflectiveOperationException e) {
            log.error("Failed to invoke Bootstrap", e);
        }
    }
}

package me.sunstorm.showmanager.command.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.ShowManager;
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
        ShowManager.getInstance().reload(true);
    }
}

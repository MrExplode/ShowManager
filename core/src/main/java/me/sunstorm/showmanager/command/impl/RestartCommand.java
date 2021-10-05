package me.sunstorm.showmanager.command.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.ShowManager;
import me.sunstorm.showmanager.command.AbstractCommand;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;

import java.util.List;

@Slf4j
public class RestartCommand extends AbstractCommand implements InjectRecipient {
    @Inject
    private ShowManager sm;

    public RestartCommand() {
        super("restart");
        inject();
    }

    @Override
    @SneakyThrows
    public void execute(List<String> args) {
        log.info("Performing restart...");
        sm.reload(true);
    }
}

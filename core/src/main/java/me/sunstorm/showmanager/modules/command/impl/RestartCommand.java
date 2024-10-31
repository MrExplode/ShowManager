package me.sunstorm.showmanager.modules.command.impl;

import me.sunstorm.showmanager.ShowManager;
import me.sunstorm.showmanager.modules.command.AbstractCommand;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RestartCommand extends AbstractCommand implements InjectRecipient {
    private static final Logger log = LoggerFactory.getLogger(RestartCommand.class);

    @Inject
    private ShowManager sm;

    public RestartCommand() {
        super("restart");
        inject();
    }

    @Override
    public void execute(List<String> args) {
        log.info("Performing restart...");
        sm.reload(true);
    }
}

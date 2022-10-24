package me.sunstorm.showmanager.modules.command.impl;

import me.sunstorm.showmanager.Worker;
import me.sunstorm.showmanager.modules.command.AbstractCommand;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;

import java.util.List;

public class PauseCommand extends AbstractCommand implements InjectRecipient {
    @Inject
    private Worker worker;

    public PauseCommand() {
        super("pause");
        inject();
    }

    @Override
    public void execute(List<String> args) {
        worker.pause();
    }
}

package me.sunstorm.showmanager.command.impl;

import me.sunstorm.showmanager.ShowManager;
import me.sunstorm.showmanager.command.AbstractCommand;

import java.util.List;

public class StopCommand extends AbstractCommand {

    public StopCommand() {
        super("stop");
    }

    @Override
    public void execute(List<String> args) {
        ShowManager.getInstance().getWorker().stop();
    }
}

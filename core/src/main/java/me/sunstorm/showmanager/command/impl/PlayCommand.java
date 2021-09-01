package me.sunstorm.showmanager.command.impl;

import me.sunstorm.showmanager.ShowManager;
import me.sunstorm.showmanager.command.AbstractCommand;

import java.util.List;

public class PlayCommand extends AbstractCommand {

    public PlayCommand() {
        super("play");
    }

    @Override
    public void execute(List<String> args) {
        ShowManager.getInstance().getWorker().play();
    }
}

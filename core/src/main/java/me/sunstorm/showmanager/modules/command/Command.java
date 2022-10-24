package me.sunstorm.showmanager.modules.command;

import java.util.List;

public interface Command {

    String getName();

    void execute(List<String> args);
}

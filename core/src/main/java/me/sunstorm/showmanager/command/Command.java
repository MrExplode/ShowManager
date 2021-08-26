package me.sunstorm.showmanager.command;

import java.util.List;

public interface Command {

    String getName();

    void execute(List<String> args);
}

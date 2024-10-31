package me.sunstorm.showmanager.modules.command;

import me.sunstorm.showmanager.modules.command.impl.PauseCommand;
import me.sunstorm.showmanager.modules.command.impl.PlayCommand;
import me.sunstorm.showmanager.modules.command.impl.RestartCommand;
import me.sunstorm.showmanager.modules.command.impl.StopCommand;
import net.minecrell.terminalconsole.SimpleTerminalConsole;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CommandModule extends SimpleTerminalConsole {
    private final Map<String, Command> commands = new HashMap<>();

    public CommandModule() {
        registerCommand(new PlayCommand());
        registerCommand(new PauseCommand());
        registerCommand(new StopCommand());
        registerCommand(new RestartCommand());
    }

    public void registerCommand(@NotNull Command command) {
        if (commands.containsKey(command.getName()))
            throw new IllegalArgumentException("Command already registered");
        commands.put(command.getName(), command);
    }

    @Override
    protected boolean isRunning() {
        return true;
    }

    @Override
    protected void runCommand(String command) {
        //TODO?
    }

    @Override
    protected void shutdown() {
        System.exit(0);
    }
}

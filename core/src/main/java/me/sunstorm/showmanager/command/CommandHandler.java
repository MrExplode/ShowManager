package me.sunstorm.showmanager.command;

import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.command.impl.PauseCommand;
import me.sunstorm.showmanager.command.impl.PlayCommand;
import me.sunstorm.showmanager.command.impl.StopCommand;
import net.minecrell.terminalconsole.SimpleTerminalConsole;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CommandHandler extends SimpleTerminalConsole {
    private final Map<String, Command> commands = new HashMap<>();

    public CommandHandler() {
        registerCommand(new PlayCommand());
        registerCommand(new PauseCommand());
        registerCommand(new StopCommand());
    }

    private void registerCommand(@NotNull Command command) {
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
        //
    }

    @Override
    protected void shutdown() {
        System.exit(0);
    }
}

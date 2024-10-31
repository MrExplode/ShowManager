package me.sunstorm.showmanager.modules.command;

public abstract class AbstractCommand implements Command {
    private final String name;

    public AbstractCommand(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}

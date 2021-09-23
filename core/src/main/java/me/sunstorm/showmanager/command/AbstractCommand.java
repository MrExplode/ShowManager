package me.sunstorm.showmanager.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class AbstractCommand implements Command {
    private final String name;
}

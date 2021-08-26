package me.sunstorm.showmanager.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public abstract class AbstractCommand implements Command {
    private final String name;
}

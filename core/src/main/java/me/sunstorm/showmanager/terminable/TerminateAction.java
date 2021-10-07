package me.sunstorm.showmanager.terminable;

@FunctionalInterface
public interface TerminateAction {
    void terminate() throws Throwable;
}

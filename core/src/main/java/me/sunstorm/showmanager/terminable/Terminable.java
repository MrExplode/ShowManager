package me.sunstorm.showmanager.terminable;

public interface Terminable {

    void shutdown() throws Exception;

    default void register() {
        Terminables.addTerminable(this);
    }
}

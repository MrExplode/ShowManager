package me.sunstorm.showmanager.injection;

public interface InjectRecipient {
    default void inject() {
        DependencyInjection.performInjection(this);
    }
}

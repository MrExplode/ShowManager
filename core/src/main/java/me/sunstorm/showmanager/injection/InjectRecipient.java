package me.sunstorm.showmanager.injection;

public interface InjectRecipient {
    default void inject() {
        inject(true);
    }

    default void inject(boolean watchUpdate) {
        DependencyInjection.performInjection(this, watchUpdate);
    }
}

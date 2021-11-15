package me.sunstorm.showmanager.http.routing.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Prefixes all handler paths in the marked class with the {@link #value()}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PathPrefix {
    String value();
}

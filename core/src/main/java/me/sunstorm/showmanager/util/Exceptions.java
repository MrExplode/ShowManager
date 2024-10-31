package me.sunstorm.showmanager.util;

import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Exception utilities.
 */
public interface Exceptions {

    /**
     * Rethrows the {@link Throwable} without wrapping it into {@link RuntimeException} (or it's subclasses).
     *
     * @param t the throwable
     */
    static void sneaky(@NotNull Throwable t) {
        throw Exceptions.superSneaky(t);
    }

    /**
     * Returns the {@link Throwable}'s stacktrace as a {@link String} using {@link StringWriter}.
     *
     * @param t the throwable
     * @return the stack trace
     */
    @NotNull
    static String toString(@NotNull Throwable t) {
        StringWriter string = new StringWriter();
        PrintWriter writer = new PrintWriter(string);
        t.printStackTrace(writer);
        return string.toString();
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> T superSneaky(@NotNull Throwable t) throws T {
        throw (T) t;
    }
}

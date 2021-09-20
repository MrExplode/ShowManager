package me.sunstorm.showmanager;

import me.sunstorm.showmanager.terminable.Terminable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TerminableTests {

    Terminable terminable = () -> { };

    @Test
    public void testRegister() {
        assertNotNull(terminable);
        terminable.register();
        assertThrows(IllegalArgumentException.class, () -> terminable.register());
    }
}

package me.sunstorm.showmanager;

import me.sunstorm.showmanager.terminable.Terminable;
import me.sunstorm.showmanager.terminable.Terminables;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TerminableTests {

    @Test
    void terminableTest() throws Exception {
        Terminable t = mock(Terminable.class, Answers.CALLS_REAL_METHODS);
        t.register();
        Terminables.shutdownAll();
        verify(t).shutdown();
    }

    @Test
    void staticTerminableTest() {
        //nah.
    }
}

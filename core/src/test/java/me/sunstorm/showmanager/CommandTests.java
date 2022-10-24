package me.sunstorm.showmanager;

import me.sunstorm.showmanager.modules.command.CommandModule;
import me.sunstorm.showmanager.modules.command.impl.PauseCommand;
import me.sunstorm.showmanager.modules.command.impl.PlayCommand;
import me.sunstorm.showmanager.modules.command.impl.RestartCommand;
import me.sunstorm.showmanager.modules.command.impl.StopCommand;
import me.sunstorm.showmanager.injection.DependencyInjection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;

import java.util.Collections;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CommandTests {
    private ShowManager showManager;
    private Worker worker;

    @BeforeAll
    void setupDependencies() {
        showManager = mock(ShowManager.class);
        worker = mock(Worker.class);
        DependencyInjection.registerProvider(ShowManager.class, () -> showManager);
        DependencyInjection.registerProvider(Worker.class, () -> worker);
    }

    @Test
    void testHandler() {
        CommandModule handler = new CommandModule();
        assertThatThrownBy(() -> handler.registerCommand(new PauseCommand())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testPauseCommand() {
        new PauseCommand().execute(Collections.emptyList());
        verify(worker).pause();
    }

    @Test
    void testPlayCommand() {
        new PlayCommand().execute(Collections.emptyList());
        verify(worker).play();
    }

    @Test
    void testRestartCommand() {
        new RestartCommand().execute(Collections.emptyList());
        ArgumentCaptor<Boolean> arg = ArgumentCaptor.forClass(Boolean.class);
        verify(showManager).reload(arg.capture());
        assertThat(arg.getValue()).isEqualTo(true);
    }

    @Test
    void testStopCommand() {
        new StopCommand().execute(Collections.emptyList());
        verify(worker).stop();
    }
}

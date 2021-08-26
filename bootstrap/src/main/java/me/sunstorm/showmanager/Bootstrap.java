package me.sunstorm.showmanager;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.util.Arrays;

@Slf4j
public class Bootstrap {

    public static void main(String... args) {
        log.info("Starting ShowManager...");
        String ver = ManagementFactory.getRuntimeMXBean().getVmVersion();
        if (Integer.parseInt(ver.substring(0, 2)) < 11) {
            log.error("Outdated Java JVM: " + ver);
            log.error("Please use Java 11 or newer");
            return;
        }

        OptionParser parser = new OptionParser();
        parser.accepts("session-master");
        parser.accepts("client");
        OptionSpec<String> ignored = parser.nonOptions();
        OptionSet optionSet = parser.parse(args);
        if (!optionSet.valuesOf(ignored).isEmpty())
            System.out.println("Ignored arguments: " + Arrays.toString(optionSet.valuesOf(ignored).toArray()));

        try {
            Class<?> mainClass = Class.forName("me.sunstorm.showmanager.ShowManager");
            Constructor<?> constructor = mainClass.getConstructor();
            constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            log.error("Failed to bootstrap ShowManager", e);
        }
    }

}

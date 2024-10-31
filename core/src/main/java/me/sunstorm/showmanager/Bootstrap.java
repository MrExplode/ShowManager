package me.sunstorm.showmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap {
    private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);

    public static void main(String[] args) {
        log.info("Loading ShowManager...");
        try {
            new ShowManager();
        } catch (Throwable t) {
            log.error("Fatal error", t);
        }
    }
}

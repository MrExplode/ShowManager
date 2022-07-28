package me.sunstorm.showmanager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Loader {

    public static void main(String[] args) {
        log.info("Loading ShowManager...");
        try {
            new ShowManager();
        } catch (Throwable t) {
            log.error("Fatal error", t);
        }
    }
}

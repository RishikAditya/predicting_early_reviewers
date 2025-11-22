package com.earlyreviewer.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * LoggerUtil provides centralized logging configuration.
 * Sets up java.util.logging to write to both console and file.
 */
public class LoggerUtil {
    private static boolean initialized = false;

    /**
     * Initializes the logging system. Should be called once at application startup.
     */
    public static void initialize() {
        if (initialized) {
            return;
        }

        try {
            // Get root logger
            Logger rootLogger = Logger.getLogger("");
            rootLogger.setLevel(Level.INFO);

            // Remove default handler
            Handler[] handlers = rootLogger.getHandlers();
            for (Handler handler : handlers) {
                rootLogger.removeHandler(handler);
            }

            // Add console handler
            Handler consoleHandler = new java.util.logging.ConsoleHandler();
            consoleHandler.setLevel(Level.INFO);
            consoleHandler.setFormatter(new SimpleFormatter());
            rootLogger.addHandler(consoleHandler);

            // Add file handler
            FileHandler fileHandler = new FileHandler("early_reviewer.log", true);
            fileHandler.setLevel(Level.INFO);
            fileHandler.setFormatter(new SimpleFormatter());
            rootLogger.addHandler(fileHandler);

            initialized = true;
        } catch (IOException e) {
            System.err.println("Failed to initialize logging: " + e.getMessage());
        }
    }

    /**
     * Gets a logger for the specified class.
     */
    public static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger(clazz.getName());
    }
}

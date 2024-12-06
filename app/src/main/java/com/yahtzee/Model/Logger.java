package com.yahtzee.Model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A singleton Logger class used for logging messages.
 * This class provides a centralized way to log messages throughout the application.
 */
public class Logger {
    private static final Logger INSTANCE = new Logger();
    private static final List<String> logs = new ArrayList<>();

    /**
     * Private constructor to prevent instantiation from outside.
     */
    private Logger() {
    }

    /**
     * Returns the singleton instance of the Logger.
     * This method ensures that only one instance of Logger exists throughout the application.
     *
     * @return The singleton instance of the Logger.
     */
    public static Logger getInstance() {
        return INSTANCE;
    }

    /**
     * Logs a message by adding it to the list of logs.
     *
     * @param message The message to be logged.
     */
    public static void log(String message) {
        logs.add(message);
    }

    /**
     * Returns a string representation of all logged messages.
     * The logs are joined by new lines to represent each log entry.
     *
     * @return A string containing all log messages, each on a new line.
     */
    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String log : logs) {
            sb.append(log).append("\n");
        }
        return sb.toString();
    }
}

package com.yahtzee.Model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Logger {
    private static final Logger INSTANCE = new Logger();
    private static final List<String> logs = new ArrayList<>();

    private Logger() {
    }

    public static Logger getInstance() {
        return INSTANCE;
    }

    public static void log(String message) {
        logs.add(message);
    }

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

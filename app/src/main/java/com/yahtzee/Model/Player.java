package com.yahtzee.Model;

import androidx.annotation.NonNull;

public class Player {
    private final String name;
    private final boolean isComputer;

    public Player(String name, boolean isComputer) {
        this.name = name;
        this.isComputer = isComputer;
    }

    public String getName() {
        return name;
    }

    public boolean isComputer() {
        return isComputer;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Player)) {
            return false;
        }

        Player player = (Player) obj;
        return name.equals(player.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}

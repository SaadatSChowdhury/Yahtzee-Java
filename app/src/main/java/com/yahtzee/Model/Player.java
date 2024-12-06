package com.yahtzee.Model;

import androidx.annotation.NonNull;

/**
 * Represents a player in the game, either a human or a computer.
 */
public class Player {
    private final String name;
    private final boolean isComputer;

    /**
     * Constructs a Player instance with the specified name and type (human or computer).
     *
     * @param name The name of the player.
     * @param isComputer A boolean flag indicating if the player is a computer.
     */
    public Player(String name, boolean isComputer) {
        this.name = name;
        this.isComputer = isComputer;
    }

    /**
     * Returns the name of the player.
     *
     * @return The name of the player.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a boolean flag indicating if the player is a computer.
     *
     * @return True if the player is a computer, false otherwise.
     */
    public boolean isComputer() {
        return isComputer;
    }

    /**
     * Returns a string representation of the player, which is their name.
     *
     * @return The name of the player.
     */
    @NonNull
    @Override
    public String toString() {
        return name;
    }

    /**
     * Checks if this player is equal to another player.
     * Two players are considered equal if their names are the same.
     *
     * @param obj The object to compare with.
     * @return True if the players have the same name, otherwise false.
     */
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

    /**
     * Returns a hash code value for the player.
     * The hash code is based on the player's name.
     *
     * @return The hash code of the player.
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}

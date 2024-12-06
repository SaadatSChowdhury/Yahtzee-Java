package com.yahtzee.Model;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * Represents help information to assist the player in making decisions during the game.
 * This class provides details on which category to target, which dice to keep, and whether to stand or reroll.
 */
public class Help {
    private final Category targetCategory;
    private final List<Integer> diceToKeep;
    private final List<Integer> keptDice;
    private final List<Integer> rolledDice;
    private boolean stand = false;

    /**
     * Constructs a new Help object with the specified parameters.
     *
     * @param targetCategory The category that is being targeted for scoring.
     * @param diceToKeep The list of dice that should be kept.
     * @param keptDice The list of dice that are currently kept.
     * @param rolledDice The list of dice that were rolled in the current turn.
     * @param stand A boolean indicating whether the player should stand (not reroll any dice).
     */
    public Help(Category targetCategory, List<Integer> diceToKeep, List<Integer> keptDice, List<Integer> rolledDice, boolean stand) {
        this.targetCategory = targetCategory;
        this.diceToKeep = diceToKeep;
        this.keptDice = keptDice;
        this.rolledDice = rolledDice;
        this.stand = stand;

    }

    /**
     * Returns the target category that is being pursued in this turn.
     *
     * @return The target category.
     */
    public Category getTargetCategory() {
        return targetCategory;
    }

    /**
     * Returns the list of dice that should be kept in the next roll.
     *
     * @return The list of dice to keep.
     */
    public List<Integer> getDiceToKeep() {
        return diceToKeep;
    }

    /**
     * Returns the list of dice that have been kept by the player so far.
     *
     * @return The list of kept dice.
     */
    public List<Integer> getKeptDice() {
        return keptDice;
    }

    /**
     * Returns the list of dice that were rolled in the current turn.
     *
     * @return The list of rolled dice.
     */
    public List<Integer> getRolledDice() {
        return rolledDice;
    }

    /**
     * Returns whether the player should stand (not reroll any dice) based on the current strategy.
     *
     * @return True if the player should stand, false if the player should reroll.
     */
    public boolean getStand() {
        return stand;
    }

    /**
     * Returns a string representation of the Help object.
     *
     * @return A string representation of the Help object.
     */
    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Target Category: ").append(targetCategory).append("\n");
        sb.append("Dice to Keep: ").append(diceToKeep).append("\n");
        sb.append(stand ? "You should stand" : "You should not stand").append("\n");

        return sb.toString();
    }

}



package com.yahtzee.Model;

import java.util.Random;

/**
 * Represents a single die in the game of Yahtzee. This class handles the value of the die,
 * whether it has been kept, and whether it is marked for certain actions like being kept
 * in the help system.
 */
public class Die {
    private int value;
    private boolean kept;

    private boolean marked;
    private boolean markedForHelpKeep;

    /**
     * Constructs a new Die object with an initial value of 0 and default states for
     * kept and marked flags.
     */
    public Die() {
        value = 0;
        kept = false;
        marked = false;
        markedForHelpKeep = false;
    }

    /**
     * Returns the current value of the die.
     *
     * @return The value of the die (between 1 and 6).
     */
    public int getValue() {
        return value;
    }

    /**
     * Sets the value of the die.
     * If the value is less than 1, it sets the value to 6;
     * if greater than 6, it sets the value to 1;
     * otherwise, it sets the value to the given value.
     *
     * @param value The value to set (between 1 and 6).
     */
    public void setValue(int value) {
        if (value < 1) {
            this.value = 6;
        } else if (value > 6) {
            this.value = 1;
        } else {
            this.value = value;
        }
    }

    /**
     * Returns whether the die has been kept (i.e., whether it should not be rolled again).
     *
     * @return True if the die has been kept, false otherwise.
     */
    public boolean isKept() {
        return kept;
    }

    /**
     * Sets whether the die has been kept.
     *
     * @param kept True if the die should be kept, false otherwise.
     */
    public void setKept(boolean kept) {
        this.kept = kept;
        this.markedForHelpKeep = false;
    }

    /**
     * Returns whether the die is marked (used for visual or logical indication).
     *
     * @return True if the die is marked, false otherwise.
     */
    public boolean isMarked() {
        return marked;
    }

    /**
     * Sets whether the die is marked.
     *
     * @param marked True if the die should be marked, false otherwise.
     */
    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    /**
     * Returns whether the die is marked for help (used for visual or logical indication).
     *
     * @return True if the die is marked for help, false otherwise.
     */
    public boolean isMarkedForHelpKeep() {
        return markedForHelpKeep;
    }

    /**
     * Sets whether the die is marked for help.
     *
     * @param markedForHelpKeep True if the die should be marked for help, false otherwise.
     */
    public void setMarkedForHelpKeep(boolean markedForHelpKeep) {
        this.markedForHelpKeep = markedForHelpKeep;
    }

    /**
     * Rolls the die to generate a new random value between 1 and 6.
     */
    public void roll() {
        value = new Random().nextInt(6) + 1;
    }

    /**
     * Resets the die to its initial state (value of 0, not kept, not marked).
     */
    public void reset() {
        value = 0;
        kept = false;
        marked = false;
        markedForHelpKeep = false;
    }
}

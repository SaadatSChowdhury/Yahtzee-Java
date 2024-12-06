package com.yahtzee.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a set of five dice in the game of Yahtzee. This class handles rolling the dice,
 * keeping or unkeeping individual dice, marking dice for special actions, and retrieving dice values.
 */
public class DiceRoll {
    private final List<Die> dice;

    /**
     * Constructs a new DiceRoll object with 5 dice.
     * Each die is initialized with default values.
     */
    public DiceRoll() {
        dice = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            dice.add(new Die());
        }
    }

    /**
     * Returns the list of dice in the DiceRoll.
     *
     * @return The list of dice.
     */
    public List<Die> getDice() {
        return dice;
    }

    /**
     * Rolls all dice that are not marked or kept.
     * Resets the marked status for any die marked for help keeping.
     */
    public void roll() {
        for (Die die : dice) {
            if (!die.isKept() && !die.isMarked()) {
                die.roll();
                die.setMarkedForHelpKeep(false);
            }
        }
    }

    /**
     * Keeps a specific die by its index.
     * This method marks the die as kept, preventing it from being rolled again.
     *
     * @param index The index of the die to keep.
     */
    public void keep(int index) {
        dice.get(index).setKept(dice.get(index).isKept());
    }

    /**
     * Keeps all dice, marking all dice as kept, preventing any further rolls.
     */
    public void keepAll() {
        for (Die die : dice) {
            die.setKept(true);
        }
    }

    /**
     * Checks if all dice in the roll have been kept.
     *
     * @return True if all dice are kept, false otherwise.
     */
    public boolean isAllDiceKept() {
        for (Die die : dice) {
            if (!die.isKept()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all dice have been rolled (i.e., if each die has a non-zero value).
     *
     * @return True if all dice have been rolled, false otherwise.
     */
    public boolean isAllDiceRolled() {
        for (Die die : dice) {
            if (die.getValue() == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Marks all dice, preventing any further rolls.
     */
    public void unKeepAll() {
        for (Die die : dice) {
            die.setKept(false);
        }
    }

    /**
     * Keeps all marked dice in the roll.
     * This method iterates through the dice and sets the `kept` property to true for any die that is currently marked.
     * It also resets the `marked` property to false after keeping the die.
     */
    public void keepMarked() {
        for (Die die : dice) {
            if (die.isMarked()) {
                die.setKept(true);
                die.setMarked(false);
            }
        }
    }

    /**
     * Resets all dice in the roll.
     * This method iterates through the dice and calls the `reset()` method on each die,
     * effectively setting its `kept` and `marked` properties to false and generating a new random value.
     */
    public void reset() {
        for (Die die : dice) {
            die.reset();
        }
    }

    /**
     * Resets all unkept dice in the roll.
     * This method iterates through the dice and calls the `reset()` method on each die that is not kept,
     * effectively setting its `marked` property to false and generating a new random value.
     */
    public void resetUnkept() {
        for (Die die : dice) {
            if (!die.isKept()) {
                die.reset();
            }
        }
    }

    /**
     * Returns a list of the values of all kept dice in the roll.
     *
     * @return The list of kept dice values.
     */
    public List<Integer> getKeptDiceValues() {
        return dice.stream()
                .filter(Die::isKept)
                .map(Die::getValue)
                .toList();
    }

    /**
     * Returns a list of values for all the dice that are marked.
     *
     * @return A list of values of marked dice.
     */
    public List<Integer> getMarkedDiceValues() {
        return dice.stream()
                .filter(Die::isMarked)
                .map(Die::getValue)
                .toList();
    }

    /**
     * Returns a list of values for all the dice that are marked.
     *
     * @return A list of values of marked dice.
     */
    public List<Integer> getRolledDiceValues() {
        return dice.stream()
                .filter(die -> !die.isKept())
                .map(Die::getValue)
                .toList();

    }

    /**
     * Returns a list of values for all the dice that are not marked.
     *
     * @return A list of values of unmarked dice.
     */
    public List<Die> getRolledDice() {
        return dice.stream()
                .filter(die -> !die.isKept())
                .toList();
    }

    /**
     * Resets the `markedForHelpKeep` property of all dice in the roll to false.
     * This is typically used to clear any markings made by a help function or similar feature.
     */
    public void unMarkAllForHelpKeep() {
        for (Die die : dice) {
            die.setMarkedForHelpKeep(false);
        }
    }
}


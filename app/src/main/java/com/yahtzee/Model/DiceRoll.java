package com.yahtzee.Model;

import java.util.ArrayList;
import java.util.List;

public class DiceRoll {
    private final List<Die> dice;

    public DiceRoll() {
        dice = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            dice.add(new Die());
        }
    }

    public List<Die> getDice() {
        return dice;
    }

    public void roll() {
        for (Die die : dice) {
            if (!die.isKept() && !die.isMarked()) {
                die.roll();
                die.setMarkedForHelpKeep(false);
            }
        }
    }

    public void keep(int index) {
        dice.get(index).setKept(dice.get(index).isKept());
    }

    public void keepAll() {
        for (Die die : dice) {
            die.setKept(true);
        }
    }

    public boolean isAllDiceKept() {
        for (Die die : dice) {
            if (!die.isKept()) {
                return false;
            }
        }
        return true;
    }

    public boolean isAllDiceRolled() {
        for (Die die : dice) {
            if (die.getValue() == 0) {
                return false;
            }
        }
        return true;
    }

    public void unKeepAll() {
        for (Die die : dice) {
            die.setKept(false);
        }
    }

    public void keepMarked() {
        for (Die die : dice) {
            if (die.isMarked()) {
                die.setKept(true);
                die.setMarked(false);
            }
        }
    }

    public void reset() {
        for (Die die : dice) {
            die.reset();
        }
    }

    public void resetUnkept() {
        for (Die die : dice) {
            if (!die.isKept()) {
                die.reset();
            }
        }
    }


    public List<Integer> getKeptDiceValues() {
        return dice.stream()
                .filter(Die::isKept)
                .map(Die::getValue)
                .toList();
    }

    public List<Integer> getMarkedDiceValues() {
        return dice.stream()
                .filter(Die::isMarked)
                .map(Die::getValue)
                .toList();
    }

    public List<Integer> getRolledDiceValues() {
        return dice.stream()
                .filter(die -> !die.isKept())
                .map(Die::getValue)
                .toList();

    }

    public List<Die> getRolledDice() {
        return dice.stream()
                .filter(die -> !die.isKept())
                .toList();
    }

    public void unMarkAllForHelpKeep() {
        for (Die die : dice) {
            die.setMarkedForHelpKeep(false);
        }
    }
}


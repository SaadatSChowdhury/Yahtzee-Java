package com.yahtzee.Model;

import androidx.annotation.NonNull;

import java.util.List;

public class Help {
    private final Category targetCategory;
    private final List<Integer> diceToKeep;
    private final List<Integer> keptDice;
    private final List<Integer> rolledDice;
    private boolean stand = false;

    public Help(Category targetCategory, List<Integer> diceToKeep, List<Integer> keptDice, List<Integer> rolledDice, boolean stand) {
        this.targetCategory = targetCategory;
        this.diceToKeep = diceToKeep;
        this.keptDice = keptDice;
        this.rolledDice = rolledDice;
        this.stand = stand;

    }

    public Category getTargetCategory() {
        return targetCategory;
    }

    public List<Integer> getDiceToKeep() {
        return diceToKeep;
    }

    public List<Integer> getKeptDice() {
        return keptDice;
    }

    public List<Integer> getRolledDice() {
        return rolledDice;
    }

    public boolean getStand() {
        return stand;
    }

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



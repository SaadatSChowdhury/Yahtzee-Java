package com.yahtzee.Model.Categories;

import androidx.annotation.NonNull;

import com.yahtzee.Model.Category;

import java.util.List;

public class SmallStraight implements Category {
    private static final SmallStraight INSTANCE = new SmallStraight();

    private SmallStraight() {
    }

    public static SmallStraight getInstance() {
        return INSTANCE;
    }

    @NonNull
    @Override
    public String toString() {
        return "Small Straight";
    }

    @Override
    public int calculateScore(List<Integer> dice) {
        if (isValid(dice)) {
            return 30;
        }
        return 0;
    }

    @Override
    public boolean isValid(List<Integer> dice) {
        return dice.containsAll(List.of(1, 2, 3, 4)) || dice.containsAll(List.of(2, 3, 4, 5)) ||
                dice.containsAll(List.of(3, 4, 5, 6));
    }

    @Override
    public boolean isPotential(List<Integer> dice) {
        int repeatCount = Category.repeatedCount(dice);
        int diceLeft = 5 - dice.size();
        return repeatCount < 2 && diceLeft > 0;

    }
}

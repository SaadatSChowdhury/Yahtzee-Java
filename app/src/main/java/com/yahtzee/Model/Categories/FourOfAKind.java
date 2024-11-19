package com.yahtzee.Model.Categories;

import androidx.annotation.NonNull;

import com.yahtzee.Model.Category;

import java.util.List;
import java.util.Objects;

public class FourOfAKind implements Category {
    private static final FourOfAKind INSTANCE = new FourOfAKind();

    private FourOfAKind() {
    }

    public static FourOfAKind getInstance() {
        return INSTANCE;
    }

    @NonNull
    @Override
    public String toString() {
        return "Four of a Kind";
    }

    @Override
    public int calculateScore(List<Integer> dice) {
        if (!isValid(dice)) {
            return 0;
        }
        return dice.stream().reduce(0, Integer::sum);
    }

    @Override
    public boolean isValid(List<Integer> dice) {
        return dice.stream().anyMatch(d -> dice.stream().filter(d2 -> Objects.equals(d2, d)).count() >= 4);
    }

    @Override
    public boolean isPotential(List<Integer> dice) {
        int diceLeft = 5 - dice.size();
        int maxCount = Category.maxCount(dice);

        return diceLeft + maxCount >= 4;
    }
}

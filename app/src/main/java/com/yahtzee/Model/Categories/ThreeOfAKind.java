package com.yahtzee.Model.Categories;

import androidx.annotation.NonNull;

import com.yahtzee.Model.Category;

import java.util.List;
import java.util.Objects;

public class ThreeOfAKind implements Category {
    private static final ThreeOfAKind INSTANCE = new ThreeOfAKind();

    private ThreeOfAKind() {
    }

    public static ThreeOfAKind getInstance() {
        return INSTANCE;
    }

    @NonNull
    @Override
    public String toString() {
        return "Three of a Kind";
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
        return dice.stream().anyMatch(d -> dice.stream().filter(d2 -> Objects.equals(d2, d)).count() >= 3);
    }

    @Override
    public boolean isPotential(List<Integer> dice) {
        int maxCount = Category.maxCount(dice);
        int diceLeft = 5 - dice.size();

        return diceLeft + maxCount >= 3;
    }
}

package com.yahtzee.Model.Categories;

import androidx.annotation.NonNull;

import com.yahtzee.Model.Category;

import java.util.List;

public class Yahtzee implements Category {
    private static final Yahtzee INSTANCE = new Yahtzee();

    private Yahtzee() {}

    public static Yahtzee getInstance() {
        return INSTANCE;
    }

    @NonNull
    @Override
    public String toString() {
        return "Yahtzee";
    }

    @Override
    public int calculateScore(List<Integer> dice) {
        return isValid(dice) ? 50 : 0;
    }

    @Override
    public boolean isValid(List<Integer> dice) {
        return dice.stream().distinct().count() == 1 && dice.size() == 5;
    }

    @Override
    public boolean isPotential(List<Integer> dice) {
        return dice.stream().distinct().count() <= 1;
    }
}

package com.yahtzee.Model.Categories;

import androidx.annotation.NonNull;

import com.yahtzee.Model.Category;

import java.util.List;

public class Fours implements Category {
    private static final Fours INSTANCE = new Fours();

    private Fours() {}

    public static Fours getInstance() {
        return INSTANCE;
    }

    @NonNull
    @Override
    public String toString() {
        return "Fours";
    }

    @Override
    public int calculateScore(List<Integer> dice) {
        return (int) dice.stream().filter(d -> d == 4).count() * 4;
    }

    @Override
    public boolean isValid(List<Integer> dice) {
        return dice.contains(4);
    }

    @Override
    public boolean isPotential(List<Integer> dice) {
        return dice.size() < 5 || dice.contains(4);
    }
}

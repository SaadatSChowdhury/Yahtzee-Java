package com.yahtzee.Model.Categories;

import androidx.annotation.NonNull;

import com.yahtzee.Model.Category;

import java.util.List;

public class Sixes implements Category {
    private static final Sixes INSTANCE = new Sixes();

    private Sixes() {}

    public static Sixes getInstance() {
        return INSTANCE;
    }

    @NonNull
    @Override
    public String toString() {
        return "Sixes";
    }

    @Override
    public int calculateScore(List<Integer> dice) {
        return (int) dice.stream().filter(d -> d == 6).count() * 6;
    }

    @Override
    public boolean isValid(List<Integer> dice) {
        return dice.contains(6);
    }

    @Override
    public boolean isPotential(List<Integer> dice) {
        return dice.size() < 5 || dice.contains(6);
    }
}

package com.yahtzee.Model.Categories;

import androidx.annotation.NonNull;

import com.yahtzee.Model.Category;

import java.util.List;

public class Threes implements Category {
    private static final Threes INSTANCE = new Threes();

    private Threes() {}

    public static Threes getInstance() {
        return INSTANCE;
    }

    @NonNull
    @Override
    public String toString() {
        return "Threes";
    }

    @Override
    public int calculateScore(List<Integer> dice) {
        return (int) dice.stream().filter(d -> d == 3).count() * 3;
    }

    @Override
    public boolean isValid(List<Integer> dice) {
        return dice.contains(3);
    }

    @Override
    public boolean isPotential(List<Integer> dice) {
        return dice.size() < 5 || dice.contains(3);
    }
}

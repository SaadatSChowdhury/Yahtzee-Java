package com.yahtzee.Model.Categories;

import androidx.annotation.NonNull;

import com.yahtzee.Model.Category;

import java.util.List;

public class Fives implements Category {
    private static final Fives INSTANCE = new Fives();

    private Fives() {}

    public static Fives getInstance() {
        return INSTANCE;
    }

    @NonNull
    @Override
    public String toString() {
        return "Fives";
    }

    @Override
    public int calculateScore(List<Integer> dice) {
        return (int) dice.stream().filter(d -> d == 5).count() * 5;
    }

    @Override
    public boolean isValid(List<Integer> dice) {
        return dice.contains(5);
    }

    @Override
    public boolean isPotential(List<Integer> dice) {
        return dice.size() < 5 || dice.contains(5);
    }
}

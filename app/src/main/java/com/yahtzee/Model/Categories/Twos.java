package com.yahtzee.Model.Categories;

import androidx.annotation.NonNull;

import com.yahtzee.Model.Category;

import java.util.List;

public class Twos implements Category {
    private static final Twos INSTANCE = new Twos();

    private Twos() {}

    public static Twos getInstance() {
        return INSTANCE;
    }

    @NonNull
    @Override
    public String toString() {
        return "Twos";
    }

    @Override
    public int calculateScore(List<Integer> dice) {
        return (int) dice.stream().filter(d -> d == 2).count() * 2;
    }

    @Override
    public boolean isValid(List<Integer> dice) {
        return dice.contains(2);
    }

    @Override
    public boolean isPotential(List<Integer> dice) {
        return dice.size() < 5 || dice.contains(2);
    }
}

package com.yahtzee.Model.Categories;

import androidx.annotation.NonNull;

import com.yahtzee.Model.Category;

import java.util.List;

public class Aces implements Category {
    private static final Aces INSTANCE = new Aces();

    private Aces() {
    }

    public static Aces getInstance() {
        return INSTANCE;
    }

    @NonNull
    @Override
    public String toString() {
        return "Ones";
    }

    @Override
    public int calculateScore(List<Integer> dice) {
        return (int) dice.stream().filter(d -> d == 1).count();
    }

    @Override
    public boolean isValid(List<Integer> dice) {
        return dice.contains(1);
    }

    @Override
    public boolean isPotential(List<Integer> dice) {
        return dice.size() < 5 || dice.contains(1);
    }
}

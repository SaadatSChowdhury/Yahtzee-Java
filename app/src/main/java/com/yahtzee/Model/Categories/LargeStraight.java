package com.yahtzee.Model.Categories;

import androidx.annotation.NonNull;

import com.yahtzee.Model.Category;

import java.util.List;
import java.util.Objects;

public class LargeStraight implements Category {
    private static final LargeStraight INSTANCE = new LargeStraight();

    private LargeStraight() {
    }

    public static LargeStraight getInstance() {
        return INSTANCE;
    }

    @NonNull
    @Override
    public String toString() {
        return "Large Straight";
    }

    @Override
    public int calculateScore(List<Integer> dice) {
        if (isValid(dice)) {
            return 40;
        }
        return 0;
    }

    @Override
    public boolean isValid(List<Integer> dice) {
        return dice.containsAll(List.of(1, 2, 3, 4, 5)) || dice.containsAll(List.of(2, 3, 4, 5, 6));
    }

    @Override
    public boolean isPotential(List<Integer> dice) {
        if (dice.contains(1) && dice.contains(6)) {
            return false;
        }

        for (int i = 0; i < dice.size(); i++) {
            for (int j = i + 1; j < dice.size(); j++) {
                if (Objects.equals(dice.get(i), dice.get(j))) {
                    return false;
                }
            }
        }

        return true;
    }

}

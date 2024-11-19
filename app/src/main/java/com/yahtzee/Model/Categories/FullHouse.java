package com.yahtzee.Model.Categories;

import androidx.annotation.NonNull;

import com.yahtzee.Model.Category;

import java.util.List;
import java.util.Objects;

public class FullHouse implements Category {
    private static final FullHouse INSTANCE = new FullHouse();

    private FullHouse() {
    }

    public static FullHouse getInstance() {
        return INSTANCE;
    }

    @NonNull
    @Override
    public String toString() {
        return "Full House";
    }

    @Override
    public int calculateScore(List<Integer> dice) {
        if (isValid(dice)) {
            return 25;
        }
        return 0;
    }

    @Override
    public boolean isValid(List<Integer> dice) {
        return dice.stream().anyMatch(d -> dice.stream().filter(d2 -> Objects.equals(d2, d)).count() == 3) && dice.stream().anyMatch(d -> dice.stream().filter(d2 -> Objects.equals(d2, d)).count() == 2);
    }

    @Override
    public boolean isPotential(List<Integer> dice) {
        //  return count_unique(dice) <= 2 && max_count(dice) <= 3;

        long distinctCount = dice.stream().distinct().count();
        int maxCount = dice.stream().mapToInt(d -> (int) dice.stream().filter(d2 -> Objects.equals(d2, d)).count()).max().orElse(0);

        return distinctCount <= 2 && maxCount <= 3;
    }
}

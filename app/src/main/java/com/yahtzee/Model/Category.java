package com.yahtzee.Model;

import static java.lang.Math.max;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

public interface Category {

    @NonNull
    String toString();

    int calculateScore(List<Integer> dice);

    boolean isValid(List<Integer> dice);

    boolean isPotential(List<Integer> dice);

    static int uniqueCount(List<Integer> dice) {
        return (int) dice.stream().distinct().count();
    }

    static int maxCount(List<Integer> dice) {
        return dice.stream().mapToInt(d -> (int) dice.stream().filter(d2 -> Objects.equals(d2, d)).count()).max().orElse(0);
    }

    static int repeatedCount(List<Integer> dice) {
        int[] counts = new int[6];
        for (int d : dice) {
            counts[d - 1]++;
        }

        for (int i = 0; i < counts.length; i++) {
            int count = counts[i];
            counts[i] = max(0, count - 1);
        }

        int sum = 0;
        for (int count : counts) {
            sum += count;
        }
        return sum;
    }
}

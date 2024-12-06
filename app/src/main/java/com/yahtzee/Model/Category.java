package com.yahtzee.Model;

import static java.lang.Math.max;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * This interface represents a category in the game of Yahtzee.
 * It defines the methods that any category (e.g., Full House, Three of a Kind) should implement.
 */
public interface Category {

    /**
     * Returns a string representation of the category, typically its name.
     *
     * @return A string representing the category.
     */
    @NonNull
    String toString();

    /**
     * Calculates the score of the given dice for this category.
     *
     * @param dice The dice to calculate the score for.
     * @return The score of the dice for this category.
     */
    int calculateScore(List<Integer> dice);

    /**
     * Returns whether the given dice are valid for this category.
     *
     * @param dice The dice to check.
     * @return True if the dice are valid for this category, false otherwise.
     */
    boolean isValid(List<Integer> dice);

    /**
     * Returns whether the given dice are a potential match for this category.
     *
     * @param dice The dice to check.
     * @return True if the dice are a potential match for this category, false otherwise.
     */
    boolean isPotential(List<Integer> dice);

    /**
     * Returns the number of unique values in the given dice.
     *
     * @param dice The dice to check.
     * @return The number of unique values in the dice.
     */
    static int uniqueCount(List<Integer> dice) {
        return (int) dice.stream().distinct().count();
    }

    /**
     * Returns the maximum number of times a value is repeated in the given dice.
     *
     * @param dice The dice to check.
     * @return The maximum number of times a value is repeated in the dice.
     */
    static int maxCount(List<Integer> dice) {
        return dice.stream().mapToInt(d -> (int) dice.stream().filter(d2 -> Objects.equals(d2, d)).count()).max().orElse(0);
    }

    /**
     * Returns the number of times a value is repeated in the given dice.
     *
     * @param dice The dice to check.
     * @return The number of times a value is repeated in the dice.
     */
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

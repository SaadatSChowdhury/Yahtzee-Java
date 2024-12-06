package com.yahtzee.Model.Categories;

import com.yahtzee.Model.Category;

import java.util.List;

/**
 * The Util class provides utility methods and constants related to the Yahtzee game categories.
 * This class includes predefined constants for each of the available categories and methods
 * to determine valid and potential categories based on a list of dice values.
 */
public class Util {

    // Predefined constants for each category in the Yahtzee game
    public static final Category ACES = Aces.getInstance();
    public static final Category TWOS = Twos.getInstance();
    public static final Category THREES = Threes.getInstance();
    public static final Category FOURS = Fours.getInstance();
    public static final Category FIVES = Fives.getInstance();
    public static final Category SIXES = Sixes.getInstance();

    public static final Category THREE_OF_A_KIND = ThreeOfAKind.getInstance();
    public static final Category FOUR_OF_A_KIND = FourOfAKind.getInstance();
    public static final Category FULL_HOUSE = FullHouse.getInstance();
    public static final Category SMALL_STRAIGHT = SmallStraight.getInstance();
    public static final Category LARGE_STRAIGHT = LargeStraight.getInstance();
    public static final Category YAHTZEE = Yahtzee.getInstance();

    /**
     * A list containing all the available categories in the Yahtzee game.
     * This list is used for easy reference when determining valid and potential categories.
     */
    public static final List<Category> ALL_CATEGORIES = List.of(
            ACES, TWOS, THREES, FOURS, FIVES, SIXES,
            THREE_OF_A_KIND, FOUR_OF_A_KIND, FULL_HOUSE, SMALL_STRAIGHT, LARGE_STRAIGHT, YAHTZEE
    );

    /**
     * Returns a list of valid categories based on the given list of dice values.
     * A valid category is one that can be scored based on the given dice values.
     *
     * @param dice a list of dice values
     * @return a list of valid categories
     */
    public static List<Category> getValidCategories(List<Integer> dice) {
        return ALL_CATEGORIES.stream().filter(c -> c.isValid(dice)).toList();
    }

    /**
     * Returns a list of potential categories based on the given list of dice values.
     * A potential category is one that could be scored based on the given dice values,
     * but may require additional dice rolls to complete.
     *
     * @param dice a list of dice values
     * @return a list of potential categories
     */
    public static List<Category> getPotentialCategories(List<Integer> dice) {
        return ALL_CATEGORIES.stream().filter(c -> c.isPotential(dice)).toList();
    }

}

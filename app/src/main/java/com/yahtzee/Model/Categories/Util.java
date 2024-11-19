package com.yahtzee.Model.Categories;

import com.yahtzee.Model.Category;

import java.util.List;


public class Util {

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

    public static final List<Category> ALL_CATEGORIES = List.of(
            ACES, TWOS, THREES, FOURS, FIVES, SIXES,
            THREE_OF_A_KIND, FOUR_OF_A_KIND, FULL_HOUSE, SMALL_STRAIGHT, LARGE_STRAIGHT, YAHTZEE
    );

    public static List<Category> getValidCategories(List<Integer> dice) {
        return ALL_CATEGORIES.stream().filter(c -> c.isValid(dice)).toList();
    }

    public static List<Category> getPotentialCategories(List<Integer> dice) {
        return ALL_CATEGORIES.stream().filter(c -> c.isPotential(dice)).toList();
    }


}

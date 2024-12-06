package com.yahtzee.Model;

import static com.yahtzee.Model.Categories.Util.ACES;
import static com.yahtzee.Model.Categories.Util.FIVES;
import static com.yahtzee.Model.Categories.Util.FOURS;
import static com.yahtzee.Model.Categories.Util.FOUR_OF_A_KIND;
import static com.yahtzee.Model.Categories.Util.FULL_HOUSE;
import static com.yahtzee.Model.Categories.Util.LARGE_STRAIGHT;
import static com.yahtzee.Model.Categories.Util.SIXES;
import static com.yahtzee.Model.Categories.Util.SMALL_STRAIGHT;
import static com.yahtzee.Model.Categories.Util.THREES;
import static com.yahtzee.Model.Categories.Util.THREE_OF_A_KIND;
import static com.yahtzee.Model.Categories.Util.TWOS;
import static com.yahtzee.Model.Categories.Util.YAHTZEE;

import androidx.annotation.Nullable;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The AI class contains the logic for determining the best strategy and providing help
 * for the AI player in the Yahtzee game.
 */
public class AI {

    // Priority categories for the AI to pursue
    static final List<Category> priorityCategories = List.of(YAHTZEE, LARGE_STRAIGHT, SMALL_STRAIGHT, FULL_HOUSE);

    // Categories that require the same kind of dice
    static final List<Category> straightCategories = List.of(LARGE_STRAIGHT, SMALL_STRAIGHT);

    // Categories that require the same kind of dice
    static final List<Category> sameKindCategories = List.of(YAHTZEE, FOUR_OF_A_KIND, THREE_OF_A_KIND);

    // Categories that require any combination of dice
    List<Category> anyCombinationCategories = List.of(SIXES, FIVES, FOURS, THREES, TWOS, ACES);

    /**
     * Provides help to the AI based on the current game state.
     *
     * @param tournament The current tournament.
     * @return The help object containing advice for the AI.
     */
    public static Help getHelp(Tournament tournament) {
        ScoreCard scoreCard = tournament.getScoreCard();
        List<Integer> keptDice = tournament.getDiceRoll().getKeptDiceValues();
        List<Integer> rolledDice = tournament.getDiceRoll().getRolledDiceValues();

        return getHelp(scoreCard, keptDice, rolledDice);
    }

    /**
     * Provides help to the AI based on the current game state.
     *
     * @param scoreCard  The current score card.
     * @param keptDice   The dice that the AI has chosen to keep.
     * @param rolledDice The dice that the AI has rolled.
     * @return The help object containing advice for the AI.
     */
    public static Help getHelp(ScoreCard scoreCard, List<Integer> keptDice, List<Integer> rolledDice) {
        List<Integer> diceToKeep = getDiceToKeep(scoreCard, keptDice, rolledDice);
        boolean stand = getStandStatus(scoreCard, keptDice, rolledDice);
        Category targetCategory = getTargetCategory(scoreCard, keptDice, diceToKeep);
        return new Help(targetCategory, diceToKeep, keptDice, rolledDice, stand);
    }

    /**
     * Determines the best category for the AI to target based on the current game state.
     *
     * @param scoreCard  The current score card.
     * @param keptDice   The dice that the AI has chosen to keep.
     * @param diceToKeep The dice that the AI has chosen to keep.
     * @return The best category for the AI to target.
     */
    @Nullable
    private static Category getTargetCategory(ScoreCard scoreCard, List<Integer> keptDice, List<Integer> diceToKeep) {
        List<Integer> resultingDice = combineRolls(keptDice, diceToKeep);
        StrategyNode resultNode = StrategyTree.getInstance().getRoot().getChild(resultingDice).get();
        List<StrategyNode> leafNodes = resultNode.getLeafNodes();

        int maxScore = 0;
        List<Category> targetCategories = new ArrayList<>();
        for (StrategyNode leaf : leafNodes) {
            List<Category> availableCategories = scoreCard.getAvailableCategories();
            for (Category category : availableCategories) {
                if (!category.isValid(leaf.getPath())) {
                    continue;
                }
                int score = category.calculateScore(leaf.getPath());
                if (score > maxScore) {
                    maxScore = score;
                    targetCategories = new ArrayList<>();
                    targetCategories.add(category);
                } else if (score == maxScore) {
                    targetCategories.add(category);
                }
            }
        }

        if (targetCategories.isEmpty()) {
            return null;
        }

        if (targetCategories.contains(FOUR_OF_A_KIND) && targetCategories.contains(THREE_OF_A_KIND)) {
            return FOUR_OF_A_KIND;
        }

        return targetCategories.get(0);
    }

    /**
     * Determines the dice that the AI should keep based on the current game state.
     *
     * @param scoreCard  The current score card.
     * @param keptDice   The dice that the AI has chosen to keep.
     * @param diceRolls  The dice that the AI has rolled.
     * @return The dice that the AI should keep.
     */
    public static List<Integer> getDiceToKeep(ScoreCard scoreCard, List<Integer> keptDice, List<Integer> diceRolls) {
        if (keptDice.size() == 5) {
            return List.of();
        }

        CategoryKeepDice categoryKeepDice = getCategoryKeepDice(scoreCard, keptDice, diceRolls);
        return categoryKeepDice.getDiceToKeep();
    }

    /**
     * Determines which dice to keep for a given category, considering the current scorecard and dice rolls.
     *
     * @param scoreCard The current scorecard.
     * @param keptDice  The dice values the AI has kept.
     * @param diceRolls The dice values rolled in the current turn.
     * @return A CategoryKeepDice object containing the category and the dice to keep.
     */
    private static CategoryKeepDice getCategoryKeepDice(ScoreCard scoreCard, List<Integer> keptDice, List<Integer> diceRolls) {
        List<Category> targetCategories = scoreCard.getAvailableCategories();
        List<Integer> potentialDice = new ArrayList<>(keptDice);
        potentialDice.addAll(diceRolls);

        for (Category category : priorityCategories) {
            if (!targetCategories.contains(category)) {
                continue;
            }

            if (category.isValid(potentialDice)) {
                List<Integer> diceToKeep = diceRolls.stream().sorted().collect(Collectors.toList());

                if (straightCategories.contains(category)) {
                    diceToKeep = diceToKeep.stream().distinct().collect(Collectors.toList());

                    for (int i = 0; i < keptDice.size(); i++) {
                        int dice = keptDice.get(i);
                        if (diceToKeep.contains(dice)) {
                            diceToKeep.remove(Integer.valueOf(dice));
                        }
                    }

                    if (keptDice.contains(1) && diceRolls.contains(6)) {
                        diceToKeep = diceRolls.stream().filter(dice -> dice != 6).collect(Collectors.toList());
                    }

                    if (keptDice.contains(6) && diceRolls.contains(1)) {
                        diceToKeep = diceRolls.stream().filter(dice -> dice != 1).collect(Collectors.toList());
                    }

                }
                return new CategoryKeepDice(category, diceToKeep);
            }
        }

        StrategyNode root = StrategyTree.getInstance().getRoot();

        List<List<Integer>> possibleCombinationsToKeep = getCombinations(diceRolls);

        StrategyNode keptNode = root.getChild(keptDice).get();
        List<StrategyNode> leaves = keptNode.getLeafNodes();

        List<StrategyNode> maxNodes = new ArrayList<>();
        List<Category> maxCategories = new ArrayList<>();
        int maxScore = 0;
        for (StrategyNode leaf : leaves) {
            for (Category category : targetCategories) {
                int score = category.calculateScore(leaf.getPath());
                if (score > maxScore) {
                    maxScore = score;
                    maxCategories = new ArrayList<>();
                    maxCategories.add(category);
                    maxNodes = new ArrayList<>();
                    maxNodes.add(leaf);
                } else if (score == maxScore) {
                    maxNodes.add(leaf);
                    maxCategories.add(category);
                }
            }
        }

        List<List<Integer>> maxRolls = maxNodes.stream().map(StrategyNode::getPath).collect(Collectors.toList());
        List<List<Integer>> maxRollsWithoutKept = maxRolls.stream().map(roll -> roll.subList(keptDice.size(), roll.size())).collect(Collectors.toList());

        List<Integer> largestIntersection = new ArrayList<>();
        Category targetCategory = null;
        for (int i = 0; i < maxRollsWithoutKept.size(); i++) {
            List<Integer> roll = maxRollsWithoutKept.get(i);
            Category category = maxCategories.get(i);
            for (List<Integer> combination : possibleCombinationsToKeep) {
                List<Integer> intersection = getIntersection(roll, combination);
                if (intersection.size() > largestIntersection.size()) {
                    largestIntersection = intersection;
                    targetCategory = category;
                }
            }
        }

        if (targetCategory == null) {
            return new CategoryKeepDice(targetCategory, largestIntersection);
        }

        if (straightCategories.contains(targetCategory)) {
            largestIntersection = largestIntersection.stream().distinct().collect(Collectors.toList());

            for (int i = 0; i < keptDice.size(); i++) {
                int dice = keptDice.get(i);
                if (largestIntersection.contains(dice)) {
                    largestIntersection.remove(Integer.valueOf(dice));
                }
            }

            if (keptDice.contains(1) && diceRolls.contains(6)) {
                largestIntersection = diceRolls.stream().filter(dice -> dice != 6).collect(Collectors.toList());
            }

            if (keptDice.contains(6) && diceRolls.contains(1)) {
                largestIntersection = diceRolls.stream().filter(dice -> dice != 1).collect(Collectors.toList());
            }
        }

        if (sameKindCategories.contains(targetCategory)) {
            largestIntersection = getOnlyMaximalKind(largestIntersection);
        }

        List<Integer> finalRoll = combineRolls(keptDice, largestIntersection);
        if (FULL_HOUSE.equals(targetCategory)) {
            for (int diceValue = 1; diceValue <= 6; diceValue++) {
                int count = 0;

                for (int dice : finalRoll) {
                    if (dice == diceValue) {
                        count++;
                    }
                }

                // if count is 1 and dice in intersection, remove it
                if (count == 1 && largestIntersection.contains(diceValue)) {
                    largestIntersection.remove(Integer.valueOf(diceValue));
                }
            }
        }


        return new CategoryKeepDice(targetCategory, largestIntersection);
    }

    /**
     * Determines whether the AI should stand based on the current game state.
     *
     * @param scoreCard  The current score card.
     * @param keptDice   The dice that the AI has chosen to keep.
     * @param diceRolls  The dice that the AI has rolled.
     * @return True if the AI should stand, false otherwise.
     */
    public static boolean getStandStatus(ScoreCard scoreCard, List<Integer> keptDice, List<Integer> diceRolls) {
        List<Integer> diceToKeep = getDiceToKeep(scoreCard, keptDice, diceRolls).stream().sorted().collect(Collectors.toList());
        List<Integer> rolledDice = diceRolls.stream().sorted().collect(Collectors.toList());
        return diceToKeep.equals(rolledDice);

    }

    /**
     * Generates all possible combinations of elements.
     *
     * @param elements The elements to generate combinations from.
     * @return A list of all possible combinations.
     */
    public static List<List<Integer>> getCombinations(List<Integer> elements) {
        List<List<Integer>> result = new ArrayList<>();
        int n = elements.size();
        for (int i = 0; i < (1 << n); i++) { // Iterate through all possible subsets
            List<Integer> combination = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                if (((i >> j) & 1) == 1) { // Check if j-th bit is set
                    combination.add(elements.get(j));
                }
            }
            result.add(combination);
        }
        return result;
    }

    /**
     * Returns the intersection of two lists.
     *
     * @param list1 The first list.
     * @param list2 The second list.
     * @return The intersection of the two lists.
     */
    private static List<Integer> getIntersection(List<Integer> list1, List<Integer> list2) {
        int[] count1 = new int[6];
        int[] count2 = new int[6];

        for (int i : list1) {
            count1[i - 1]++;
        }

        for (int i : list2) {
            count2[i - 1]++;
        }

        List<Integer> intersection = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            int minCount = Math.min(count1[i], count2[i]);
            for (int j = 0; j < minCount; j++) {
                intersection.add(i + 1);
            }
        }

        return intersection;
    }

    /**
     * Combines two lists of dice rolls.
     *
     * @param keptDice  The dice that the AI has chosen to keep.
     * @param diceRolls The dice that the AI has rolled.
     * @return The combined list of dice rolls.
     */
    private static List<Integer> combineRolls(List<Integer> keptDice, List<Integer> diceRolls) {
        List<Integer> combinedRolls = new ArrayList<>(keptDice);
        combinedRolls.addAll(diceRolls);
        return combinedRolls;
    }

    /**
     * Returns the list of dice that are of the maximal kind.
     *
     * @param dice The list of dice.
     * @return The list of dice that are of the maximal kind.
     */
    private static List<Integer> getOnlyMaximalKind(List<Integer> dice) {
        int[] count = new int[6];
        for (int i : dice) {
            count[i - 1]++;
        }

        int maxCount = 0;
        for (int i = 0; i < 6; i++) {
            maxCount = Math.max(maxCount, count[i]);
        }

        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            if (count[i] == maxCount) {
                for (int j = 0; j < maxCount; j++) {
                    result.add(i + 1);
                }
            }
        }

        return result;
    }

}

/**
 * The CategoryKeepDice class is used to represent a category and the list of dice
 * that should be kept for that category in the Yahtzee game.
 */
class CategoryKeepDice {

    // The category that the dice are being kept for
    private final Category category;

    // The list of dice that should be kept
    private final List<Integer> diceToKeep;

    /**
     * Creates a new CategoryKeepDice object.
     *
     * @param category   The category that the dice are being kept for.
     * @param diceToKeep The list of dice that should be kept.
     */
    public CategoryKeepDice(Category category, List<Integer> diceToKeep) {
        this.category = category;
        this.diceToKeep = diceToKeep;
    }

    /**
     * Retrieves the category associated with the dice to keep.
     *
     * @return The category for which the dice are being kept.
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Retrieves the list of dice values that should be kept.
     *
     * @return The list of dice values to keep.
     */
    public List<Integer> getDiceToKeep() {
        return diceToKeep;
    }

}



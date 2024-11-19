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

public class AI {

    static final List<Category> priorityCategories = List.of(YAHTZEE, LARGE_STRAIGHT, SMALL_STRAIGHT, FULL_HOUSE);
    static final List<Category> straightCategories = List.of(LARGE_STRAIGHT, SMALL_STRAIGHT);
    static final List<Category> sameKindCategories = List.of(YAHTZEE, FOUR_OF_A_KIND, THREE_OF_A_KIND);
    List<Category> anyCombinationCategories = List.of(SIXES, FIVES, FOURS, THREES, TWOS, ACES);

    public static Help getHelp(Game game) {
        ScoreCard scoreCard = game.getScoreCard();
        List<Integer> keptDice = game.getDiceRoll().getKeptDiceValues();
        List<Integer> rolledDice = game.getDiceRoll().getRolledDiceValues();

        return getHelp(scoreCard, keptDice, rolledDice);
    }

    public static Help getHelp(ScoreCard scoreCard, List<Integer> keptDice, List<Integer> rolledDice) {
        List<Integer> diceToKeep = getDiceToKeep(scoreCard, keptDice, rolledDice);
        boolean stand = getStandStatus(scoreCard, keptDice, rolledDice);
        Category targetCategory = getTargetCategory(scoreCard, keptDice, diceToKeep);
        return new Help(targetCategory, diceToKeep, keptDice, rolledDice, stand);
    }

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


    public static List<Integer> getDiceToKeep(ScoreCard scoreCard, List<Integer> keptDice, List<Integer> diceRolls) {
        if (keptDice.size() == 5) {
            return List.of();
        }

        CategoryKeepDice categoryKeepDice = getCategoryKeepDice(scoreCard, keptDice, diceRolls);
        return categoryKeepDice.getDiceToKeep();
    }

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

        return new CategoryKeepDice(targetCategory, largestIntersection);
    }


    public static boolean getStandStatus(ScoreCard scoreCard, List<Integer> keptDice, List<Integer> diceRolls) {
        List<Integer> diceToKeep = getDiceToKeep(scoreCard, keptDice, diceRolls).stream().sorted().collect(Collectors.toList());
        List<Integer> rolledDice = diceRolls.stream().sorted().collect(Collectors.toList());
        return diceToKeep.equals(rolledDice);

    }


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

    private static List<Integer> combineRolls(List<Integer> keptDice, List<Integer> diceRolls) {
        List<Integer> combinedRolls = new ArrayList<>(keptDice);
        combinedRolls.addAll(diceRolls);
        return combinedRolls;
    }

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

class CategoryKeepDice {
    private final Category category;
    private final List<Integer> diceToKeep;

    public CategoryKeepDice(Category category, List<Integer> diceToKeep) {
        this.category = category;
        this.diceToKeep = diceToKeep;
    }

    public Category getCategory() {
        return category;
    }

    public List<Integer> getDiceToKeep() {
        return diceToKeep;
    }

}



package com.yahtzee.Model;

import static com.yahtzee.Model.Categories.Util.ALL_CATEGORIES;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents a player's scorecard in a game of Yahtzee.
 * A ScoreCard tracks the scores for each category and the winner of each round.
 */
public class ScoreCard {
    private final List<CardEntry> entries = ALL_CATEGORIES.stream().map(CardEntry::new).toList();

    /**
     * Default constructor for ScoreCard.
     */
    public ScoreCard() {
    }


    /**
     * Constructs a ScoreCard from a serialized string.
     *
     * @param serial   the serialized string
     * @param human    the human player
     * @param computer the computer player
     * @return the ScoreCard
     */
    public static ScoreCard fromString(String serial, Player human, Player computer) {
        List<String> lines = Arrays.stream(serial.split("\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());

        int categoryIndex = 0;
        ScoreCard scoreCard = new ScoreCard();

        for (String line : lines) {
            if (line.equals("0")) {
                categoryIndex++;
                continue;
            }

            String[] parts = line.split(" ");
            if (parts.length != 3) {
                continue;
            }

            Category category = ALL_CATEGORIES.get(categoryIndex);
            int points = Integer.parseInt(parts[0]);
            Player winner = parts[1].equals("Human") ? human : computer;
            int round = Integer.parseInt(parts[2]);

            scoreCard.setScore(category, points, round, winner);
            categoryIndex++;
        }

        return scoreCard;
    }

    /**
     * Returns a string representation of the ScoreCard.
     *
     * @return A string that represents the ScoreCard.
     */
    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Scorecard:\n");

        for (CardEntry entry : entries) {
            if (entry.score.isEmpty()) {
                sb.append("0\n");
            } else {
                sb.append(entry.score.get())
                        .append(" ")
                        .append(entry.winner.get().getName())
                        .append(" ")
                        .append(entry.round.get())
                        .append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * Checks equality of two ScoreCard objects.
     *
     * @param obj The object to compare.
     * @return True if the two ScoreCards are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ScoreCard)) {
            return false;
        }
        ScoreCard other = (ScoreCard) obj;
        return entries.equals(other.entries);
    }

    /**
     * Returns a list of available categories that are not yet filled in the scorecard.
     *
     * @return A list of available categories.
     */
    public List<Category> getAvailableCategories() {
        return entries.stream().filter(e -> e.score.isEmpty()).map(e -> e.category).toList();
    }

    /**
     * Sets the score for a category in a round.
     *
     * @param category The category to set the score for.
     * @param score    The score to set.
     * @param round    The round to set the score for.
     * @param winner   The winner of the round.
     */
    public void setScore(Category category, int score, int round, Player winner) {
        entries.stream()
                .filter(e -> e.category == category)
                .findFirst()
                .ifPresent(e -> e.set(score, round, winner));
    }

    /**
     * Gets the score for a given category.
     *
     * @param category The category to retrieve the score for.
     * @return An Optional containing the score if it exists, or empty if not.
     */
    public Optional<Integer> getScore(Category category) {
        return entries.stream()
                .filter(e -> e.category == category)
                .findFirst()
                .flatMap(e -> e.score);
    }

    /**
     * Gets the round for a given category.
     *
     * @param category The category to retrieve the round for.
     * @return An Optional containing the round if it exists, or empty if not.
     */
    public Optional<Integer> getRound(Category category) {
        return entries.stream()
                .filter(e -> e.category == category)
                .findFirst()
                .flatMap(e -> e.round);
    }

    /**
     * Gets the winner for a given category.
     *
     * @param category The category to retrieve the winner for.
     * @return An Optional containing the winner if it exists, or empty if not.
     */
    public Optional<Player> getWinner(Category category) {
        return entries.stream()
                .filter(e -> e.category == category)
                .findFirst().flatMap(e -> e.winner);
    }

    /**
     * Gets the winner of the game.
     *
     * @return An Optional containing the winner if the game is complete, or empty if not.
     */
    public Optional<Player> getWinner() {
        if (!isComplete()) return Optional.empty();
        return getPlayers().stream()
                .max((p1, p2) -> Integer.compare(getTotalScore(p1), getTotalScore(p2)));
    }

    /**
     * Gets the total score for a given player.
     *
     * @param player The player to retrieve the total score for.
     * @return The total score for the player.
     */
    public int getTotalScore(Player player) {
        return entries.stream()
                .filter(e -> e.winner.isPresent() && e.winner.get().equals(player))
                .mapToInt(e -> e.score.get())
                .sum();
    }

    /**
     * Checks if the scorecard is complete.
     *
     * @return True if the scorecard is complete, false otherwise.
     */
    public boolean isComplete() {
        return entries.stream()
                .allMatch(e -> e.score.isPresent());
    }

    /**
     * Returns all card entries in the scorecard.
     *
     * @return A list of all card entries.
     */
    public List<CardEntry> getEntries() {
        return entries;
    }

    /**
     * Returns the card entry for a given category.
     *
     * @param keptDice The category to retrieve the card entry for.
     * @return The card entry for the given category.
     */
    public List<Category> getPotentialCategories(List<Integer> keptDice) {
        if (keptDice.isEmpty()) {
            return getAvailableCategories();
        }
        return entries.stream()
                .filter(e -> e.score.isEmpty())
                .filter(e -> e.category.isPotential(keptDice))
                .map(e -> e.category)
                .toList();
    }

    /**
     * Returns the players in the game.
     *
     * @return A list of players in the game.
     */
    public List<Player> getPlayers() {
        return entries.stream()
                .filter(e -> e.winner.isPresent())
                .map(e -> e.winner.get())
                .distinct()
                .toList();
    }

    /**
     * Returns the valid categories for the current roll.
     *
     * @param keptDice The dice that are kept.
     * @return A list of valid categories.
     */
    public List<Category> getValidCategories(List<Integer> keptDice) {
        return entries.stream()
                .filter(e -> e.score.isEmpty())
                .filter(e -> e.category.isValid(keptDice))
                .map(e -> e.category)
                .toList();
    }
}

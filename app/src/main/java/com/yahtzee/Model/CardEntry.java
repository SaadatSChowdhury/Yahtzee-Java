package com.yahtzee.Model;

import java.util.Optional;

/**
 * Represents an entry on a player's scorecard for a specific category.
 * Each entry stores the score, the round it was recorded in, and the player who achieved the score.
 */
public class CardEntry {

    // The category this entry corresponds to.
    final Category category;

    // The score achieved in this category.
    Optional<Integer> score = Optional.empty();

    // The round in which this score was achieved.
    Optional<Integer> round = Optional.empty();

    // The player who achieved this score.
    Optional<Player> winner = Optional.empty();

    /**
     * Creates a new CardEntry for the specified category.
     *
     * @param category The category this entry belongs to.
     */
    public CardEntry(Category category) {
        this.category = category;
    }

    /**
     * Compares this CardEntry to another object for equality.
     * Two CardEntries are considered equal if they have the same category, score, round, and winner.
     *
     * @param obj The object to compare this CardEntry to.
     * @return True if the two CardEntries are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CardEntry)) {
            return false;
        }
        // All fields should be equal
        CardEntry cardEntry = (CardEntry) obj;
        return category.equals(cardEntry.category)
                && score.equals(cardEntry.score)
                && round.equals(cardEntry.round)
                && winner.equals(cardEntry.winner);
    }

    /**
     * Sets the score, round, and winner for this CardEntry.
     *
     * @param score  The score achieved in this category.
     * @param round  The round in which this score was achieved.
     * @param winner The player who achieved this score.
     */
    public void set(int score, int round, Player winner) {
        this.score = Optional.of(score);
        this.round = Optional.of(round);
        this.winner = Optional.of(winner);
    }

    /**
     * Gets the category this entry corresponds to.
     *
     * @return The category this entry belongs to.
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Gets the score achieved in this category.
     *
     * @return The score achieved in this category.
     */
    public Optional<Integer> getScore() {
        return score;
    }

    /**
     * Gets the round in which this score was achieved.
     *
     * @return The round in which this score was achieved.
     */
    public Optional<Integer> getRound() {
        return round;
    }

    /**
     * Gets the player who achieved this score.
     *
     * @return The player who achieved this score.
     */
    public Optional<Player> getWinner() {
        return winner;
    }

}



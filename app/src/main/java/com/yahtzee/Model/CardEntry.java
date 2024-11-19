package com.yahtzee.Model;

import java.util.Optional;

public class CardEntry {
    final Category category;
    Optional<Integer> score = Optional.empty();
    Optional<Integer> round = Optional.empty();
    Optional<Player> winner = Optional.empty();

    public CardEntry(Category category) {
        this.category = category;
    }

    //Implement equals and hashcode
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

    public void set(int score, int round, Player winner) {
        this.score = Optional.of(score);
        this.round = Optional.of(round);
        this.winner = Optional.of(winner);
    }

    public Category getCategory() {
        return category;
    }

    public Optional<Integer> getScore() {
        return score;
    }

    public Optional<Integer> getRound() {
        return round;
    }

    public Optional<Player> getWinner() {
        return winner;
    }

}



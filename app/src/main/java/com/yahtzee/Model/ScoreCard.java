package com.yahtzee.Model;

import static com.yahtzee.Model.Categories.Util.ALL_CATEGORIES;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ScoreCard {
    private final List<CardEntry> entries = ALL_CATEGORIES.stream().map(CardEntry::new).toList();

    public ScoreCard() {
    }


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

    // Implement equals and hashCode
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ScoreCard)) {
            return false;
        }
        ScoreCard other = (ScoreCard) obj;
        return entries.equals(other.entries);
    }


    public List<Category> getAvailableCategories() {
        return entries.stream().filter(e -> e.score.isEmpty()).map(e -> e.category).toList();
    }

    public void setScore(Category category, int score, int round, Player winner) {
        entries.stream()
                .filter(e -> e.category == category)
                .findFirst()
                .ifPresent(e -> e.set(score, round, winner));
    }

    public Optional<Integer> getScore(Category category) {
        return entries.stream()
                .filter(e -> e.category == category)
                .findFirst()
                .flatMap(e -> e.score);
    }

    public Optional<Integer> getRound(Category category) {
        return entries.stream()
                .filter(e -> e.category == category)
                .findFirst()
                .flatMap(e -> e.round);
    }

    public Optional<Player> getWinner(Category category) {
        return entries.stream()
                .filter(e -> e.category == category)
                .findFirst().flatMap(e -> e.winner);
    }

    public Optional<Player> getWinner() {
        if (!isComplete()) return Optional.empty();
        return getPlayers().stream()
                .max((p1, p2) -> Integer.compare(getTotalScore(p1), getTotalScore(p2)));
    }

    public int getTotalScore(Player player) {
        return entries.stream()
                .filter(e -> e.winner.isPresent() && e.winner.get().equals(player))
                .mapToInt(e -> e.score.get())
                .sum();
    }

    public boolean isComplete() {
        return entries.stream()
                .allMatch(e -> e.score.isPresent());
    }

    public List<CardEntry> getEntries() {
        return entries;
    }

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

    public List<Player> getPlayers() {
        return entries.stream()
                .filter(e -> e.winner.isPresent())
                .map(e -> e.winner.get())
                .distinct()
                .toList();
    }


    public List<Category> getValidCategories(List<Integer> keptDice) {
        return entries.stream()
                .filter(e -> e.score.isEmpty())
                .filter(e -> e.category.isValid(keptDice))
                .map(e -> e.category)
                .toList();
    }
}

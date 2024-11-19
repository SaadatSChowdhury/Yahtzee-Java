package com.yahtzee.Model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class Game {
    private static Game instance;

    private final List<Player> players = new ArrayList<>(List.of(
            new Player("Human", false),
            new Player("Computer", true)
    ));
    private ScoreCard scoreCard = new ScoreCard();
    private final DiceRoll diceRoll = new DiceRoll();

    private int currentRound;
    private int currentTurn;
    private Player currentPlayer;
    private Player nextPlayer;
    private Help currentHelp;

    private Queue<Player> turnOrder;

    private Game() {
        currentRound = 1;
        currentTurn = 1;
        ScoreCard scoreCard = new ScoreCard();
    }

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public static void startNewGame() {
        instance = new Game();
        Logger.log("New game started");
    }

    public static void setFromString(String gameString) {
        List<String> lines = gameString.lines().collect(Collectors.toList());

        int roundNumber = 1;
        for (String line : lines) {
            if (line.startsWith("Round: ")) {
                roundNumber = Integer.parseInt(line.substring(7));
                break;
            }
        }

        int scorecardStart = 0;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith("Scorecard:")) {
                scorecardStart = i + 1;
                break;
            }
        }

        List<String> scorecardLines = lines.subList(scorecardStart, lines.size());
        String scorecardSerial = String.join("\n", scorecardLines);
        Player human = new Player("Human", false);
        Player computer = new Player("Computer", true);
        ScoreCard scoreCard = ScoreCard.fromString(scorecardSerial, human, computer);

        instance = new Game();
        instance.setScoreCard(scoreCard);
        instance.setCurrentRound(roundNumber);

        Logger.log("Game loaded from serial");

        instance.determinePlayerOrder();
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder serial = new StringBuilder();
        serial.append("Round: ").append(currentRound).append("\n");
        serial.append(scoreCard.toString()).append("\n");
        return serial.toString();
    }


    public DiceRoll getDiceRoll() {
        return diceRoll;
    }

    public ScoreCard getScoreCard() {
        return scoreCard;
    }

    public void setScoreCard(ScoreCard scoreCard) {
        this.scoreCard = scoreCard;
    }

    public List<Player> getPlayers() {
        return players;
    }


    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public void setPlayerOrder(Player currentPlayer, Player nextPlayer) {
        this.currentPlayer = currentPlayer;
        this.nextPlayer = nextPlayer;
    }

    @Nullable
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getNextPlayer() {
        return nextPlayer;
    }

    public Help getCurrentHelp() {
        return currentHelp;
    }


    public void stand() {
        Logger.log(currentPlayer.getName() + " rolls " + diceRoll.getRolledDiceValues());
        Logger.log(currentPlayer.getName() + " keeps dice: " + diceRoll.getRolledDiceValues());

        keepAllDice();
        Logger.log(currentPlayer.getName() + "'s kept dice from all rolls: " + diceRoll.getKeptDiceValues());

        if (currentTurn < 3) {
            Logger.log(currentPlayer.getName() + " stands");
        }

        if (scoreCard.getValidCategories(diceRoll.getKeptDiceValues()).isEmpty()) {
            finishTurn();
        }

        Logger.log("");
    }

    public void reRoll() {
        if (!diceRoll.isAllDiceRolled()) {
            Logger.log("Not all dice are rolled. Not rerolling");
            return;
        }

        if (currentTurn >= 3) {
            Logger.log("Max rerolls reached. Not rerolling");
            return;
        }

        Logger.log(currentPlayer.getName() + " rolls " + diceRoll.getRolledDiceValues());
        Logger.log(currentPlayer.getName() + " keeps dice: " + diceRoll.getMarkedDiceValues());
        diceRoll.keepMarked();
        Logger.log(currentPlayer.getName() + "'s kept dice from all rolls: " + diceRoll.getKeptDiceValues());
        diceRoll.resetUnkept();
        currentTurn++;

        if (scoreCard.getPotentialCategories(diceRoll.getKeptDiceValues()).isEmpty()) {
            finishTurn();
        }

        Logger.log("");

    }

    private void keepAllDice() {
        diceRoll.keepAll();
    }

    public int getTurnNumber() {
        return currentTurn;
    }

    public void selectCategory(Category category) {
        if (category != null && scoreCard.getAvailableCategories().contains(category)) {
            List<Integer> diceValues = diceRoll.getKeptDiceValues();
            int score = category.calculateScore(diceValues);
            scoreCard.setScore(category, score, currentRound, currentPlayer);
            Logger.log(currentPlayer.getName() + " selected " + category + " for " + score + " points");
            Logger.log("");
        }

        finishTurn();
        checkOver();
    }

    private void finishTurn() {
        currentTurn = 1;
        diceRoll.reset();
        if (nextPlayer != null) {
            currentPlayer = nextPlayer;
            nextPlayer = null;
        } else {
            currentRound++;
            determinePlayerOrder();
        }
        resetHelp();
    }

    private void determinePlayerOrder() {
        // Get a sorted list of players by total score
        Player p1 = players.get(0);
        Player p2 = players.get(1);

        int p1Score = scoreCard.getTotalScore(p1);
        int p2Score = scoreCard.getTotalScore(p2);

        Logger.log(p1.getName() + " score: " + p1Score);
        Logger.log(p2.getName() + " score: " + p2Score);

        if (p1Score < p2Score) {
            currentPlayer = p1;
            nextPlayer = p2;
            Logger.log(p1.getName() + " goes first");
        } else if (p2Score < p1Score) {
            currentPlayer = p2;
            nextPlayer = p1;
            Logger.log(p2.getName() + " goes first");
        } else {
            currentPlayer = null;
            nextPlayer = null;
            Logger.log("Players are tied");
        }
        Logger.log("");
    }

    public boolean isOver() {
        return scoreCard.isComplete();
    }

    public String getResultText() {
        if (!isOver()) {
            return "Game is not over";
        }

        Player p1 = players.get(0);
        Player p2 = players.get(1);

        int p1Score = scoreCard.getTotalScore(p1);
        int p2Score = scoreCard.getTotalScore(p2);

        StringBuilder result = new StringBuilder();

        if (p1Score > p2Score) {
            result.append(p1.getName()).append(" wins!");
        } else if (p2Score > p1Score) {
            result.append(p2.getName()).append(" wins!");
        } else {
            result.append("It's a tie!");
        }

        result.append("\n\n");
        result.append("Final scores:\n");
        result.append(p1.getName()).append(": ").append(p1Score).append("\n");
        result.append(p2.getName()).append(": ").append(p2Score);

        return result.toString();


    }

    public void resetHelp() {
        currentHelp = null;

        for (Die die : diceRoll.getDice()) {
            die.setMarkedForHelpKeep(false);
        }
    }

    public void getHelp() {
        resetHelp();
        if (!diceRoll.isAllDiceRolled()) {
            return;
        }

        currentHelp = AI.getHelp(this);
        int[] helpDiceCounts = new int[7];
        for (int dieValue : currentHelp.getDiceToKeep()) {
            helpDiceCounts[dieValue]++;
        }

        for (Die die : diceRoll.getRolledDice()) {
            if (helpDiceCounts[die.getValue()] > 0) {
                die.setMarkedForHelpKeep(true);
                helpDiceCounts[die.getValue()]--;
            }
        }
    }

    public void confirmComputerRoll() {
        if (!currentPlayer.isComputer()) {
            return;
        }

        getHelp();
        Logger.log("Computer's target category: " + currentHelp.getTargetCategory());


        if (currentHelp.getStand() || currentTurn >= 3) {
            stand();
        } else {
            for (Die die : diceRoll.getRolledDice()) {
                if (die.isMarkedForHelpKeep()) {
                    die.setMarked(true);
                }
            }
            reRoll();
        }

        if (diceRoll.isAllDiceKept()) {
            getHelp();
            selectCategory(currentHelp.getTargetCategory());
        }
        Logger.log("");

    }

    public void checkOver() {
        if (isOver()) {
            Logger.log("Game over");
            Logger.log(getResultText());
            Logger.log("");
        }
    }
}

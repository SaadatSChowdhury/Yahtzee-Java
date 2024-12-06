package com.yahtzee.Model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * Represents the main tournament logic for a Yahtzee game.
 * Handles player turns, rounds, dice rolls, and scorecard updates.
 */
public class Tournament {
    private static Tournament instance;

    // List of players participating in the tournament
    private final List<Player> players = new ArrayList<>(List.of(
            new Player("Human", false),
            new Player("Computer", true)
    ));
    private ScoreCard scoreCard = new ScoreCard();
    private final DiceRoll diceRoll = new DiceRoll();

    // Current round number
    private int currentRound;

    // Current turn number within a round
    private int currentTurn;

    // Player currently taking a turn
    private Player currentPlayer;

    // Player taking the next turn
    private Player nextPlayer;

    // AI Class-generated help for the current player
    private Help currentHelp;

    // Queue for determining the order of players
    private Queue<Player> turnOrder;

    /**
     * Private constructor for the singleton pattern.
     * Initializes a new tournament with default settings.
     */
    private Tournament() {
        currentRound = 1;
        currentTurn = 1;
        ScoreCard scoreCard = new ScoreCard();
    }

    /**
     * Returns the singleton instance of the Tournament class.
     *
     * @return the Tournament instance
     */
    public static Tournament getInstance() {
        if (instance == null) {
            instance = new Tournament();
        }
        return instance;
    }

    /**
     * Starts a new game by creating a new instance of the Tournament class.
     */
    public static void startNewGame() {
        instance = new Tournament();
        Logger.log("New game started");
    }

    /**
     * Loads a game from a serialized string.
     *
     * @param gameString the serialized game string
     */
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

        instance = new Tournament();
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

    // Getters and setters for fields
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


    /**
     * Handles a player's decision to stand, ending their turn.
     * Logs dice rolls and calculates available categories.
     */
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

    /**
     * Handles a player's decision to reroll, keeping selected dice.
     * Logs dice rolls and calculates available categories.
     */
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

    /**
     * Keeps all rolled dice for the current turn.
     */
    private void keepAllDice() {
        diceRoll.keepAll();
    }

    public int getTurnNumber() {
        return currentTurn;
    }

    /**
     * Handles category selection by the player.
     *
     * @param category the category to select
     */
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

    /**
     * Finishes the current player's turn and prepares the game for the next player or round.
     */
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

    /**
     * Determines the order of players for the next round based on their scores.
     */
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

    /**
     * Checks if the game is over.
     *
     * @return true if the game is over, false otherwise
     */
    public boolean isOver() {
        return scoreCard.isComplete();
    }

    /**
     * Returns the result of the game as a formatted string.
     *
     * @return the result of the game
     */
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

    /**
     * Resets the AI Class help for the current player.
     */
    public void resetHelp() {
        currentHelp = null;

        for (Die die : diceRoll.getDice()) {
            die.setMarkedForHelpKeep(false);
        }
    }

    /**
     * Generates AI Class help for the current player.
     */
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

    /**
     * Handles the computer player's turn logic.
     */
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

    /**
     * Checks if the game is over and logs the result if true.
     */
    public void checkOver() {
        if (isOver()) {
            Logger.log("Game over");
            Logger.log(getResultText());
            Logger.log("");
        }
    }
}

package com.yahtzee.View;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Button;
import android.widget.TableLayout;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.yahtzee.Model.Category;
import com.yahtzee.Model.DiceRoll;
import com.yahtzee.Model.Die;
import com.yahtzee.Model.Tournament;
import com.yahtzee.Model.CardEntry;
import com.yahtzee.Model.Player;

import com.yahtzee.R;

import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * The main activity for the Yahtzee game.
 * This activity handles the game display, user interactions, and game logic.
 */
public class GameActivity extends AppCompatActivity {

    TableLayout scoreCardTable;
    Tournament tournament;
    DiceRoll diceRoll;

    private static final String TAG = "GameActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);

        tournament = Tournament.getInstance();
        diceRoll = tournament.getDiceRoll();

        initGameDisplay();
    }

    /**
     * Initializes the game display.
     * Checks if the tournament is over and starts the ResultActivity if it is.
     * Otherwise, initializes the round display, current player display, scorecard,
     * turn display, help button, computer roll confirm button, log button and save game button.
     */
    private void initGameDisplay() {
        if (tournament.isOver()) {
            Intent intent = new Intent(this, ResultActivity.class);
            startActivity(intent);
        }
        initRoundDisplay();
        initCurrentPlayerDisplay();
        initScoreCard();
        initTurnDisplay();
        initHelpButton();
        initComputerRollConfirmButton();
        initLogButton();
        initSaveGameButton();
    }

    /**
     * Initializes the round display by setting the round number text.
     */
    private void initRoundDisplay() {
        TextView roundDisplay = findViewById(R.id.roundNumber);
        roundDisplay.setText(tournament.getCurrentRound() + "");
    }

    /**
     * Initializes the current player display by setting the current player's name.
     * If the current player is null, starts the TieBreakerActivity.
     */
    private void initCurrentPlayerDisplay() {
        TextView currentPlayerDisplay = findViewById(R.id.playerName);
        Player currentPlayer = tournament.getCurrentPlayer();
        if (currentPlayer == null) {
            // Start the TieBreakerActivity
            Intent intent = new Intent(this, TieBreakerActivity.class);
            startActivity(intent);
        } else {
            currentPlayerDisplay.setText(tournament.getCurrentPlayer().getName());
        }
    }

    /**
     * Initializes the scorecard display.
     * Removes all existing rows except the header.
     * Adds a new row for each category in the scorecard.
     * Sets the background color of available categories to green.
     * Adds an onclick listener to valid categories when all dice are kept.
     * Displays the total score for each player.
     */
    private void initScoreCard() {
        scoreCardTable = findViewById(R.id.scoreCardTable);
        scoreCardTable.removeViews(1, scoreCardTable.getChildCount() - 1);


        List<CardEntry> entries = tournament.getScoreCard().getEntries();

        List<Integer> potentialKeptDiceValues = new ArrayList<>(diceRoll.getKeptDiceValues());
        potentialKeptDiceValues.addAll(diceRoll.getMarkedDiceValues());


        List<Category> potentialCategories = tournament.getScoreCard().getPotentialCategories(potentialKeptDiceValues);
        List<Category> validCategories = tournament.getScoreCard().getValidCategories(diceRoll.getKeptDiceValues());

        for (CardEntry entry : tournament.getScoreCard().getEntries()) {
            TableRow row = new TableRow(this);
            TextView category = new TextView(this);
            category.setText(entry.getCategory().toString());
            row.addView(category);

            TextView round = new TextView(this);
            round.setText(entry.getRound().map(Object::toString).orElse(""));
            row.addView(round);

            TextView winner = new TextView(this);
            winner.setText(entry.getWinner().map(Player::getName).orElse(""));
            row.addView(winner);

            TextView score = new TextView(this);
            score.setText(entry.getScore().map(Object::toString).orElse(""));
            row.addView(score);

            // If the category is available, set the background color to green
            if (potentialCategories.contains(entry.getCategory()) || validCategories.contains(entry.getCategory())) {
                row.setBackgroundColor(getResources().getColor(R.color.green));
            }

            // If all dice are kept, add onclick listener to the row
            if (diceRoll.isAllDiceKept() && validCategories.contains(entry.getCategory())) {
                row.setOnClickListener(v -> {
                    tournament.selectCategory(entry.getCategory());
                    initGameDisplay();
                });

                // Show the score for the category in blue
                score.setTextColor(getResources().getColor(R.color.blue));
                score.setText(entry.getCategory().calculateScore(diceRoll.getKeptDiceValues()) + "");
            }

            scoreCardTable.addView(row);

        }

        TextView totalScore = findViewById(R.id.totalScore);
        StringBuilder totalScoreText = new StringBuilder();
        for (Player player : tournament.getPlayers()) {
            totalScoreText.append(player.getName()).append("'s Total Score: ").append(tournament.getScoreCard().getTotalScore(player)).append("\n");
        }
        totalScore.setText(totalScoreText.toString());
    }

    /**
     * Initializes the turn display by setting the turn number text.
     * Also initializes the dice roll, stand button, and reroll button.
     */
    private void initTurnDisplay() {
        TextView turnNumber = findViewById(R.id.turnNumber);
        turnNumber.setText(tournament.getTurnNumber() + "");
        initDiceRoll();
        initStandButton();
        initRerollButton();
    }

    /**
     * Initializes the dice roll display.
     * Removes all existing dice views.
     * Adds a new view for each die in the dice roll.
     * Sets the die value text and kept state.
     * Adds an onclick listener to each die to toggle its kept state.
     * Adds an onclick listener to the roll all button to roll all dice.
     */
    private void initDiceRoll() {
        LinearLayout diceContainer = findViewById(R.id.diceContainer);
        diceContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);
        for (Die die : diceRoll.getDice()) {
            View diceView = inflater.inflate(R.layout.dice_item, diceContainer, false);


            TextView dieUp = diceView.findViewById(R.id.dieUp);
            dieUp.setVisibility(View.GONE);
            TextView dieDown = diceView.findViewById(R.id.dieDown);
            dieDown.setVisibility(View.GONE);
            TextView dieRandom = diceView.findViewById(R.id.dieRandom);
            dieRandom.setVisibility(View.GONE);
            SwitchCompat dieKept = diceView.findViewById(R.id.dieKept);
            dieKept.setVisibility(View.GONE);

            TextView valueView = diceView.findViewById(R.id.dieValue);
            int value = die.getValue();
            valueView.setText(value == 0 ? "🎲" : value + "");


            dieKept.setChecked(die.isMarked());
            boolean currentPlayerIsHuman = isHumanPlayer();
            if (!die.isKept() && tournament.getTurnNumber() < 3 && currentPlayerIsHuman) {
                dieKept.setVisibility(View.VISIBLE);
                dieKept.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    die.setMarked(isChecked);
                    initGameDisplay();
                });
            }

            if (!die.isMarked() && !die.isKept()) {

                dieUp.setVisibility(View.VISIBLE);
                dieUp.setOnClickListener(v -> {
                    die.setValue(die.getValue() + 1);
                    diceRoll.unMarkAllForHelpKeep();
                    valueView.setText(die.getValue() + "");
                    initGameDisplay();
                });

                dieDown.setVisibility(View.VISIBLE);
                dieDown.setOnClickListener(v -> {
                    die.setValue(die.getValue() - 1);
                    diceRoll.unMarkAllForHelpKeep();
                    die.setMarkedForHelpKeep(false);
                    valueView.setText(die.getValue() + "");
                    initGameDisplay();
                });

                dieRandom.setVisibility(View.VISIBLE);
                dieRandom.setOnClickListener(v -> {
                    die.roll();
                    diceRoll.unMarkAllForHelpKeep();
                    valueView.setText(die.getValue() + "");
                    initGameDisplay();
                });

            }

            if (die.isMarkedForHelpKeep()) {
                // Set the background color to green
                diceView.setBackgroundColor(getResources().getColor(R.color.green));
            }

            if (value == 0) {
                dieKept.setVisibility(View.GONE);
            }


            diceView.setId(View.generateViewId());
            diceContainer.addView(diceView);
        }

        Button rollAllButton = findViewById(R.id.rollAllButton);
        rollAllButton.setOnClickListener(v -> {
            diceRoll.roll();
            clearHelpText();
            initGameDisplay();
        });
        rollAllButton.setEnabled(!diceRoll.isAllDiceKept());
    }

    /**
     * Initializes the stand button.
     * Sets the button text to "Finish Round" if it is the last turn.
     * Adds an onclick listener to the button to stand.
     * Disables the button if it is not the player's turn or if all dice are kept.
     */
    private void initStandButton() {
        Button standButton = findViewById(R.id.standButton);

        if (isComputerPlayer()) {
            standButton.setVisibility(View.GONE);
        } else {
            standButton.setVisibility(View.VISIBLE);
        }

        standButton.setOnClickListener(v -> {
            tournament.stand();
            clearHelpText();
            initGameDisplay();
        });

        standButton.setEnabled(diceRoll.isAllDiceRolled() && !diceRoll.isAllDiceKept());

        if (tournament.getTurnNumber() == 3) {
            standButton.setText("Finish Round");
        }

    }

    /**
     * Initializes the reroll button.
     * Adds an onclick listener to the button to reroll the selected dice.
     * Disables the button if it is not the player's turn or if no dice are selected.
     */
    private void initRerollButton() {
        Button reRollButton = findViewById(R.id.reRollButton);

        if (isComputerPlayer()) {
            reRollButton.setVisibility(View.GONE);
        } else {
            reRollButton.setVisibility(View.VISIBLE);
        }

        reRollButton.setOnClickListener(v -> {
            tournament.reRoll();
            clearHelpText();
            initGameDisplay();
        });

        if (diceRoll.isAllDiceRolled() && tournament.getTurnNumber() < 3 && !diceRoll.isAllDiceKept()) {
            reRollButton.setEnabled(true);
        } else {
            reRollButton.setEnabled(false);
        }

    }

    /**
     * Initializes the help button.
     * Adds an onclick listener to the button to get help.
     * Disables the button if it is not the player's turn or if all dice are kept.
     */
    private void initHelpButton() {
        Button helpButton = findViewById(R.id.getHelpButton);
        TextView helpText = findViewById(R.id.helpText);

        if (isComputerPlayer()) {
            helpButton.setVisibility(View.GONE);
            helpText.setVisibility(View.GONE);
            helpText.setText("");
        } else {
            helpButton.setVisibility(View.VISIBLE);
            if (!helpText.getText().equals("")) {
                helpText.setVisibility(View.VISIBLE);
            }
        }

        helpButton.setOnClickListener(v -> {
            if (diceRoll.isAllDiceKept()) {
                clearHelpText();
                tournament.getHelp();
                String bestCategory = tournament.getCurrentHelp().getTargetCategory().toString();
                helpText.setText("Best Category: " + bestCategory);
            } else {
                tournament.getHelp();
                helpText.setText(tournament.getCurrentHelp().toString());
            }
            initGameDisplay();
        });

        helpButton.setEnabled(diceRoll.isAllDiceRolled() && !isComputerPlayer());


        if (tournament.getTurnNumber() == 3 && !diceRoll.isAllDiceKept()) {
            helpButton.setEnabled(false);
        }

    }

    /**
     * Initializes the computer roll confirm button.
     * Adds an onclick listener to the button to confirm the computer's roll.
     * Disables the button if it is not the computer's turn or if all dice are kept.
     */
    private void initComputerRollConfirmButton() {
        Button computerRollConfirmButton = findViewById(R.id.confirmComputerRollButton);

        if (isHumanPlayer()) {
            computerRollConfirmButton.setVisibility(View.GONE);
        } else {
            computerRollConfirmButton.setVisibility(View.VISIBLE);
        }

        computerRollConfirmButton.setOnClickListener(v -> {
            tournament.confirmComputerRoll();
            initGameDisplay();
        });
        computerRollConfirmButton.setEnabled(isComputerPlayer() && diceRoll.isAllDiceRolled() && !diceRoll.isAllDiceKept());
    }

    /**
     * Initializes the log button.
     * Adds an onclick listener to the button to open the LogActivity.
     */
    private void initLogButton() {
        Button logButton = findViewById(R.id.logButton);
        logButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, LogActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Initializes the save game button.
     * Adds an onclick listener to the button to open the file picker.
     */
    private void initSaveGameButton() {
        Button saveGameButton = findViewById(R.id.saveGameButton);
        saveGameButton.setOnClickListener(view -> openFilePicker());
    }

    /**
     * Clears the help text.
     */
    private void clearHelpText() {
        TextView helpText = findViewById(R.id.helpText);
        helpText.setText("");
    }

    /**
     * Opens the file picker to allow the user to choose a location and filename to save the game.
     */
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("text/plain"); // The type of file we want to create is plain text
        intent.putExtra(Intent.EXTRA_TITLE, "game_save.txt"); // Default filename
        startActivityForResult(intent, 1001);
    }

    /**
     * Handles the result of the file picker activity.
     * If the user selects a file, saves the game data to the file.
     *
     * @param requestCode The request code passed to startActivityForResult.
     * @param resultCode  The result code returned by the child activity.
     * @param data  
    The intent returned by the child activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            saveGameToFile(fileUri);
        }
    }

    /**
     * Saves the game data to a file.
     *
     * @param fileUri The URI of the file to save the game data to.
     */
    private void saveGameToFile(Uri fileUri) {
        String gameData = Tournament.getInstance().toString();

        try (OutputStream outputStream = getContentResolver().openOutputStream(fileUri);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {

            writer.write(gameData);
            Toast.makeText(this, "Game saved successfully!", Toast.LENGTH_SHORT).show();


            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        } catch (IOException e) {
            Log.e(TAG, "Error saving game data", e);
            Toast.makeText(this, "Error saving game", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Checks if the current player is a computer player.
     *
     * @return true if the current player is a computer player, false otherwise
     */
    private boolean isComputerPlayer() {
        return tournament.getCurrentPlayer() != null && tournament.getCurrentPlayer().isComputer();
    }

    /**
     * Checks if the current player is a human player.
     *
     * @return true if the current player is a human player, false otherwise.
     */
    private boolean isHumanPlayer() {
        return tournament.getCurrentPlayer() != null && !tournament.getCurrentPlayer().isComputer();
    }

}


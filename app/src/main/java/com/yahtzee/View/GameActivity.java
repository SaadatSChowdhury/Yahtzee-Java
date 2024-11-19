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
import com.yahtzee.Model.Game;
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

public class GameActivity extends AppCompatActivity {

    TableLayout scoreCardTable;


    Game game;
    DiceRoll diceRoll;

    private static final String TAG = "GameActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);

        game = Game.getInstance();
        diceRoll = game.getDiceRoll();

        initGameDisplay();
    }

    private void initGameDisplay() {
        if (game.isOver()) {
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

    private void initRoundDisplay() {
        TextView roundDisplay = findViewById(R.id.roundNumber);
        roundDisplay.setText(game.getCurrentRound() + "");
    }

    private void initCurrentPlayerDisplay() {
        TextView currentPlayerDisplay = findViewById(R.id.playerName);
        Player currentPlayer = game.getCurrentPlayer();
        if (currentPlayer == null) {
            // Start the TieBreakerActivity
            Intent intent = new Intent(this, TieBreakerActivity.class);
            startActivity(intent);
        } else {
            currentPlayerDisplay.setText(game.getCurrentPlayer().getName());
        }
    }

    private void initScoreCard() {
        scoreCardTable = findViewById(R.id.scoreCardTable);
        // Remove all views except the header
        scoreCardTable.removeViews(1, scoreCardTable.getChildCount() - 1);


        List<CardEntry> entries = game.getScoreCard().getEntries();

        List<Integer> potentialKeptDiceValues = new ArrayList<>(diceRoll.getKeptDiceValues());
        potentialKeptDiceValues.addAll(diceRoll.getMarkedDiceValues());


        List<Category> potentialCategories = game.getScoreCard().getPotentialCategories(potentialKeptDiceValues);
        List<Category> validCategories = game.getScoreCard().getValidCategories(diceRoll.getKeptDiceValues());

        for (CardEntry entry : game.getScoreCard().getEntries()) {
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
                    game.selectCategory(entry.getCategory());
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
        for (Player player : game.getPlayers()) {
            totalScoreText.append(player.getName()).append("'s Total Score: ").append(game.getScoreCard().getTotalScore(player)).append("\n");
        }
        totalScore.setText(totalScoreText.toString());
    }

    private void initTurnDisplay() {
        TextView turnNumber = findViewById(R.id.turnNumber);
        turnNumber.setText(game.getTurnNumber() + "");
        initDiceRoll();
        initStandButton();
        initRerollButton();
    }

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
            if (!die.isKept() && game.getTurnNumber() < 3 && currentPlayerIsHuman) {
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
        // Hide the roll all button
//        rollAllButton.setVisibility(View.GONE);
    }

    private void initStandButton() {
        Button standButton = findViewById(R.id.standButton);

        if (isComputerPlayer()) {
            standButton.setVisibility(View.GONE);
        } else {
            standButton.setVisibility(View.VISIBLE);
        }

        standButton.setOnClickListener(v -> {
            game.stand();
            clearHelpText();
            initGameDisplay();
        });

        standButton.setEnabled(diceRoll.isAllDiceRolled() && !diceRoll.isAllDiceKept());

        if (game.getTurnNumber() == 3) {
            standButton.setText("Finish Round");
        }

    }

    private void initRerollButton() {
        Button reRollButton = findViewById(R.id.reRollButton);

        if (isComputerPlayer()) {
            reRollButton.setVisibility(View.GONE);
        } else {
            reRollButton.setVisibility(View.VISIBLE);
        }

        reRollButton.setOnClickListener(v -> {
            game.reRoll();
            clearHelpText();
            initGameDisplay();
        });

        if (diceRoll.isAllDiceRolled() && game.getTurnNumber() < 3 && !diceRoll.isAllDiceKept()) {
            reRollButton.setEnabled(true);
        } else {
            reRollButton.setEnabled(false);
        }

    }

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
                game.getHelp();
                String bestCategory = game.getCurrentHelp().getTargetCategory().toString();
                helpText.setText("Best Category: " + bestCategory);
            } else {
                game.getHelp();
                helpText.setText(game.getCurrentHelp().toString());
            }
            initGameDisplay();
        });

        helpButton.setEnabled(diceRoll.isAllDiceRolled() && !isComputerPlayer());


        if (game.getTurnNumber() == 3 && !diceRoll.isAllDiceKept()) {
            helpButton.setEnabled(false);
        }

    }

    private void initComputerRollConfirmButton() {
        Button computerRollConfirmButton = findViewById(R.id.confirmComputerRollButton);

        if (isHumanPlayer()) {
            computerRollConfirmButton.setVisibility(View.GONE);
        } else {
            computerRollConfirmButton.setVisibility(View.VISIBLE);
        }

        computerRollConfirmButton.setOnClickListener(v -> {
            game.confirmComputerRoll();
            initGameDisplay();
        });
        computerRollConfirmButton.setEnabled(isComputerPlayer() && diceRoll.isAllDiceRolled() && !diceRoll.isAllDiceKept());
    }

    private void initLogButton() {
        Button logButton = findViewById(R.id.logButton);
        logButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, LogActivity.class);
            startActivity(intent);
        });
    }

    private void initSaveGameButton() {
        Button saveGameButton = findViewById(R.id.saveGameButton);
        saveGameButton.setOnClickListener(view -> openFilePicker());
    }

    private void clearHelpText() {
        TextView helpText = findViewById(R.id.helpText);
        helpText.setText("");
    }

    // Open file picker to let the user choose the location and file name.
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("text/plain"); // The type of file we want to create is plain text
        intent.putExtra(Intent.EXTRA_TITLE, "game_save.txt"); // Default filename
        startActivityForResult(intent, 1001);
    }

    // Activity result callback to handle the file picker result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            saveGameToFile(fileUri);
        }
    }

    // Method to save game data to the selected file
    private void saveGameToFile(Uri fileUri) {
        String gameData = Game.getInstance().toString();

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

    private boolean isComputerPlayer() {
        return game.getCurrentPlayer() != null && game.getCurrentPlayer().isComputer();
    }

    private boolean isHumanPlayer() {
        return game.getCurrentPlayer() != null && !game.getCurrentPlayer().isComputer();
    }

}


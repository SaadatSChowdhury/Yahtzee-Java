package com.yahtzee.View;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.yahtzee.Model.CardEntry;
import com.yahtzee.Model.Tournament;
import com.yahtzee.Model.Player;
import com.yahtzee.R;

import java.util.List;

/**
 * Activity for displaying the results of the game.
 */
public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result);


        Tournament tournament = Tournament.getInstance();
        if (!tournament.isOver()) {
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        }

        initScoreCard();
        initResultText();
        initMainMenuButton();
        initLogButton();


    }

    /**
     * Initializes the scorecard display.
     * Removes all existing rows except the header.
     * Adds a new row for each category in the scorecard.
     */
    private void initScoreCard() {
        Tournament tournament = Tournament.getInstance();

        TableLayout scoreCardTable = (TableLayout) findViewById(R.id.scoreCardTableResult);
        // Remove all views except the header
        scoreCardTable.removeViews(1, scoreCardTable.getChildCount() - 1);


        List<CardEntry> entries = tournament.getScoreCard().getEntries();
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

            scoreCardTable.addView(row);

        }
    }

    /**
     * Initializes the result text display.
     * Sets the text of the result text view to the result text of the tournament.
     */
    private void initResultText() {
        Tournament tournament = Tournament.getInstance();
        TextView resultText = (TextView) findViewById(R.id.resultText);
        resultText.setText(tournament.getResultText());
    }

    /**
     * Initializes the main menu button.
     * Sets an OnClickListener on the main menu button to start the MainActivity.
     */
    private void initMainMenuButton() {
        findViewById(R.id.mainMenuButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Initializes the log button.
     * Sets an OnClickListener on the log button to start the LogActivity.
     */
    private void initLogButton() {
        findViewById(R.id.logButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, LogActivity.class);
            startActivity(intent);
        });
    }
}
package com.yahtzee.View;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.yahtzee.Model.CardEntry;
import com.yahtzee.Model.Game;
import com.yahtzee.Model.Player;
import com.yahtzee.R;

import java.util.List;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result);


        Game game = Game.getInstance();
        if (!game.isOver()) {
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        }

        initScoreCard();
        initResultText();
        initMainMenuButton();
        initLogButton();


    }

    private void initScoreCard() {
        Game game = Game.getInstance();

        TableLayout scoreCardTable = (TableLayout) findViewById(R.id.scoreCardTableResult);
        // Remove all views except the header
        scoreCardTable.removeViews(1, scoreCardTable.getChildCount() - 1);


        List<CardEntry> entries = game.getScoreCard().getEntries();
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

            scoreCardTable.addView(row);

        }
    }

    private void initResultText() {
        Game game = Game.getInstance();
        TextView resultText = (TextView) findViewById(R.id.resultText);
        resultText.setText(game.getResultText());
    }

    private void initMainMenuButton() {
        findViewById(R.id.mainMenuButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void initLogButton() {
        findViewById(R.id.logButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, LogActivity.class);
            startActivity(intent);
        });
    }
}
package com.yahtzee.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yahtzee.Model.Game;
import com.yahtzee.Model.Player;
import com.yahtzee.R;

import java.util.Random;


public class TieBreakerActivity extends AppCompatActivity {

    private TextView computerDieValue;
    private TextView humanDieValue;
    private TextView resultText;

    private TextView humanDieUp, humanDieDown, humanDieRandom;
    private TextView computerDieUp, computerDieDown, computerDieRandom;

    private Button autoRollButton;
    private Button manualRollButton;
    private Button continueButton;

    private Random random;

    private int humanDie = 0;    // Default value for human die
    private int computerDie = 0; // Default value for computer die

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tie_breaker);

        // Initialize Views
        computerDieValue = findViewById(R.id.computerDieValue);
        humanDieValue = findViewById(R.id.humanDieValue);
        resultText = findViewById(R.id.resultText);

        humanDieUp = findViewById(R.id.humanDieUp);
        humanDieDown = findViewById(R.id.humanDieDown);
        humanDieRandom = findViewById(R.id.humanDieRandom);

        computerDieUp = findViewById(R.id.computerDieUp);
        computerDieDown = findViewById(R.id.computerDieDown);
        computerDieRandom = findViewById(R.id.computerDieRandom);

        autoRollButton = findViewById(R.id.autoRollButton);
        manualRollButton = findViewById(R.id.manualRollButton);
        continueButton = findViewById(R.id.continueButton);

        continueButton.setVisibility(View.GONE);

        random = new Random();

        // Set up listeners for human controls
        humanDieUp.setOnClickListener(v -> {
            humanDie = adjustDieValue(humanDie, true);
            humanDieValue.setText(String.valueOf(humanDie));
        });

        humanDieDown.setOnClickListener(v -> {
            humanDie = adjustDieValue(humanDie, false);
            humanDieValue.setText(String.valueOf(humanDie));
        });

        humanDieRandom.setOnClickListener(v -> {
            humanDie = rollDice();
            humanDieValue.setText(String.valueOf(humanDie));
        });

        // Set up listeners for computer controls
        computerDieUp.setOnClickListener(v -> {
            computerDie = adjustDieValue(computerDie, true);
            computerDieValue.setText(String.valueOf(computerDie));
        });

        computerDieDown.setOnClickListener(v -> {
            computerDie = adjustDieValue(computerDie, false);
            computerDieValue.setText(String.valueOf(computerDie));
        });

        computerDieRandom.setOnClickListener(v -> {
            computerDie = rollDice();
            computerDieValue.setText(String.valueOf(computerDie));
        });

        // Auto Roll Button Logic
        autoRollButton.setOnClickListener(v -> {
            computerDie = rollDice();
            humanDie = rollDice();

            updateDiceValues(computerDie, humanDie);
            determineWinner(computerDie, humanDie);
            postRollActions();
        });

        // Manual Roll Button Logic
        manualRollButton.setOnClickListener(v -> {
            if (computerDie == 0 || humanDie == 0) {
                Toast.makeText(TieBreakerActivity.this, "Please roll both dice first.", Toast.LENGTH_SHORT).show();
                resultText.setText("Please roll both dice first.");
                return;
            }
            updateDiceValues(computerDie, humanDie);
            determineWinner(computerDie, humanDie);
            postRollActions();
        });

        // Continue Button Logic
        continueButton.setOnClickListener(v -> {

            Game game = Game.getInstance();
            Player human = new Player("Human", false);
            Player computer = new Player("Computer", true);


            if (computerDie > humanDie) {
                game.setPlayerOrder(computer, human);
            } else if (humanDie > computerDie) {
                game.setPlayerOrder(human, computer);
            } else {
                Toast.makeText(TieBreakerActivity.this, "It's a tie! Roll again.", Toast.LENGTH_SHORT).show();
                resultText.setText("It's a tie! Roll again.");
                return;
            }


            Intent intent = new Intent(TieBreakerActivity.this, GameActivity.class);
            startActivity(intent);
        });
    }

    // Roll Dice Method
    private int rollDice() {
        return random.nextInt(6) + 1; // Generates a number between 1 and 6
    }

    // Adjust the die value up or down; it should cycle from 1 to 6
    private int adjustDieValue(int current, boolean isIncrement) {
        if (isIncrement) {
            return current < 6 ? current + 1 : 1;
        } else {
            return current > 1 ? current - 1 : 6;
        }
    }

    // Update the displayed dice values
    private void updateDiceValues(int computerRoll, int humanRoll) {
        if (computerRoll == 0 || humanRoll == 0) {
            return;
        }
        computerDieValue.setText(String.valueOf(computerRoll));
        humanDieValue.setText(String.valueOf(humanRoll));
    }

    // Determine and display the winner
    private void determineWinner(int computerRoll, int humanRoll) {
        if (computerRoll > humanRoll) {
            resultText.setText("Computer will be playing first!");
        } else if (humanRoll > computerRoll) {
            resultText.setText("You will play first!");
        } else {
            resultText.setText("It's a tie! Roll again.");
        }
    }

    private void postRollActions() {

        if (computerDie == humanDie) {
            return;
        }

        // Hide the roll buttons
        autoRollButton.setVisibility(View.GONE);
        manualRollButton.setVisibility(View.GONE);

        // Hide the up and down buttons
        humanDieUp.setVisibility(View.GONE);
        humanDieDown.setVisibility(View.GONE);
        humanDieRandom.setVisibility(View.GONE);
        computerDieUp.setVisibility(View.GONE);
        computerDieDown.setVisibility(View.GONE);
        computerDieRandom.setVisibility(View.GONE);


        // Show the continue button
        continueButton.setVisibility(View.VISIBLE);

    }
}

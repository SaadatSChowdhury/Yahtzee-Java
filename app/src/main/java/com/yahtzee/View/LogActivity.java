package com.yahtzee.View;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.yahtzee.R;

/**
 * Activity for displaying the game log.
 */
public class LogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_log);

        // Get the log text view and set its text to the game log
        TextView logTextView = findViewById(R.id.logText);
        logTextView.setText(com.yahtzee.Model.Logger.getInstance().toString());

        // Get the back button and set an onClickListener to finish the activity
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }
}
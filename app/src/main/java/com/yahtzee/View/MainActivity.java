package com.yahtzee.View;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.yahtzee.Model.Game;
import com.yahtzee.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    Button newGameButton;
    Button loadGameButton;

    private static final int PICK_SERIAL_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        newGameButton = findViewById(R.id.newGameButton);
        loadGameButton = findViewById(R.id.loadGameButton);

        newGameButton.setOnClickListener(v -> {
            Game.startNewGame();
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        });


        loadGameButton.setOnClickListener(view -> {

            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, PICK_SERIAL_FILE);


        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        if (requestCode == PICK_SERIAL_FILE && resultCode == RESULT_OK) {

            Uri uri = resultData.getData();

            StringBuilder stringBuilder = new StringBuilder();
            try (InputStream inputStream =
                         getContentResolver().openInputStream(uri);
                 BufferedReader reader = new BufferedReader(
                         new InputStreamReader(Objects.requireNonNull(inputStream)))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Add a newline character to the end of each line
                    // because BufferedReader.readLine() strips them
                    stringBuilder.append(line).append("\n");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String serialString = stringBuilder.toString();

            Game.setFromString(serialString);


            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);

        }


    }


}
package com.example.clicker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

// Ваш импорт

public class LevelsActivity extends AppCompatActivity {

    private Button replayButton1, replayButton2, replayButton3, replayButton4, replayButton5;
    private String username;
    private com.example.clicker.DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);
        databaseHelper = new com.example.clicker.DatabaseHelper(this);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        System.out.println("Received username from Main in Level: " + username);

        // Находим кнопки в макете
        replayButton1 = findViewById(R.id.replay_button_1);
        replayButton2 = findViewById(R.id.replay_button_2);
        replayButton3 = findViewById(R.id.replay_button_3);
        replayButton4 = findViewById(R.id.replay_button_4);
        replayButton5 = findViewById(R.id.replay_button_5);

        replayButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity(1);
            }
        });

        replayButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity(2);
            }
        });

        replayButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity(3);
            }
        });

        replayButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity(4);
            }
        });

        replayButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity(5);
            }
        });
        replayButton1.setVisibility(View.INVISIBLE);
        replayButton2.setVisibility(View.INVISIBLE);
        replayButton3.setVisibility(View.INVISIBLE);
        replayButton4.setVisibility(View.INVISIBLE);
        replayButton5.setVisibility(View.INVISIBLE);

        // Проверяем, какие уровни были пройдены и отображаем соответствующие кнопки
        checkCompletedLevels();
    }
    private String getCurrentUsername() {
        // Получаем имя пользователя из Intent, переданного из LoginActivity
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        System.out.println("Received username: " + username);
        return username != null ? username : ""; // Возвращаем имя пользователя или пустую строку, если имя не найдено

    }
    // Метод для открытия главного экрана на выбранном уровне
    private void openMainActivity(int levelNumber) {
        Intent intent = new Intent(LevelsActivity.this, MainActivity.class);
        intent.putExtra("levelNumber", levelNumber);
        intent.putExtra("username", username); // Добавляем имя пользователя в Intent
        System.out.println("Username was gone from Level to Main: " + username);
        startActivity(intent);
        finish();
    }


    // Метод для проверки пройденных уровней и отображения соответствующих кнопок
    private void checkCompletedLevels() {
        int currentlevel = databaseHelper.getUserLevel(getCurrentUsername());
        switch (currentlevel) {
            case 1:
                replayButton1.setVisibility(View.VISIBLE);
                break;
            case 2:
                replayButton1.setVisibility(View.VISIBLE);
                replayButton2.setVisibility(View.VISIBLE);
                break;
            case 3:
                replayButton1.setVisibility(View.VISIBLE);
                replayButton2.setVisibility(View.VISIBLE);
                replayButton3.setVisibility(View.VISIBLE);
                break;
            case 4:
                replayButton1.setVisibility(View.VISIBLE);
                replayButton2.setVisibility(View.VISIBLE);
                replayButton3.setVisibility(View.VISIBLE);
                replayButton4.setVisibility(View.VISIBLE);
                break;
            case 5:
                replayButton1.setVisibility(View.VISIBLE);
                replayButton2.setVisibility(View.VISIBLE);
                replayButton3.setVisibility(View.VISIBLE);
                replayButton4.setVisibility(View.VISIBLE);
                replayButton5.setVisibility(View.VISIBLE);
                break;
        }
    }

}

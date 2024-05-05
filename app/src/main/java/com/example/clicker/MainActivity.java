package com.example.clicker;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private int currentBossIndex = 0;
    private int[] bossHealths = {100, 200, 300, 400, 500};
    private Button bossButton;
    private Button restartlevelButton;

    private Button startgameButton;
    private Button nextBossButton;
    private TextView healthTextView;
    private TextView defeatedTextView;
    private TextView levelTextView;
    private TextView timerTextView;
    private CountDownTimer bossTimer;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        int level = databaseHelper.getUserLevel(getCurrentUsername());

        restartlevelButton = findViewById(R.id.restart_level_button);
        startgameButton = findViewById(R.id.start_game_button);
        bossButton = findViewById(R.id.boss_button);
        nextBossButton = findViewById(R.id.next_boss_button);
        healthTextView = findViewById(R.id.health_text_view);
        defeatedTextView = findViewById(R.id.defeated_text_view);
        levelTextView = findViewById(R.id.level_text_view);
        timerTextView = findViewById(R.id.timer_text_view);

        currentBossIndex=level-1;
        bossHealths[currentBossIndex] = calculateBossHealth(currentBossIndex);
        updateHealthText();
        updateLevelText(level); // Обновляем уровень на главном экране

        bossButton.setEnabled(false); // Кнопка "Босс" неактивна до начала игры
        restartlevelButton.setVisibility(View.INVISIBLE); // Скрываем кнопку "Босс"
        levelTextView.setVisibility(View.VISIBLE); // Показываем уровень

        bossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bossHealths[currentBossIndex] -= 10;
                updateHealthText();
                if (bossHealths[currentBossIndex] <= 0) {
                    bossDefeated();
                }
            }
        });

        nextBossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextBoss();
            }
        });

        restartlevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartLevel();
            }
        });

        startgameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });
    }

    private void startGame() {
        startgameButton.setVisibility(View.INVISIBLE); // Скрываем кнопку "Start Game"
        restartlevelButton.setVisibility(View.INVISIBLE); // Скрываем кнопку перезапуска
        bossButton.setVisibility(View.VISIBLE); // Показываем кнопку "Босс"
        bossButton.setEnabled(true); // Активируем кнопку "Босс"
        healthTextView.setVisibility(View.VISIBLE); // Показываем здоровье босса
        timerTextView.setVisibility(View.VISIBLE); // Показываем таймер
        startBossTimer(); // Запускаем таймер

        // Получаем текущий уровень игрока из базы данных
        int level = databaseHelper.getUserLevel(getCurrentUsername());
        updateLevelText(level); // Вызываем с аргументом
    }

    private String getCurrentUsername() {
        // Получаем имя пользователя из Intent, переданного из LoginActivity
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        return username != null ? username : ""; // Возвращаем имя пользователя или пустую строку, если имя не найдено
    }

    private void updateHealthText() {
        healthTextView.setText("Здоровье босса: " + bossHealths[currentBossIndex]);
    }

    private void bossDefeated() {
        stopBossTimer();
        defeatedTextView.setVisibility(View.VISIBLE);
        nextBossButton.setVisibility(View.VISIBLE);
        bossButton.setVisibility(View.INVISIBLE);
        timerTextView.setVisibility(View.INVISIBLE); // Скрываем таймер при победе
    }

    private void startBossTimer() {
        int bossTimerDuration = (currentBossIndex + 1) * 5 * 1000;
        bossTimer = new CountDownTimer(bossTimerDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                timerTextView.setText("Осталось: " + secondsRemaining + " секунд");
            }

            @Override
            public void onFinish() {
                restartlevelButton.setVisibility(View.VISIBLE); // Показываем кнопку "Босс"
            }
        }.start();
    }

    private void stopBossTimer() {
        if (bossTimer != null) {
            bossTimer.cancel();
        }
    }

    private void nextBoss() {
        bossButton.setVisibility(View.VISIBLE);
        bossButton.setEnabled(false); // Кнопка "Босс" неактивна до начала игры
        startgameButton.setVisibility(View.VISIBLE);
        stopBossTimer();
        currentBossIndex++;
        if (currentBossIndex < bossHealths.length) {
            bossHealths[currentBossIndex] = calculateBossHealth(currentBossIndex);
            updateHealthText();
            defeatedTextView.setVisibility(View.INVISIBLE);
            nextBossButton.setVisibility(View.INVISIBLE);
            int level = currentBossIndex + 1;
            updateLevelText(level); // Обновляем уровень на главном экране
            // Обновляем уровень игрока в базе данных
            databaseHelper.incrementUserLevel(getCurrentUsername());
        } else {
            Toast.makeText(this, "Поздравляем! Вы победили всех боссов!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLevelText(int level) {
        levelTextView.setText("Уровень: " + level);
    }

    private int calculateBossHealth(int index) {
        return (index + 1) * 100;
    }

    private void restartLevel() {
        restartlevelButton.setVisibility(View.INVISIBLE); // Скрываем кнопку перезапуска
        bossButton.setEnabled(false); // Кнопка "Босс" неактивна до начала игры
        startgameButton.setVisibility(View.VISIBLE);
        bossHealths[currentBossIndex] = calculateBossHealth(currentBossIndex);
        updateHealthText();
    }
}

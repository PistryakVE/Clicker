package com.example.clicker;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private int currentBossIndex = 0;
    int crystalCount;
    private int[] bossHealths = {100, 200, 300, 400, 500};
    private ImageButton bossButton;
    private Button shopButton;
    private Button selectlevelButton;

    private Button restartlevelButton;

    private Button startgameButton;
    private Button nextBossButton;
    private Button twoTimeButton;
    private Button twoAttackButton;
    private Button passtheBossButton;

    private TextView healthTextView;
    private TextView defeatedTextView;
    private TextView levelTextView;
    private TextView usileniyaTextView;

    private TextView timerTextView;
    private CountDownTimer bossTimer;
    private TextView crystalCountTextView;
    private com.example.clicker.DatabaseHelper databaseHelper;
    private long bossTimerDuration;
    private final int defaultattackDamage = 10;
    private  int attackDamage = defaultattackDamage;
    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHelper = new com.example.clicker.DatabaseHelper(this);
        int level = databaseHelper.getUserLevel(getCurrentUsername());
        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        if (username != null && !username.isEmpty()) {
            System.out.println("Received username from Level: " + username);
        } else {
            System.out.println("Received username from level: " + username);
        }

        int selectedlevel = intent.getIntExtra("levelNumber", -1);
        if (selectedlevel!=-1){
            level=selectedlevel;
            intent.removeExtra("levelNumber");
        }
        else{
            intent.removeExtra("levelNumber");
        }

        twoTimeButton = findViewById(R.id.twotime_button);
        twoAttackButton = findViewById(R.id.twoatack_button);
        passtheBossButton = findViewById(R.id.pass_the_boss_button);
        shopButton = findViewById(R.id.shop_button);
        restartlevelButton = findViewById(R.id.restart_level_button);
        selectlevelButton = findViewById(R.id.selectlevels_button);
        startgameButton = findViewById(R.id.start_game_button);
        bossButton = findViewById(R.id.boss_button);
        nextBossButton = findViewById(R.id.next_boss_button);
        healthTextView = findViewById(R.id.health_text_view);
        usileniyaTextView = findViewById(R.id.usileniya_text_view);
        defeatedTextView = findViewById(R.id.defeated_text_view);
        levelTextView = findViewById(R.id.level_text_view);
        timerTextView = findViewById(R.id.timer_text_view);
        crystalCountTextView = findViewById(R.id.crystal_count_text_view);
        currentBossIndex=level-1;
        bossHealths[currentBossIndex] = calculateBossHealth(currentBossIndex);
        updateHealthText();
        updateLevelText(level); // Обновляем уровень на главном экране

        bossTimerDuration = (currentBossIndex + 1) * 5 * 1000;

        bossButton.setEnabled(false); // Кнопка "Босс" неактивна до начала игры
        restartlevelButton.setVisibility(View.INVISIBLE); // Скрываем кнопку "Босс"
        levelTextView.setVisibility(View.VISIBLE); // Показываем уровень

        // Загружаем количество кристаллов из базы данных и обновляем его отображение
        crystalCount = databaseHelper.getCrystalCount(getCurrentUsername());
        updateCrystalCountText();

        twoTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemCount = databaseHelper.getItemCount(1, getCurrentUsername()); // Проверяем количество товара у игрока
                if (itemCount > 0) { // Если товар есть у игрока
                    // Уменьшаем количество товара на 1 в базе данных
                    databaseHelper.decreaseItemCount(1, getCurrentUsername());

                    // Увеличиваем время для текущего таймера в два раза
                    bossTimerDuration *= 2;
                    timerTextView.setVisibility(View.VISIBLE); // Показываем таймер

                    // Возможно, вы захотите обновить текстовое поле с отображением времени таймера
                    timerTextView.setText("Новое время: " + bossTimerDuration/1000 + " секунд");
                } else {
                    // Если товара нет у игрока, вы можете вывести сообщение об этом
                    Toast.makeText(MainActivity.this, "У вас нет такого товара", Toast.LENGTH_SHORT).show();
                }
            }
        });
        twoAttackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemCount = databaseHelper.getItemCount(2, getCurrentUsername()); // Проверяем количество товара у игрока
                if (itemCount > 0) { // Если товар есть у игрока
                    // Уменьшаем количество товара на 1 в базе данных
                    databaseHelper.decreaseItemCount(2, getCurrentUsername());

                    // Увеличиваем урон в два раза
                    attackDamage *= 2;

                    // Возможно, вы захотите обновить текстовое поле с отображением урона
                    Toast.makeText(MainActivity.this, "Урон удвоен: " + attackDamage, Toast.LENGTH_SHORT).show();
                } else {
                    // Если товара нет у игрока, вы можете вывести сообщение об этом
                    Toast.makeText(MainActivity.this, "У вас нет такого товара", Toast.LENGTH_SHORT).show();
                }
            }
        });

        passtheBossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemCount = databaseHelper.getItemCount(3, getCurrentUsername()); // Проверяем количество товара у игрока
                if (itemCount > 0) { // Если товар есть у игрока
                    // Уменьшаем количество товара на 1 в базе данных
                    databaseHelper.decreaseItemCount(3, getCurrentUsername());
                    startgameButton.setVisibility(View.INVISIBLE); // Скрываем кнопку "Start Game"
                    bossDefeated();

                } else {
                    // Если товара нет у игрока, вы можете вывести сообщение об этом
                    Toast.makeText(MainActivity.this, "У вас нет такого товара", Toast.LENGTH_SHORT).show();
                }
            }
        });
        bossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bossHealths[currentBossIndex] -= attackDamage;
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
        String username = getCurrentUsername();
        shopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShopActivity.class);
                intent.putExtra("username", username); // Добавляем имя пользователя в Intent
                startActivity(intent);
            }
        });
        selectlevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LevelsActivity.class);
                intent.putExtra("username", username); // Добавляем имя пользователя в Intent
                startActivity(intent);
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
        //int level = databaseHelper.getUserLevel(getCurrentUsername());
        //updateLevelText(level); // Вызываем с аргументом
    }

    private String getCurrentUsername() {
        // Получаем имя пользователя из Intent, переданного из LoginActivity
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        System.out.println("Received username: " + username);
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

        int bossInitialHealth = calculateBossHealth(currentBossIndex); // Изначальное здоровье босса

        crystalCount = databaseHelper.getCrystalCount(getCurrentUsername());

        crystalCount += bossInitialHealth; // Добавляем кристаллы, равные изначальному здоровью босса

        // Сохраняем обновленное количество кристаллов в базе данных
        databaseHelper.updateCrystalCount(getCurrentUsername(), crystalCount);

        // Обновляем отображение количества кристаллов на экране
        updateCrystalCountText();
    }


    private void updateCrystalCountText() {
        crystalCountTextView.setText("Кристаллы: " + databaseHelper.getCrystalCount(getCurrentUsername()));
    }

    private void startBossTimer() {
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
        attackDamage = defaultattackDamage;

        bossButton.setVisibility(View.VISIBLE);
        bossButton.setEnabled(false); // Кнопка "Босс" неактивна до начала игры
        startgameButton.setVisibility(View.VISIBLE);
        stopBossTimer();
        currentBossIndex++;
        bossTimerDuration = (currentBossIndex + 1) * 5 * 1000;

        if (currentBossIndex < bossHealths.length) {
            bossHealths[currentBossIndex] = calculateBossHealth(currentBossIndex);
            updateHealthText();
            defeatedTextView.setVisibility(View.INVISIBLE);
            nextBossButton.setVisibility(View.INVISIBLE);
            int level = currentBossIndex + 1;
            updateLevelText(level); // Обновляем уровень на главном экране
            // Обновляем уровень игрока в базе данных
            int userlevel=databaseHelper.getUserLevel(getCurrentUsername());
            if (userlevel<level) {
                databaseHelper.incrementUserLevel(getCurrentUsername());
            }
        } else {
            Toast.makeText(this, "Поздравляем! Вы победили всех боссов!", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_final);
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

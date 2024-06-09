package com.example.clicker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ShopActivity extends AppCompatActivity {

    private com.example.clicker.DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        databaseHelper = new com.example.clicker.DatabaseHelper(this);

        ImageButton buyButton1 = findViewById(R.id.buy_button1);
        ImageButton buyButton2 = findViewById(R.id.buy_button2);
        ImageButton buyButton3 = findViewById(R.id.buy_button3);
        Button homeButton = findViewById(R.id.home_button);

        buyButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyItem(1);
            }
        });

        buyButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyItem(2);
            }
        });

        buyButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyItem(3);
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity(); // Закрываем текущую активность и возвращаемся на главный экран
            }
        });
    }

    private String getCurrentUsername() {
        // Получаем имя пользователя из Intent, переданного из LoginActivity
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        return username != null ? username : ""; // Возвращаем имя пользователя или пустую строку, если имя не найдено
    }

    private void buyItem(int itemId) {
        boolean success = databaseHelper.buyItem(itemId, getCurrentUsername());
        if (success) {
            // Обновляем видимость кнопки товара на главном экране
            Toast.makeText(this, "Товар успешно куплен", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Недостаточно средств", Toast.LENGTH_SHORT).show();
        }
    }

    private void openMainActivity() {
        Intent intent = new Intent(ShopActivity.this, MainActivity.class);
        intent.putExtra("username", getCurrentUsername()); // Добавляем имя пользователя в Intent
        System.out.println("Username was gone from Shop to Main: " + getCurrentUsername());
        startActivity(intent);
        finish();
    }
}

package com.example.clicker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton, registerButton;
    private com.example.clicker.DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Инициализация объекта DatabaseHelper
        databaseHelper = new com.example.clicker.DatabaseHelper(this);

        // Находим элементы интерфейса
        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);

        // Настраиваем обработчики нажатия кнопок
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ваша логика проверки правильности введенных данных
                // Это пример, вам нужно использовать вашу реализацию проверки

                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Проверяем правильность введенных данных с помощью DatabaseHelper
                boolean isValid = databaseHelper.checkUser(username, password);

                if (isValid) {
                    // Переходим на главный экран, если введенные данные правильные
                    Intent intent = new Intent(LoginActivity.this, com.example.clicker.MainActivity.class);
                    intent.putExtra("username", username); // Добавляем имя пользователя в Intent
                    startActivity(intent);
                    finish(); // Закрываем активность входа
                } else {
                    // Выводим сообщение об ошибке, если введенные данные неправильные
                    Toast.makeText(LoginActivity.this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ваша логика регистрации пользователя
                // Это пример, вам нужно использовать вашу реализацию регистрации

                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Регистрируем пользователя с помощью DatabaseHelper
                boolean isRegistered = databaseHelper.addUser(username, password);

                if (isRegistered) {
                    // Выводим сообщение об успешной регистрации, если успешно
                    Toast.makeText(LoginActivity.this, "Пользователь зарегистрирован", Toast.LENGTH_SHORT).show();

                    // Переходим на главный экран, после успешной регистрации
                    Intent intent = new Intent(LoginActivity.this, com.example.clicker.MainActivity.class);
                    intent.putExtra("username", username); // Добавляем имя пользователя в Intent

                    startActivity(intent);
                    finish(); // Закрываем активность входа
                } else {
                    // Выводим сообщение об ошибке, если пользователь уже существует
                    Toast.makeText(LoginActivity.this, "Пользователь уже существует", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

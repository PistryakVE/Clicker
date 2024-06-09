package com.example.clicker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TUSURCLICKER.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_LEVEL = "level";
    private static final String COLUMN_CRYSTAL_COUNT = "crystal_count";
    private static final String COLUMN_ITEM1_COUNT = "item1_count";
    private static final String COLUMN_ITEM2_COUNT = "item2_count";
    private static final String COLUMN_ITEM3_COUNT = "item3_count";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTableQuery = "CREATE TABLE " + TABLE_USERS + "(" +
                COLUMN_USERNAME + " TEXT PRIMARY KEY," +
                COLUMN_PASSWORD + " TEXT," +
                COLUMN_LEVEL + " INTEGER DEFAULT 1," +
                COLUMN_CRYSTAL_COUNT + " INTEGER DEFAULT 0," +
                COLUMN_ITEM1_COUNT + " INTEGER DEFAULT 0," +
                COLUMN_ITEM2_COUNT + " INTEGER DEFAULT 0," +
                COLUMN_ITEM3_COUNT + " INTEGER DEFAULT 0)";
        db.execSQL(createUserTableQuery);
    }

    // Метод для обновления количества ресурсов у пользователя
    public void updateCrystalCount(String username, int crystalCount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CRYSTAL_COUNT, crystalCount);
        db.update(TABLE_USERS, values, COLUMN_USERNAME + " = ?", new String[]{username});
    }

    // Добавляем новый метод для получения количества кристаллов пользователя
    public int getCrystalCount(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_CRYSTAL_COUNT + " FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USERNAME + " = ?", new String[]{username});
        int crystalCount = 0;
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(COLUMN_CRYSTAL_COUNT);
            if (columnIndex != -1) {
                crystalCount = cursor.getInt(columnIndex);
            }
            cursor.close();
        }
        return crystalCount;
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?", new String[]{username, password});
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public int getUserLevel(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_LEVEL + " FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USERNAME + " = ?", new String[]{username});
        int level = 1;
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(COLUMN_LEVEL);
            if (columnIndex != -1) {
                level = cursor.getInt(columnIndex);
            } else {
                // Handle case when COLUMN_LEVEL is not found
            }
            cursor.close();
        }
        return level;
    }


    public void incrementUserLevel(String username) {
        int currentLevel = getUserLevel(username);
        int newLevel = currentLevel + 1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LEVEL, newLevel);
        db.update(TABLE_USERS, values, COLUMN_USERNAME + " = ?", new String[]{username});
    }




    // Добавляем методы для обновления количества купленных товаров и проверки их доступности
    public boolean buyItem(int itemId, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        int itemCost;//Цена товара
        String itemColumn;//Столбец в таблице
        System.out.println("Зашли в базу");

        // Определяем стоимость товара и столбец в базе данных
        switch (itemId) {
            case 1:
                itemCost = 100;
                itemColumn = COLUMN_ITEM1_COUNT;
                break;
            case 2:
                itemCost = 100;
                itemColumn = COLUMN_ITEM2_COUNT;
                break;
            case 3:
                itemCost = 500;
                itemColumn = COLUMN_ITEM3_COUNT;
                break;
            default:
                return false;
        }

        // Получаем текущее количество кристаллов
        int currentCrystalCount = getCrystalCount(username);
        System.out.println("Количество ресурсов: " + currentCrystalCount);

        // Проверяем, достаточно ли кристаллов для покупки
        if (currentCrystalCount >= itemCost) {
            System.out.println("Достаточно для покупки");

            ContentValues values = new ContentValues();
            values.put(COLUMN_CRYSTAL_COUNT, currentCrystalCount - itemCost); // Уменьшаем кристаллы
            values.put(itemColumn, getItemCount(itemId, username) + 1); // Увеличиваем количество товара
            System.out.println("Обновляем данные базы: " + currentCrystalCount);

            // Обновляем базу данных
            int affectedRows = db.update(TABLE_USERS, values, COLUMN_USERNAME + " = ?", new String[]{username});
            return affectedRows > 0;
        } else {
            System.out.println("Недостаточно кристаллов для покупки: " + currentCrystalCount);

            return false; // Недостаточно кристаллов для покупки
        }
    }

    public int getItemCount(int itemId, String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String column;
        switch (itemId) {
            case 1:
                column = COLUMN_ITEM1_COUNT;
                break;
            case 2:
                column = COLUMN_ITEM2_COUNT;
                break;
            case 3:
                column = COLUMN_ITEM3_COUNT;
                break;
            default:
                return 0;
        }
        Cursor cursor = db.rawQuery("SELECT " + column + " FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USERNAME + " = ?", new String[]{username});
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(column);
            if (columnIndex != -1) {
                count = cursor.getInt(columnIndex);
            }
            cursor.close();
        }
        return count;
    }
    public boolean decreaseItemCount(int itemId, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        String itemColumn;//Столбец в таблице

        // Определяем столбец в базе данных
        switch (itemId) {
            case 1:
                itemColumn = COLUMN_ITEM1_COUNT;
                break;
            case 2:
                itemColumn = COLUMN_ITEM2_COUNT;
                break;
            case 3:
                itemColumn = COLUMN_ITEM3_COUNT;
                break;
            default:
                return false;
        }

        // Получаем текущее количество товаров
        int currentitemCount = getItemCount(itemId,username);

        ContentValues values = new ContentValues();
        values.put(itemColumn, currentitemCount - 1); // Уменьшаем кол-во товара
        // Обновляем базу данных
        int affectedRows = db.update(TABLE_USERS, values, COLUMN_USERNAME + " = ?", new String[]{username});
        return affectedRows > 0;

    }

}
package com.dji.gpscoordinates;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

/**
 * Активность для ввода GPS координат
 * Работает без DJI SDK (для автономного использования)
 */
public class GPSCoordinatesActivity extends Activity {
    
    private EditText latitudeInput;
    private EditText longitudeInput;
    private EditText nameInput;
    private Button applyButton;
    private Button saveButton;
    private Button cancelButton;
    
    private static final String PREFS_NAME = "GPS_COORDINATES";
    private static final String KEY_POINTS_COUNT = "points_count";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_coordinates);
        
        // Инициализация UI
        latitudeInput = findViewById(R.id.latitude_input);
        longitudeInput = findViewById(R.id.longitude_input);
        nameInput = findViewById(R.id.name_input);
        
        applyButton = findViewById(R.id.apply_button);
        saveButton = findViewById(R.id.save_button);
        cancelButton = findViewById(R.id.cancel_button);
        
        // Обработчики кнопок
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyCoordinates();
            }
        });
        
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePoint();
            }
        });
        
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    /**
     * Применение координат
     */
    private void applyCoordinates() {
        try {
            String latStr = latitudeInput.getText().toString().trim();
            String lonStr = longitudeInput.getText().toString().trim();
            
            if (latStr.isEmpty() || lonStr.isEmpty()) {
                showError("Введите координаты!");
                return;
            }
            
            double lat = Double.parseDouble(latStr);
            double lon = Double.parseDouble(lonStr);
            
            // Проверка валидности
            if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
                showError("Неверные координаты!\nШирота: -90 до 90\nДолгота: -180 до 180");
                return;
            }
            
            // Сохраняем последние примененные координаты
            saveLastCoordinates(lat, lon);
            
            // Отправка координат (если дрон подключен)
            sendCoordinatesToDrone(lat, lon, 0.0);
            
            showSuccess("Координаты применены!\nШирота: " + lat + "\nДолгота: " + lon);
            
        } catch (NumberFormatException e) {
            showError("Введите корректные числа!");
        }
    }
    
    /**
     * Сохранение точки
     */
    private void savePoint() {
        try {
            String latStr = latitudeInput.getText().toString().trim();
            String lonStr = longitudeInput.getText().toString().trim();
            String name = nameInput.getText().toString().trim();
            
            if (latStr.isEmpty() || lonStr.isEmpty()) {
                showError("Введите координаты!");
                return;
            }
            
            if (name.isEmpty()) {
                showError("Введите название точки!");
                return;
            }
            
            double lat = Double.parseDouble(latStr);
            double lon = Double.parseDouble(lonStr);
            
            // Проверка валидности
            if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
                showError("Неверные координаты!");
                return;
            }
            
            // Сохранение в SharedPreferences
            savePointToStorage(name, lat, lon, 0.0);
            
            showSuccess("Точка сохранена: " + name);
            
            // Очищаем поле названия для следующей точки
            nameInput.setText("");
            
        } catch (NumberFormatException e) {
            showError("Введите корректные координаты!");
        }
    }
    
    /**
     * Отправка координат на дрон
     * В реальной версии здесь будет код для отправки через DJI SDK
     */
    private void sendCoordinatesToDrone(double lat, double lon, double alt) {
        // TODO: Интеграция с DJI SDK
        // Здесь будет код отправки через Data Transparent Transmission
        
        // Пока просто логируем
        android.util.Log.d("GPSCoordinates", String.format(
            "Отправка координат: Lat=%.6f, Lon=%.6f, Alt=%.2f", lat, lon, alt));
    }
    
    /**
     * Сохранение последних примененных координат
     */
    private void saveLastCoordinates(double lat, double lon) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("last_lat", (float)lat);
        editor.putFloat("last_lon", (float)lon);
        editor.apply();
    }
    
    /**
     * Сохранение точки в хранилище
     */
    private void savePointToStorage(String name, double lat, double lon, double alt) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int count = prefs.getInt(KEY_POINTS_COUNT, 0);
        
        if (count >= 50) {
            showError("Достигнут лимит точек (50)!");
            return;
        }
        
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("point_" + count + "_name", name);
        editor.putFloat("point_" + count + "_lat", (float)lat);
        editor.putFloat("point_" + count + "_lon", (float)lon);
        editor.putFloat("point_" + count + "_alt", (float)alt);
        editor.putInt(KEY_POINTS_COUNT, count + 1);
        editor.apply();
    }
    
    private void showError(String message) {
        Toast.makeText(this, "❌ " + message, Toast.LENGTH_LONG).show();
    }
    
    private void showSuccess(String message) {
        Toast.makeText(this, "✅ " + message, Toast.LENGTH_SHORT).show();
    }
}


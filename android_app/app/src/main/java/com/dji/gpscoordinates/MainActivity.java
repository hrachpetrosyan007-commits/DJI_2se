package com.dji.gpscoordinates;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;

public class MainActivity extends AppCompatActivity {
    
    private static final String PREFS_NAME = "GPS_COORDINATES";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button openGPSButton = findViewById(R.id.open_gps_button);
        TextView lastCoordsText = findViewById(R.id.last_coords_text);
        
        // Показываем последние координаты
        showLastCoordinates(lastCoordsText);
        
        openGPSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GPSCoordinatesActivity.class);
                startActivity(intent);
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        TextView lastCoordsText = findViewById(R.id.last_coords_text);
        showLastCoordinates(lastCoordsText);
    }
    
    private void showLastCoordinates(TextView textView) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        float lat = prefs.getFloat("last_lat", 0);
        float lon = prefs.getFloat("last_lon", 0);
        
        if (lat != 0 || lon != 0) {
            textView.setText(String.format("Последние координаты:\n%.6f, %.6f", lat, lon));
        } else {
            textView.setText("Координаты еще не введены");
        }
    }
}


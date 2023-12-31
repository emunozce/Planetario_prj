package com.emc.planetario;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class EleccionActivity extends AppCompatActivity {
    private ImageView toggleMusic;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.segunda);

        Button planetario = findViewById(R.id.planetario);
        planetario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EleccionActivity.this, PlanetarioActivity.class);
                startActivity(intent);
            }
        });
        Button preguntas = findViewById(R.id.preguntas);
        preguntas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EleccionActivity.this, PreguntasActivity.class);
                startActivity(intent);
            }
        });
    }
}


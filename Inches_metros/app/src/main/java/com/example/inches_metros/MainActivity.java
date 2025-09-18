package com.example.inches_metros;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText inches;
    private Button calcular;
    private TextView metrosText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inches =  findViewById(R.id.inches);
        calcular = findViewById(R.id.calcular);
        metrosText = findViewById(R.id.metros);

        calcular.setOnClickListener(v->calcularMetros());

    }
    public void calcularMetros(){
        double emaitza = (double) (Double.parseDouble(String.valueOf(inches.getText())) * 0.0254);
        metrosText.setText(inches.getText() + " pulgadas son " + emaitza +" metros");
    }
}
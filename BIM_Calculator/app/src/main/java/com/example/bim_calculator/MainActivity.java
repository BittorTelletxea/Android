package com.example.bim_calculator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private EditText ageInput, feetInput, inchesInput, heightInput;
    private TextView resultInput;
    private RadioButton male, female;
    private String sexua;
    private Button btnCalcular;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ageInput =  findViewById(R.id.age);
        feetInput = findViewById(R.id.feet);
        inchesInput = findViewById(R.id.inches);
        heightInput = findViewById(R.id.heiht);
        heightInput = findViewById(R.id.heiht);
        resultInput = findViewById(R.id.result);
        btnCalcular = findViewById(R.id.calcular);

        male = findViewById(R.id.male);
        female = findViewById(R.id.female);

        btnCalcular.setOnClickListener(v -> {
            try {
                int edad = Integer.parseInt(ageInput.getText().toString());
                int pies = Integer.parseInt(feetInput.getText().toString());
                int pulgadas = Integer.parseInt(inchesInput.getText().toString());
                float pesoKg = Float.parseFloat(heightInput.getText().toString());

                int totalPulgadas = (pies * 12) + pulgadas;
                double alturaMetros = totalPulgadas * 0.0254;

                double bim = pesoKg / (alturaMetros * alturaMetros);

                if(male.isChecked()){
                    sexua = "Gizona";
                } else if (female.isChecked()) {
                    sexua = "Emakumea";
                }
                resultInput.setText("\n \n Sexua: " + sexua + "\n \n Adina: " + edad +"\n \n Zure BIM da: " + String.format("%.2f", bim) );

            } catch (Exception e) {
                resultInput.setText("Datu guztiak jarri");
            }



        });
    }
}
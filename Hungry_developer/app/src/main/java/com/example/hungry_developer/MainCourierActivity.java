package com.example.hungry_developer;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainCourierActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_courier_activity);

        ListView lista = findViewById(R.id.mainCourier);
        Plato[] platos = {
                new Plato("Cheese and orange muffins","Cheese and orange muffins",10),
                new Plato("Tofu and cheese burgers","Tofu and cheese burgers",9),
                new Plato("Anchovy and ezekiel salad","Anchovy and ezekiel salad",8),
                new Plato("Radish and basil salad","Radish and basil salad",11),
                new Plato("Caraway and pork stir fry","Caraway and pork stir fry",13)

        };


        ArrayAdapter<Plato>platosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,platos);
        lista.setAdapter(platosAdapter);

    }
}
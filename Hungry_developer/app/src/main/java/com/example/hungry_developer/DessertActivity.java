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

public class DessertActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dessert_activity);

        ListView lista = findViewById(R.id.desset_list);
        Plato[] platos = {
                new Plato("Tumeric and venison korma","Tumeric and venison korma",10),
                new Plato("Stilton and squash wontons","Stilton and squash wontons",9),
                new Plato("Egusi and artichoke soup","Egusi and artichoke soup",8),
                new Plato("Chilli and rosemary stir fry","Chilli and rosemary stir fry",11),
                new Plato("Red cabbage and sprout salad","Red cabbage and sprout salad",13)

        };

        ArrayAdapter<Plato>platosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,platos);
        lista.setAdapter(platosAdapter);

    }
}
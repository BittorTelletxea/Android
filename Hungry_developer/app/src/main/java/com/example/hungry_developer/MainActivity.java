package com.example.hungry_developer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private CardView starter_Layout, main_layout, dessert_layout;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        starter_Layout = findViewById(R.id.starters);
        main_layout = findViewById(R.id.main_card);
        dessert_layout = findViewById(R.id.dessert_card);

        starter_Layout.setOnClickListener(v -> starter_void());
        main_layout.setOnClickListener(v-> main_void());
        dessert_layout.setOnClickListener(v->dessert_void());

    }
    public void starter_void(){

        Intent starter_intent = new Intent(MainActivity.this, StarterActivity.class);
        startActivity(starter_intent);
    }
    public void main_void(){
        Intent main_intent = new Intent(MainActivity.this, MainCourierActivity.class);
        startActivity(main_intent);
    }
    public void dessert_void(){
        Intent dessert_intent = new Intent(MainActivity.this, DessertActivity.class);
        startActivity(dessert_intent);
    }
}
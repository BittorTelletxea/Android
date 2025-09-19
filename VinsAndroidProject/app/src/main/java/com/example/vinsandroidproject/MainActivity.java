package com.example.vinsandroidproject;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private CardView card;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        card = findViewById(R.id.card);

        Project[] projects = {
                new Project("The App Title", "This will be our description of the project", 5),
                new Project("The App Title2", "This will be our description of the project2", 5),
                new Project("The App Title3", "This will be our description of the project3", 5),
                new Project("The App Title4", "This will be our description of the project4", 5),

        };


    }
}
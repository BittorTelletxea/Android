package com.example.flagguesser

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale



class RankingActivity : AppCompatActivity() {
    private lateinit var playBtn: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ranking_activity)

        playBtn = findViewById(R.id.btn_jugar)

        playBtn.setOnClickListener { cambiarPlay() }

    }
    private fun cambiarPlay(){
        val intentPlay: Intent = Intent(this@RankingActivity, MainActivity::class.java)
        startActivity(intentPlay)
    }
}


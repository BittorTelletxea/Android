package com.example.flagguesser

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text


class RankingActivity : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance();
    private lateinit var username: TextView
    private lateinit var listaRecordNombre: ListView
    private lateinit var listaRecordRacha: ListView
    private lateinit var listaRecordPais: ListView

    private lateinit var playBtn: ImageView
    val paisCodigo = Locale.getDefault().country


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ranking_activity)

        playBtn = findViewById(R.id.btn_jugar)
        username = findViewById(R.id.username)
        listaRecordNombre = findViewById(R.id.listRachasNombre)
        listaRecordRacha = findViewById(R.id.listRachasRacha)
        listaRecordPais = findViewById(R.id.listRachasPais)


        val banderaResId = when (paisCodigo.uppercase()) {
            "ES" -> R.drawable.es
            "MX" -> R.drawable.mx
            "JP" -> R.drawable.jp
            "US" -> R.drawable.us
            "FR" -> R.drawable.fr
            else -> R.drawable.bw
        }
        val imgUserFlag = findViewById<ImageView>(R.id.usuariobandera)
        imgUserFlag.setImageResource(banderaResId)

        playBtn.setOnClickListener { cambiarPlay() }

        añadirDatos()
        añadirRecords()




    }
    private fun añadirDatos(){
        val nombre = db.collection("users").document("1").get()
        nombre.addOnSuccessListener { document ->
            if(document.exists()){
                val nombreUser = document.get("nombre").toString()
                username.text = "${nombreUser}"

            }
        }
    }
    private fun añadirRecords(){
        db.collection("users")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val listaStringsNombre = ArrayList<String>()
                val listaStringsRacha = ArrayList<String>()
                val listaStringsPais = ArrayList<String>()


                for (document in querySnapshot.documents) {
                    val nombre = document.getString("nombre") ?: "Sin nombre"
                    val pais = document.getString("pais") ?: "Desconocido"
                    val racha = (document.get("racha") as? Number)?.toInt() ?: 0

                    // Creamos un texto simple para mostrar en la lista
                    val nombreRacha = "$nombre"
                    val rachaUser = "$racha"
                    val paisUser = "$pais"
                    listaStringsNombre.add(nombreRacha)
                    listaStringsRacha.add(rachaUser)
                    listaStringsPais.add(paisUser)

                }

                // Mostramos directamente en el ListView
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaStringsNombre)
                val adapter2 = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaStringsRacha)
                val adapter3 = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaStringsPais)

                listaRecordNombre.adapter = adapter
                listaRecordRacha.adapter = adapter2
                listaRecordPais.adapter = adapter3
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener usuarios: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cambiarPlay(){
        val intentPlay: Intent = Intent(this@RankingActivity, MainActivity::class.java)
        startActivity(intentPlay)
    }
}


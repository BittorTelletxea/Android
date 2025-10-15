package com.example.flagguesser

import android.content.Intent
import android.os.Bundle
import android.view.View
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
    private var userRegistrado: String? = null
    private lateinit var logutImg: ImageView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ranking_activity)

        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userRegistrado = prefs.getString("userRegistrado", null)

        playBtn = findViewById(R.id.btn_jugar)
        username = findViewById(R.id.username)
        listaRecordNombre = findViewById(R.id.listRachasNombre)
        listaRecordRacha = findViewById(R.id.listRachasRacha)
        listaRecordPais = findViewById(R.id.listRachasPais)
        logutImg = findViewById(R.id.logoutImage)

        if (userRegistrado != null) {
            username.text = userRegistrado
            logutImg.visibility = View.VISIBLE

        } else {
            username.text = "Saioa Hasi"
            logutImg.visibility = View.GONE
        }

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

        logutImg.setOnClickListener { logout() }
        username.setOnClickListener { hasiSaioa() }

        playBtn.setOnClickListener { cambiarPlay() }

        añadirRecords()
    }
    private fun logout(){
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        prefs.edit().remove("userRegistrado").apply()
        userRegistrado = null
        username.text = "Saioa Hasi"
        logutImg.visibility = View.GONE;

    }
    private fun hasiSaioa() {
        var izenaAurkitua = false;
        val input = EditText(this)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Saioa Hasi")
            .setView(input)
            .setPositiveButton("Onartu") { _, _ ->
                val nuevoNombre = input.text.toString().trim()
                if (nuevoNombre.isNotEmpty()) {
                    db.collection("users").get().addOnSuccessListener { documentSnapshots ->
                        if (!documentSnapshots.isEmpty) {
                            for (document in documentSnapshots.documents) {
                                if (document.getString("nombre").equals(nuevoNombre.toLowerCase(
                                        Locale.ROOT))) {
                                    username.text = nuevoNombre
                                    userRegistrado = nuevoNombre
                                    val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                                    prefs.edit().putString("userRegistrado", userRegistrado).apply()
                                    logutImg.visibility = View.VISIBLE;

                                    izenaAurkitua = true
                                }
                            }
                            if (!izenaAurkitua) {
                                val usuarioRegistered = hashMapOf(
                                    "nombre" to nuevoNombre.toLowerCase(Locale.ROOT),
                                    "pais" to paisCodigo,
                                    "racha" to 0
                                )

                                userRegistrado = nuevoNombre;
                                username.text = nuevoNombre
                                db.collection("users").add(usuarioRegistered).addOnSuccessListener {
                                    Toast.makeText(
                                        this, "Erabiltzaile " + nuevoNombre + " erregistratua",
                                        Toast.LENGTH_SHORT
                                    )
                                }
                            }

                        }
                    }
                    Toast.makeText(this, "Saioa hasi da", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Ez duzu izenik jarri", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Ezeztatu") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()

        dialog.show()
    }


    private fun añadirRecords() {
        db.collection("users")
            .orderBy("racha", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val listaStringsNombre = ArrayList<String>()
                val listaStringsRacha = ArrayList<String>()
                val listaStringsPais = ArrayList<String>()

                var posicion = 1

                for (document in querySnapshot.documents) {
                    val rachaUser = (document.get("racha") as? Number)?.toInt() ?: 0

                    if(rachaUser != 0) {
                        val nombre = document.getString("nombre") ?: "Sin nombre"
                        val pais = document.getString("pais") ?: "Desconocido"
                        val racha = (document.get("racha") as? Number)?.toInt() ?: 0

                        listaStringsNombre.add("$posicion. $nombre")
                        listaStringsPais.add(pais)
                        listaStringsRacha.add(racha.toString())

                        posicion++
                    }
                }

                val adapterNombre = ArrayAdapter(this, R.layout.item_lista, listaStringsNombre)
                val adapterPais = ArrayAdapter(this, R.layout.item_lista, listaStringsPais)
                val adapterRacha = ArrayAdapter(this, R.layout.item_lista, listaStringsRacha)

                listaRecordNombre.adapter = adapterNombre
                listaRecordPais.adapter = adapterPais
                listaRecordRacha.adapter = adapterRacha
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


package com.example.flagguesser

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
    private lateinit var topGlText: TextView
    private lateinit var topPsText: TextView
    private lateinit var tvPosicionUsuario: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ranking_activity)

        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        userRegistrado = prefs.getString("userRegistrado", null)

        playBtn = findViewById(R.id.btn_jugar)
        username = findViewById(R.id.username)
        listaRecordNombre = findViewById(R.id.listRachasNombre)
        listaRecordRacha = findViewById(R.id.listRachasRacha)
        listaRecordPais = findViewById(R.id.listRachasPais)
        logutImg = findViewById(R.id.logoutImage)
        topGlText = findViewById(R.id.topGlText)
        topPsText = findViewById(R.id.topPsText)
        tvPosicionUsuario = findViewById(R.id.tvPosicionUsuario)

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
        topPsText.setOnClickListener { añadirRecordsNacional() }
        topGlText.setOnClickListener { añadirRecords() }


    }
    private fun logout(){
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        prefs.edit().remove("userRegistrado").apply()
        userRegistrado = null
        username.text = "Saioa Hasi"
        logutImg.visibility = View.GONE;

    }
    private fun hasiSaioa() {
        val rankingIntent: Intent = Intent(this@RankingActivity, LogInActivity::class.java)
        startActivity(rankingIntent)
        overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_down)
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
                    val nombre = document.getString("nombre") ?: "Sin nombre"
                    val pais = document.getString("pais") ?: "Desconocido"
                    val racha = (document.get("racha") as? Number)?.toInt() ?: 0

                    listaStringsNombre.add("$posicion. $nombre")
                    listaStringsPais.add(pais)
                    listaStringsRacha.add(racha.toString())


                    posicion++


                }

                val adapterNombre = ArrayAdapter(this, R.layout.item_lista, listaStringsNombre)
                val adapterPais = ArrayAdapter(this, R.layout.item_lista, listaStringsPais)
                val adapterRacha = ArrayAdapter(this, R.layout.item_lista, listaStringsRacha)

                listaRecordNombre.adapter = adapterNombre
                listaRecordPais.adapter = adapterPais
                listaRecordRacha.adapter = adapterRacha

                topGlText.setTextColor(getColor(R.color.black))
                topPsText.setTextColor(getColor(R.color.blanco_suave))
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener usuarios: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        var puesto = 0
        db.collection("users")
            .orderBy("racha", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                for(document in queryDocumentSnapshots.documents){
                        puesto++
                        if (document.getString("nombre").equals(userRegistrado)) {
                            tvPosicionUsuario.text = "Zure postua: ${puesto}"
                            break
                    }
                }
            }

    }
    private fun añadirRecordsNacional() {
        db.collection("users")
            .orderBy("racha", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val listaStringsNombre = ArrayList<String>()
                val listaStringsRacha = ArrayList<String>()
                val listaStringsPais = ArrayList<String>()

                var posicion = 1

                for (document in querySnapshot.documents) {
                    if(document.getString("pais").equals(paisCodigo)) {
                        val rachaUser = (document.get("racha") as? Number)?.toInt() ?: 0

                        if (rachaUser != 0) {
                            val nombre = document.getString("nombre") ?: "Sin nombre"
                            val pais = document.getString("pais") ?: "Desconocido"
                            val racha = (document.get("racha") as? Number)?.toInt() ?: 0

                            listaStringsNombre.add("$posicion. $nombre")
                            listaStringsPais.add(pais)
                            listaStringsRacha.add(racha.toString())

                            posicion++


                        }
                    }
                }

                val adapterNombre = ArrayAdapter(this, R.layout.item_lista, listaStringsNombre)
                val adapterPais = ArrayAdapter(this, R.layout.item_lista, listaStringsPais)
                val adapterRacha = ArrayAdapter(this, R.layout.item_lista, listaStringsRacha)

                listaRecordNombre.adapter = adapterNombre
                listaRecordPais.adapter = adapterPais
                listaRecordRacha.adapter = adapterRacha

                topGlText.setTextColor(getColor(R.color.blanco_suave))
                topPsText.setTextColor(getColor(R.color.black))
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener usuarios: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        var puesto = 0
        db.collection("users")
            .orderBy("racha", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                for(document in queryDocumentSnapshots.documents){
                    if(document.getString("pais").equals(paisCodigo)) {

                        puesto++
                        if (document.getString("nombre").equals(userRegistrado)) {
                            tvPosicionUsuario.text = "Zure postua: ${puesto}"
                            break
                        }
                    }
                }
            }
    }


    private fun cambiarPlay(){
        val intentPlay: Intent = Intent(this@RankingActivity, MainActivity::class.java)
        startActivity(intentPlay)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right)

    }
}


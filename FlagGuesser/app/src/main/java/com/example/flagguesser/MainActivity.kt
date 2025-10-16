package com.example.flagguesser

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import java.util.Locale
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class MainActivity : AppCompatActivity() {

    private lateinit var imageBandera: ImageView
    private lateinit var btnOpcion1: Button
    private lateinit var btnOpcion2: Button
    private lateinit var btnOpcion3: Button
    private lateinit var rankingBtn: ImageView
    private val paisCodigo = Locale.getDefault().country

    private val db = FirebaseFirestore.getInstance();
    private lateinit var username: TextView
    private var userRegistrado: String? = null
    private lateinit var banderaCorrecta: BanderaClass
    private lateinit var recordRachaText: TextView
    private lateinit var logutImg: ImageView

    val listaBanderas = listOf(
        BanderaClass("Argentina", R.drawable.ar),
        BanderaClass("Alemania", R.drawable.de),
        BanderaClass("Arabia Saudita", R.drawable.sa),
        BanderaClass("Australia", R.drawable.au),
        BanderaClass("Austria", R.drawable.at),
        BanderaClass("Bélgica", R.drawable.be),
        BanderaClass("Bolivia", R.drawable.bo),
        BanderaClass("Brasil", R.drawable.br),
        BanderaClass("Canadá", R.drawable.ca),
        BanderaClass("Chile", R.drawable.cl),
        BanderaClass("China", R.drawable.cn),
        BanderaClass("Colombia", R.drawable.co),
        BanderaClass("Corea del Sur", R.drawable.kr),
        BanderaClass("Costa Rica", R.drawable.cr),
        BanderaClass("Croacia", R.drawable.hr),
        BanderaClass("Cuba", R.drawable.cu),
        BanderaClass("Dinamarca", R.drawable.dk),
        BanderaClass("Ecuador", R.drawable.ec),
        BanderaClass("Egipto", R.drawable.eg),
        BanderaClass("El Salvador", R.drawable.sv),
        BanderaClass("Emiratos Árabes Unidos", R.drawable.ae),
        BanderaClass("Eslovaquia", R.drawable.sk),
        BanderaClass("Eslovenia", R.drawable.si),
        BanderaClass("España", R.drawable.es),
        BanderaClass("Estados Unidos", R.drawable.us),
        BanderaClass("Estonia", R.drawable.ee),
        BanderaClass("Filipinas", R.drawable.ph),
        BanderaClass("Finlandia", R.drawable.fi),
        BanderaClass("Francia", R.drawable.fr),
        BanderaClass("Grecia", R.drawable.gr),
        BanderaClass("Guatemala", R.drawable.gt),
        BanderaClass("Honduras", R.drawable.hn),
        BanderaClass("Hungría", R.drawable.hu),
        BanderaClass("India", R.drawable.in_flag),
        BanderaClass("Indonesia", R.drawable.id),
        BanderaClass("Irlanda", R.drawable.ie),
        BanderaClass("Islandia", R.drawable.is_flag),
        BanderaClass("Israel", R.drawable.il),
        BanderaClass("Italia", R.drawable.it),
        BanderaClass("Japón", R.drawable.jp),
        BanderaClass("México", R.drawable.mx),
        BanderaClass("Marruecos", R.drawable.ma),
        BanderaClass("Noruega", R.drawable.no),
        BanderaClass("Nueva Zelanda", R.drawable.nz),
        BanderaClass("Panamá", R.drawable.pa),
        BanderaClass("Paraguay", R.drawable.py),
        BanderaClass("Perú", R.drawable.pe),
        BanderaClass("Polonia", R.drawable.pl),
        BanderaClass("Portugal", R.drawable.pt),
        BanderaClass("Reino Unido", R.drawable.gb),
        BanderaClass("República Dominicana", R.drawable.do_flag),
        BanderaClass("Rusia", R.drawable.ru),
        BanderaClass("Sudáfrica", R.drawable.za),
        BanderaClass("Suecia", R.drawable.se),
        BanderaClass("Suiza", R.drawable.ch),
        BanderaClass("Turquía", R.drawable.tr),
        BanderaClass("Ucrania", R.drawable.ua),
        BanderaClass("Uruguay", R.drawable.uy),
        BanderaClass("Venezuela", R.drawable.ve)
    )


    private lateinit var tvRacha: TextView
    private var racha: Int = 0 // Contador de racha

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageBandera = findViewById(R.id.bandera)
        btnOpcion1 = findViewById(R.id.opcion1)
        btnOpcion2 = findViewById(R.id.opcion2)
        btnOpcion3 = findViewById(R.id.opcion3)
        tvRacha = findViewById(R.id.tvRacha)
        username = findViewById(R.id.username)
        rankingBtn = findViewById(R.id.btn_ranking)
        recordRachaText = findViewById(R.id.text_view_record)
        logutImg = findViewById(R.id.logoutImage)



        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        userRegistrado = prefs.getString("userRegistrado", null)

        if (userRegistrado != null) {
            username.text = userRegistrado
            logutImg.visibility = View.VISIBLE;

        } else {
            username.text = "Saioa Hasi"
            logutImg.visibility = View.GONE;

        }

        rankingBtn.setOnClickListener { cambiarRanking() }

        username.setOnClickListener { hasiSaioa() }



        //erabiltzailearen herrialdearen bandera jarri
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

        mostrarNuevaBandera()
        aldatuRecord()
    }

    private fun logout(){
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        prefs.edit().remove("userRegistrado").apply()
        userRegistrado = null
        username.text = "Saioa Hasi"
        logutImg.visibility = View.GONE;
        aldatuRecord()

    }

    fun aldatuRecord() {
        var rachaEncontrada = false

        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userRegistrado = prefs.getString("userRegistrado", null)

        db.collection("users").get()
            .addOnSuccessListener { query ->
                if (!query.isEmpty) {
                    for (document in query.documents) {

                        if (!userRegistrado.isNullOrBlank() ) {
                            if (document.getString("nombre").equals(userRegistrado)) {
                                val rachaUser = (document.get("racha") as? Number)?.toInt() ?: 0
                                recordRachaText.text = "$rachaUser"
                                rachaEncontrada = true
                                break
                            }
                        } else {
                            recordRachaText.text = "Saioa hasi"
                        }
                    }
                    if (!rachaEncontrada) {
                        recordRachaText.text = "Saioa hasi"
                    }
                }
            }

    }
    private fun hasiSaioa() {
        val rankingIntent: Intent = Intent(this@MainActivity, LogInActivity::class.java)
        startActivity(rankingIntent)
        overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_down)
    }

    private fun mostrarTop10Popup(posicion: Int) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_top10)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val tvMensaje = dialog.findViewById<TextView>(R.id.tvMensaje)
        val btnAceptar = dialog.findViewById<Button>(R.id.btnAceptar)

        tvMensaje.text = "Zure record-a $posicion. postuan sartu da munduko Top 10ean!"

        btnAceptar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }



    private fun mostrarNuevaBandera() {
        val botones = listOf(btnOpcion1, btnOpcion2, btnOpcion3)

        botones.forEach { boton ->
            boton.setBackgroundColor(getColor(R.color.white))
            boton.isEnabled = true
        }

        banderaCorrecta = listaBanderas.random()

        val opciones = listaBanderas.shuffled().filter { it != banderaCorrecta }.take(2).toMutableList()
        opciones.add(banderaCorrecta)
        opciones.shuffle()

        imageBandera.setImageResource(banderaCorrecta.imagenResId)

        tvRacha.text = "$racha"

        botones.forEachIndexed { index, boton ->
            boton.text = opciones[index].nombre
            boton.setOnClickListener {
                botones.forEach { it.isEnabled = false }

                if (opciones[index] == banderaCorrecta) {
                    boton.setBackgroundColor(getColor(R.color.green))
                    racha++
                    tvRacha.text = "$racha"
                } else {
                    boton.setBackgroundColor(getColor(R.color.red))
                    val correctoIndex = opciones.indexOf(banderaCorrecta)
                    botones[correctoIndex].setBackgroundColor(getColor(R.color.green))

                    if (!userRegistrado.isNullOrBlank()) {
                        val usuario = db.collection("users")
                        usuario.get().addOnSuccessListener { query ->
                            for (document in query.documents) {
                                if (document.getString("nombre") == userRegistrado) {
                                    val rachaUserDB = (document.get("racha") as? Number)?.toInt() ?: 0

                                    if (racha > rachaUserDB) {
                                        db.collection("users").document(document.id).update("racha", racha)
                                            .addOnSuccessListener {
                                                db.collection("users")
                                                    .orderBy("racha", Query.Direction.DESCENDING)
                                                    .limit(10)
                                                    .get()
                                                    .addOnSuccessListener { querys ->
                                                        var posicion = 0;
                                                        for(document in querys.documents){
                                                            posicion++
                                                            if(document.getString("nombre") == userRegistrado){
                                                                mostrarTop10Popup(posicion)
                                                                break
                                                            }
                                                        }
                                                        racha = 0
                                                        tvRacha.text = "$racha"
                                                        aldatuRecord()
                                                    }
                                            }
                                    } else {
                                        racha = 0
                                        tvRacha.text = "$racha"
                                    }
                                    break
                                }
                            }
                        }
                    } else {
                        racha = 0
                        tvRacha.text = "$racha"
                    }
                }


                boton.postDelayed({
                    mostrarNuevaBandera()
                }, 1000)
            }
        }
    }

    private fun cambiarRanking(){
        val rankingIntent: Intent = Intent(this@MainActivity, RankingActivity::class.java)
        startActivity(rankingIntent)

    }


}


package com.example.flagguesser

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.viewbinding.ViewBinding
import com.example.flagguesser.databinding.ActivityMainBinding
import java.util.Locale
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val paisCodigo = Locale.getDefault().country
    private val db = FirebaseFirestore.getInstance()
    private var userRegistrado: String? = null
    private lateinit var banderaCorrecta: BanderaClass
    private val listaBanderas = BanderasData.lista
    private var racha: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        userRegistrado = prefs.getString("userRegistrado", null)

        if (userRegistrado != null) {
            binding.username.text = userRegistrado
            binding.logoutImage.visibility = View.VISIBLE
        } else {
            binding.username.text = "Saioa Hasi"
            binding.logoutImage.visibility = View.GONE
        }

        binding.btnRanking.setOnClickListener { cambiarRanking() }
        binding.username.setOnClickListener { hasiSaioa() }
        binding.logoutImage.setOnClickListener { logout() }

        val banderaResId = when (paisCodigo.uppercase()) {
            "ES" -> R.drawable.es
            "MX" -> R.drawable.mx
            "JP" -> R.drawable.jp
            "US" -> R.drawable.us
            "FR" -> R.drawable.fr
            else -> R.drawable.bw
        }
        binding.usuariobandera.setImageResource(banderaResId)

        mostrarNuevaBandera()
        aldatuRecord()
    }

    private fun logout() {
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        prefs.edit().remove("userRegistrado").apply()
        userRegistrado = null
        binding.username.text = "Saioa Hasi"
        binding.logoutImage.visibility = View.GONE
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
                        if (!userRegistrado.isNullOrBlank()) {
                            if (document.getString("nombre") == userRegistrado) {
                                val rachaUser = (document.get("racha") as? Number)?.toInt() ?: 0
                                binding.textViewRecord.text = "$rachaUser"
                                rachaEncontrada = true
                                break
                            }
                        } else {
                            binding.textViewRecord.text = "Saioa hasi"
                        }
                    }
                    if (!rachaEncontrada) {
                        binding.textViewRecord.text = "Saioa hasi"
                    }
                }
            }
    }

    private fun hasiSaioa() {
        val rankingIntent = Intent(this@MainActivity, LogInActivity::class.java)
        startActivity(rankingIntent)
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down)
    }

    private fun mostrarTop10Popup(posicion: Int) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_top10)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val tvMensaje = dialog.findViewById<TextView>(R.id.tvMensaje)
        val btnAceptar = dialog.findViewById<Button>(R.id.btnAceptar)

        tvMensaje.text = "Zure record-a $posicion. postuan sartu da munduko Top 10ean!"
        btnAceptar.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun mostrarNuevaBandera() {
        val botones = listOf(binding.opcion1, binding.opcion2, binding.opcion3)

        botones.forEach { boton ->
            boton.setBackgroundColor(getColor(R.color.white))
            boton.isEnabled = true
        }

        banderaCorrecta = listaBanderas.random()
        val opciones = listaBanderas.shuffled().filter { it != banderaCorrecta }.take(2).toMutableList()
        opciones.add(banderaCorrecta)
        opciones.shuffle()

        binding.bandera.setImageResource(banderaCorrecta.imagenResId)
        binding.tvRacha.text = "$racha"

        botones.forEachIndexed { index, boton ->
            boton.text = opciones[index].nombre
            boton.setOnClickListener {
                botones.forEach { it.isEnabled = false }

                if (opciones[index] == banderaCorrecta) {
                    boton.setBackgroundColor(getColor(R.color.green))
                    racha++
                    binding.tvRacha.text = "$racha"
                } else {
                    boton.setBackgroundColor(getColor(R.color.red))
                    val correctoIndex = opciones.indexOf(banderaCorrecta)
                    botones[correctoIndex].setBackgroundColor(getColor(R.color.green))

                    if (!userRegistrado.isNullOrBlank()) {
                        db.collection("users").get().addOnSuccessListener { query ->
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
                                                        var posicion = 0
                                                        for (doc in querys.documents) {
                                                            posicion++
                                                            if (doc.getString("nombre") == userRegistrado) {
                                                                mostrarTop10Popup(posicion)
                                                                break
                                                            }
                                                        }
                                                        racha = 0
                                                        binding.tvRacha.text = "$racha"
                                                        aldatuRecord()
                                                    }
                                            }
                                    } else {
                                        racha = 0
                                        binding.tvRacha.text = "$racha"
                                    }
                                    break
                                }
                            }
                        }
                    } else {
                        racha = 0
                        binding.tvRacha.text = "$racha"
                    }
                }

                boton.postDelayed({ mostrarNuevaBandera() }, 1000)
            }
        }
    }

    private fun cambiarRanking() {
        val rankingIntent = Intent(this@MainActivity, RankingActivity::class.java)
        startActivity(rankingIntent)
    }
}

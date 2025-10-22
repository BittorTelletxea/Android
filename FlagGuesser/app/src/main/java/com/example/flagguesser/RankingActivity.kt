package com.example.flagguesser

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.example.flagguesser.databinding.RankingActivityBinding
import java.util.Locale

class RankingActivity : AppCompatActivity() {

    private lateinit var binding: RankingActivityBinding
    private val db = FirebaseFirestore.getInstance()
    private val paisCodigo = Locale.getDefault().country
    private var userRegistrado: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar layout con binding
        binding = RankingActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recuperar usuario registrado
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        userRegistrado = prefs.getString("userRegistrado", null)

        if (userRegistrado != null) {
            binding.username.text = userRegistrado
            binding.logoutImage.visibility = View.VISIBLE
        } else {
            binding.username.text = "Saioa Hasi"
            binding.logoutImage.visibility = View.GONE
        }

        // Bandera del usuario
        val banderaResId = when (paisCodigo.uppercase()) {
            "ES" -> R.drawable.es
            "MX" -> R.drawable.mx
            "JP" -> R.drawable.jp
            "US" -> R.drawable.us
            "FR" -> R.drawable.fr
            else -> R.drawable.bw
        }
        binding.usuariobandera.setImageResource(banderaResId)

        // Listeners
        binding.logoutImage.setOnClickListener { logout() }
        binding.username.setOnClickListener { hasiSaioa() }
        binding.btnJugar.setOnClickListener { cambiarPlay() }
        binding.topPsText.setOnClickListener { añadirRecordsNacional() }
        binding.topGlText.setOnClickListener { añadirRecords() }

        // Cargar records
        añadirRecords()
    }

    private fun logout() {
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        prefs.edit().remove("userRegistrado").apply()
        userRegistrado = null
        binding.username.text = "Saioa Hasi"
        binding.logoutImage.visibility = View.GONE
    }

    private fun hasiSaioa() {
        val intent = Intent(this@RankingActivity, LogInActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down)
    }

    private fun añadirRecords() {
        db.collection("users")
            .orderBy("racha", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val listaNombre = ArrayList<String>()
                val listaRacha = ArrayList<String>()
                val listaPais = ArrayList<String>()
                var posicion = 1

                for (document in querySnapshot.documents) {
                    val nombre = document.getString("nombre") ?: "Sin nombre"
                    val pais = document.getString("pais") ?: "Desconocido"
                    val racha = (document.get("racha") as? Number)?.toInt() ?: 0

                    listaNombre.add("$posicion. $nombre")
                    listaPais.add(pais)
                    listaRacha.add(racha.toString())
                    posicion++
                }

                binding.listRachasNombre.adapter = ArrayAdapter(this, R.layout.item_lista, listaNombre)
                binding.listRachasPais.adapter = ArrayAdapter(this, R.layout.item_lista, listaPais)
                binding.listRachasRacha.adapter = ArrayAdapter(this, R.layout.item_lista, listaRacha)

                binding.topGlText.setTextColor(getColor(R.color.black))
                binding.topPsText.setTextColor(getColor(R.color.blanco_suave))
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener usuarios: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        // Mostrar posición del usuario
        var puesto = 0
        db.collection("users")
            .orderBy("racha", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    puesto++
                    if (document.getString("nombre") == userRegistrado) {
                        binding.tvPosicionUsuario.text = "Zure postua: $puesto"
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
                val listaNombre = ArrayList<String>()
                val listaRacha = ArrayList<String>()
                val listaPais = ArrayList<String>()
                var posicion = 1

                for (document in querySnapshot.documents) {
                    if (document.getString("pais") == paisCodigo) {
                        val rachaUser = (document.get("racha") as? Number)?.toInt() ?: 0
                        if (rachaUser != 0) {
                            val nombre = document.getString("nombre") ?: "Sin nombre"
                            val pais = document.getString("pais") ?: "Desconocido"
                            listaNombre.add("$posicion. $nombre")
                            listaPais.add(pais)
                            listaRacha.add(rachaUser.toString())
                            posicion++
                        }
                    }
                }

                binding.listRachasNombre.adapter = ArrayAdapter(this, R.layout.item_lista, listaNombre)
                binding.listRachasPais.adapter = ArrayAdapter(this, R.layout.item_lista, listaPais)
                binding.listRachasRacha.adapter = ArrayAdapter(this, R.layout.item_lista, listaRacha)

                binding.topGlText.setTextColor(getColor(R.color.blanco_suave))
                binding.topPsText.setTextColor(getColor(R.color.black))
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener usuarios: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        // Posición del usuario a nivel nacional
        var puesto = 0
        db.collection("users")
            .orderBy("racha", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    if (document.getString("pais") == paisCodigo) {
                        puesto++
                        if (document.getString("nombre") == userRegistrado) {
                            binding.tvPosicionUsuario.text = "Zure postua: $puesto"
                            break
                        }
                    }
                }
            }
    }

    private fun cambiarPlay() {
        val intent = Intent(this@RankingActivity, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right)
    }
}

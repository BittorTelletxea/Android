package com.example.flagguesser

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flagguesser.databinding.RegisterActivityBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale
import java.util.Objects.hash

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: RegisterActivityBinding
    private val db = FirebaseFirestore.getInstance()
    private val paisCodigo = Locale.getDefault().country

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RegisterActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener { erregistratu() }
        binding.loginLink.setOnClickListener { loginLink() }
        binding.atzeraBtn.setOnClickListener { atzera() }
    }

    private fun erregistratu() {
        val usuarioText = binding.erabiltzailea.text.toString()
        val contraseñaText = binding.pasahitza.text.toString()
        val contraseñaRepText = binding.pasahitzaRep.text.toString()

        if (usuarioText.isNotEmpty() && contraseñaText.isNotEmpty() && contraseñaRepText.isNotEmpty()) {
            if (contraseñaText == contraseñaRepText) {
                db.collection("users").get().addOnSuccessListener { queryDocumentSnapshots ->
                    var userExist = false
                    for (document in queryDocumentSnapshots.documents) {
                        val usuarioRecorrido = document.getString("nombre")
                        if (usuarioRecorrido.equals(usuarioText, ignoreCase = true)) {
                            userExist = true
                            Toast.makeText(this, "Erabiltzaile hori existitzen da", Toast.LENGTH_SHORT).show()
                            break
                        }
                    }

                    if (!userExist) {
                        val erabiltzaileBerria = hashMapOf(
                            "contraseña" to contraseñaText,
                            "nombre" to usuarioText,
                            "pais" to paisCodigo,
                            "racha" to 0
                        )
                        db.collection("users").add(erabiltzaileBerria)

                        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                        prefs.edit().putString("userRegistrado", usuarioText).apply()

                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                    }
                }
            } else {
                Toast.makeText(this, "Pasahitz berdina jarri", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Datuak jarri", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginLink() {
        val intent = Intent(this@RegisterActivity, LogInActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right)
    }

    private fun atzera() {
        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
    }
}

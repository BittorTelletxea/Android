package com.example.flagguesser

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flagguesser.databinding.LogInActivityBinding
import com.google.firebase.firestore.FirebaseFirestore

class LogInActivity : AppCompatActivity() {

    private lateinit var binding: LogInActivityBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LogInActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener { hasiSaioa() }
        binding.erregitratuLink.setOnClickListener { registerLink() }
        binding.atzeraBtn.setOnClickListener { atzera() }
    }

    private fun hasiSaioa() {
        val usuarioText = binding.username.text.toString()
        val contraseñaText = binding.contraseA.text.toString()

        if (usuarioText.isNotEmpty() && contraseñaText.isNotEmpty()) {
            db.collection("users").get().addOnSuccessListener { queryDocumentSnapshots ->
                if (!queryDocumentSnapshots.isEmpty) {
                    var userExist = false
                    for (document in queryDocumentSnapshots.documents) {
                        val usuarioRecorrido = document.getString("nombre")
                        val contraseñaRecorrido = document.getString("contraseña")

                        if (usuarioRecorrido == usuarioText && contraseñaRecorrido == contraseñaText) {
                            userExist = true
                            val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                            prefs.edit().putString("userRegistrado", usuarioRecorrido).apply()
                            break
                        }
                    }

                    if (userExist) {
                        val intent = Intent(this@LogInActivity, MainActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                    } else {
                        Toast.makeText(this, "Kredentzial okerrak", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Datuak jarri", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerLink() {
        val intent = Intent(this@LogInActivity, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun atzera() {
        val intent = Intent(this@LogInActivity, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
    }
}

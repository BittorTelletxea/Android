package com.example.flagguesser

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Objects.hash

class RegisterActivity: AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance();
    private lateinit var username: EditText
    private lateinit var contraseña: EditText
    private lateinit var contraseñaRep: EditText
    private lateinit var registerBtn: Button
    private lateinit var atzeraBtn: ImageView

    private lateinit var loginLink: TextView
    private lateinit var errorea: TextView

    private val paisCodigo = Locale.getDefault().country


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)

        username = findViewById(R.id.erabiltzailea)
        contraseña = findViewById(R.id.pasahitza)
        contraseñaRep = findViewById(R.id.pasahitzaRep)
        registerBtn = findViewById(R.id.btnRegister)
        loginLink = findViewById(R.id.loginLink)
        errorea = findViewById(R.id.errorea)
        atzeraBtn = findViewById(R.id.atzeraBtn)

        registerBtn.setOnClickListener { erregistratu() }
        loginLink.setOnClickListener { loginLink() }
        atzeraBtn.setOnClickListener { atzera() }
    }
    private fun erregistratu(){

        if(!username.text.isEmpty() || !contraseña.text.isEmpty() || !contraseñaRep.text.isEmpty()){

            if(contraseña.text.toString() == contraseñaRep.text.toString()) {
                db.collection("users").get().addOnSuccessListener { queryDocumentSnapshots ->
                    if (!queryDocumentSnapshots.isEmpty) {
                        var userExist = false
                        for (document in queryDocumentSnapshots.documents) {
                            val usuarioRecorrido = document.getString("nombre")
                            if (usuarioRecorrido?.equals(
                                    username.text.toString(),
                                    ignoreCase = true
                                ) == true
                            ) {
                                userExist = true
                                Toast.makeText(this, "Erabiltzaile hori existitzen da", Toast.LENGTH_SHORT).show()

                                break
                            }
                        }
                        if (!userExist) {
                            val erabiltzaileBerria = hashMapOf(
                                "contraseña" to contraseña.text.toString(),
                                "nombre" to username.text.toString(),
                                "pais" to paisCodigo,
                                "racha" to 0
                            )
                            db.collection("users").add(erabiltzaileBerria)
                            val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                            prefs.edit().putString("userRegistrado", username.text.toString()).apply()
                            val rankingIntent: Intent =
                                Intent(this@RegisterActivity, MainActivity::class.java)
                            startActivity(rankingIntent)
                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)


                        } else {
                            Toast.makeText(this, "Kredentzial okerrak", Toast.LENGTH_SHORT).show()

                        }
                    }
                }
            }else{
                Toast.makeText(this, "Pasahitz berdina jarri", Toast.LENGTH_SHORT).show()

            }
        }else{
            Toast.makeText(this, "Datuak jarri", Toast.LENGTH_SHORT).show()

        }

    }
    private fun loginLink(){
        val intentLogin = Intent(this@RegisterActivity, LogInActivity::class.java)
        startActivity(intentLogin)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right)

    }
    private fun atzera(){
        val intentAtzera = Intent(this@RegisterActivity, MainActivity::class.java)
        startActivity(intentAtzera)
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
    }
}
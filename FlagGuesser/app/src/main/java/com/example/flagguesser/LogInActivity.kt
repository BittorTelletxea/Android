package com.example.flagguesser

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale
import com.google.firebase.firestore.FirebaseFirestore
class LogInActivity: AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance();
    private lateinit var username: EditText
    private lateinit var contraseña: EditText
    private lateinit var loginBtn: Button
    private lateinit var atzeraBtn: ImageView

    private lateinit var erregistratuLink: TextView
    private lateinit var errorea: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_in_activity)

        username = findViewById(R.id.username)
        contraseña = findViewById(R.id.contraseña)
        loginBtn = findViewById(R.id.btnLogin)
        erregistratuLink = findViewById(R.id.erregitratuLink)
        errorea = findViewById(R.id.errorea)
        atzeraBtn = findViewById(R.id.atzeraBtn)

        loginBtn.setOnClickListener { hasiSaioa() }
        erregistratuLink.setOnClickListener { registerLink() }
        atzeraBtn.setOnClickListener { atzera() }
    }
    private fun hasiSaioa(){

        if(!username.text.isEmpty() || !contraseña.text.isEmpty()){
            db.collection("users").get().addOnSuccessListener { queryDocumentSnapshots ->
                if(!queryDocumentSnapshots.isEmpty){
                    var userExist = false
                    for (document in queryDocumentSnapshots.documents) {
                        val usuarioRecorrido = document.getString("nombre")
                        val contraseñaRecorrido = document.getString("contraseña")
                        if (usuarioRecorrido?.equals(username.text.toString(), ignoreCase = true) == true &&
                            contraseñaRecorrido?.equals(contraseña.text.toString()) == true) {
                            userExist = true
                            val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                            prefs.edit().putString("userRegistrado", usuarioRecorrido).apply()
                            break
                        }
                    }
                    if(userExist){
                        val rankingIntent: Intent = Intent(this@LogInActivity, MainActivity::class.java)
                        startActivity(rankingIntent)
                    }else{
                        errorea.text= "Kredentzial okerrak"
                    }
                }
            }
        }else{
            errorea.text = "Datuak jarri"
        }

    }
    private fun registerLink(){
        val intentLogin = Intent(this@LogInActivity, RegisterActivity::class.java)
        startActivity(intentLogin)
    }
    private fun atzera(){
        val intentAtzera = Intent(this@LogInActivity, MainActivity::class.java)
        startActivity(intentAtzera)
    }
}
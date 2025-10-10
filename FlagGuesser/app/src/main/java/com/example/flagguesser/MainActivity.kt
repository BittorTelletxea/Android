package com.example.flagguesser

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    private lateinit var imageBandera: ImageView
    private lateinit var btnOpcion1: Button
    private lateinit var btnOpcion2: Button
    private lateinit var btnOpcion3: Button
    private lateinit var rankingBtn: ImageView
    val paisCodigo = Locale.getDefault().country

    val db = FirebaseFirestore.getInstance();

    private lateinit var username: TextView
    private var userRegistrado: String? = null

    private lateinit var banderaCorrecta: BanderaClass
    private lateinit var recordRachaText: TextView

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

        mostrarNuevaBandera()
        aldatuRecord()
    }

    /*private fun añadirDatos(){
        val nombre = db.collection("users").document("1").get()
        nombre.addOnSuccessListener { document ->
            if(document.exists()){
               val nombreUser = document.get("nombre").toString()
                username.text = "${nombreUser}"


            }
        }
    }*/
    fun aldatuRecord() {
        var rachaEncontrada = false

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
                                    aldatuRecord()
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
                                aldatuRecord()
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






    private fun mostrarNuevaBandera() {
        // Lista de botones
        val botones = listOf(btnOpcion1, btnOpcion2, btnOpcion3)

        // 🔹 Resetear color de fondo y habilitar
        botones.forEach { boton ->
            boton.setBackgroundColor(getColor(R.color.white)) // color original
            boton.isEnabled = true
        }

        // Elegir una bandera correcta aleatoria
        banderaCorrecta = listaBanderas.random()

        // Elegir otras 2 incorrectas
        val opciones = listaBanderas.shuffled().filter { it != banderaCorrecta }.take(2).toMutableList()
        opciones.add(banderaCorrecta)
        opciones.shuffle()

        // Mostrar la bandera
        imageBandera.setImageResource(banderaCorrecta.imagenResId)

        // Actualizar racha en pantalla
        tvRacha.text = "$racha"

        // Asignar texto y click listeners
        botones.forEachIndexed { index, boton ->
            boton.text = opciones[index].nombre
            boton.setOnClickListener {
                // Deshabilitar todos los botones temporalmente
                botones.forEach { it.isEnabled = false }

                if (opciones[index] == banderaCorrecta) {
                    // Acertó
                    boton.setBackgroundColor(getColor(R.color.green))
                    racha++
                    tvRacha.text = "$racha"

                } else {
                    // Falló
                    boton.setBackgroundColor(getColor(R.color.red))
                    val correctoIndex = opciones.indexOf(banderaCorrecta)
                    botones[correctoIndex].setBackgroundColor(getColor(R.color.green))

                    val usuario = db.collection("users")
                    val indice = usuario.get().addOnSuccessListener { query ->
                        if(!query.isEmpty){
                            for(document in query.documents){
                                if(document.getString("nombre").equals(userRegistrado)){
                                    val rachaUser = (document.get("racha") as? Number)?.toInt() ?: 0
                                        if(rachaUser < racha){
                                            db.collection("users").document(document.id).update("racha", racha).addOnSuccessListener {
                                                Toast.makeText(
                                                    this,
                                                    "Racha eguneratua",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                aldatuRecord()
                                            }
                                    }
                                }
                            }
                        }

                        // Reiniciamos la racha **dentro del listener de Firebase**
                        racha = 0
                        tvRacha.text = "$racha"
                    }
                }

                // Esperar un segundo antes de la siguiente bandera
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


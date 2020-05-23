package net.azarquiel.mycuisine.view.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_receta_detalle.*
import kotlinx.android.synthetic.main.content_recipes_acivity.*
import kotlinx.android.synthetic.main.rowrecipe.view.*
import net.azarquiel.mycuisine.R
import net.azarquiel.mycuisine.view.adapter.CustomAdapter
import net.azarquiel.mycuisine.view.adapter.DetailAdapter
import net.azarquiel.mycuisine.view.model.Receta
import net.azarquiel.mycuisine.view.model.User
import org.jetbrains.anko.toast

class RecetaDetalle : AppCompatActivity() {
    private lateinit var adapter:DetailAdapter
    private lateinit var receta:Receta
   private lateinit var storage:FirebaseStorage
   private lateinit var db: FirebaseFirestore
    private var voto:Boolean=false




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receta_detalle)
        db= FirebaseFirestore.getInstance()
        storage= FirebaseStorage.getInstance()
        receta=intent.getSerializableExtra("receta") as Receta
        initRV()
        inyectarInformacion()
        btnmgs.setOnClickListener { ilike() }
        btnnomgs.setOnClickListener { idislike() }

    }

    private fun idislike() {
    votar(2)
    }

    private fun ilike() {
        votar(1)

    }
    private fun votar(v:Int){
        if (voto==true){
          toast("Ya has votado")

        }else if(v==1) {
            if (voto != true) {
                receta.likes++
                btnmgs.text = receta.likes.toString()
                voto = true
            actualizar()


            }

        }else if (v == 2) {
            if (voto != true) {
                receta.dislikes++
                btnnomgs.text = receta.dislikes.toString()
                voto = true
                actualizar()
            }
        }
    }
    private fun actualizar(){
        db.collection("users").document("${receta.user}").collection("recetas").document("${receta.img}").set(receta)
    }

    private fun initRV() {
        adapter = DetailAdapter(this, R.layout.rowingredientes)
        rvIngredientes.layoutManager = LinearLayoutManager(this)
        rvIngredientes.adapter = adapter

    }
    private fun inyectarInformacion(){
        adapter.setIngredientes(receta.ingredientes)
        txtnombredetalle.text=receta.nombre
        txtpasosdetail.text=receta.pasos
        txtdificultaddetalle.text=receta.dificultad
        btnmgs.text=receta.likes.toString()
        btnnomgs.text=receta.dislikes.toString()
        val storageRef = storage.reference
        storageRef.child("images/${receta.img}.jpg").downloadUrl.addOnSuccessListener {

            Glide.with(this).load(it).into(ivRecetaDetalle)
        }.addOnFailureListener {
            // Handle any errors
        }

    }



}

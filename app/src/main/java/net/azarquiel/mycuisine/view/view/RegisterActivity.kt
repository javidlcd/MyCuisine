package net.azarquiel.mycuisine.view.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_register.*
import net.azarquiel.mycuisine.R
import org.jetbrains.anko.*




class RegisterActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore

    private lateinit var auth: FirebaseAuth
    private var imagecount:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        db=FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        buttoncrear.setOnClickListener{crearaccount()}
        btnfleizq.setOnClickListener{profileimagedown()}
        btnfledere.setOnClickListener{profileimageup()}

    }


    private fun profileimageup() {
       imagecount++
        if(imagecount>12){
            imagecount=0
        }
     refrescarfoto()

    }

     fun refrescarfoto() {
        when(imagecount){
            0->profile_image.setImageResource(R.drawable.p0)
            1->profile_image.setImageResource(R.drawable.p1)
            2->profile_image.setImageResource(R.drawable.p2)
            3->profile_image.setImageResource(R.drawable.p3)
            4->profile_image.setImageResource(R.drawable.p4)
            5->profile_image.setImageResource(R.drawable.p5)
            6->profile_image.setImageResource(R.drawable.p6)
            7->profile_image.setImageResource(R.drawable.p7)
            8->profile_image.setImageResource(R.drawable.p8)
            9->profile_image.setImageResource(R.drawable.p9)
            10->profile_image.setImageResource(R.drawable.p10)
            11->profile_image.setImageResource(R.drawable.p11)
            12->profile_image.setImageResource(R.drawable.p12)

        }


    }

    private fun profileimagedown() {
        imagecount--
        if(imagecount<0){
            imagecount=12
        }
        refrescarfoto()

    }


    private fun crearaccount() {

        if(txtemail.text.toString().isEmpty()||txtnombre.text.toString().isEmpty()||txtpassword.text.toString().isEmpty()){
            toast("Campos necesarios")
        }else if (txtpassword.text.toString().length<5){
            toast("La contraseña debe tener minimo 6 caracteres")
        }else
        addUser(txtemail.text.toString(),txtpassword.text.toString(),txtnombre.text.toString())
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser!=null){
            val intent = Intent(this, RecipesAcivity::class.java)
            intent.putExtra("id",currentUser.uid)
            startActivity(intent)


        }

    }
    private fun addData(nombre:String,uid:String?) {
        val user: MutableMap<String, Any> = HashMap() // diccionario key value
        user["uid"] = uid!!
        user["nombre"] = nombre
        user["img"]=imagecount

        db.collection("users")
            .document("${uid}").set(user)
            .addOnSuccessListener { Log.d("", "DocumentSnapshot successfully written!")
            val ccuser=auth.currentUser
                ccuser!!.sendEmailVerification().addOnSuccessListener {
                    Log.e("Success","Email enviado")
                }.addOnFailureListener {
                    Log.e("Failure", "Error al enviar")
                }
            updateUI(ccuser)}
            .addOnFailureListener { e -> Log.w("", "Error writing document", e) }
    }



    private fun addUser(email:String,password: String,nombre:String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.e(email, "Registration success")

                    val uid=auth.currentUser?.uid
                    addData(nombre,uid)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.e(email, "Registration failled")
                    updateUI(null)
                }


            }


    }
}

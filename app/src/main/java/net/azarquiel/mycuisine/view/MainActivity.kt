package net.azarquiel.mycuisine.view

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore


import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


import net.azarquiel.mycuisine.R
import net.azarquiel.mycuisine.view.view.RecipesAcivity
import net.azarquiel.mycuisine.view.view.RegisterActivity
import org.jetbrains.anko.*





class MainActivity : AppCompatActivity() {
    lateinit var db: FirebaseFirestore

    private lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db=FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        btnlogin.setOnClickListener{login()}
        btncrear.setOnClickListener{createuser()}
        btnrestablecer.setOnClickListener{contraseña()}


        fab.setOnClickListener { view ->
            alert("version 1.0") {
               title="MyCuisine"
                yesButton { }

            }.show()


        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        var currentUser = auth.currentUser
        updateUI(currentUser)
    }

        private fun updateUI(currentUser: FirebaseUser?) {
            if (currentUser!=null){
                val intent = Intent(this, RecipesAcivity::class.java)
                intent.putExtra("id",currentUser.uid)
                startActivity(intent)
            }

        }
    private fun contraseña(){
        alert {
            title = "Restablecer contraseña"
            customView {
                verticalLayout {
                    lparams(width = wrapContent, height = wrapContent)

                    val etEmail = editText {
                        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        hint = "Email"
                        padding = dip(16)
                    }
                    positiveButton("Aceptar") {
                        if (etEmail.text.toString().isEmpty())
                            toast("Es necesario facilitar su email")
                        else {
                            val emailAddress = etEmail.text.toString()
                            auth.sendPasswordResetEmail(emailAddress)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d("Confirmacion", "Email sent.")
                                        toast("Email enviado")


                                    }else{
                                        Log.d("Fail", "Email Error.")
                                        toast("Error al enviar el email")
                                    }
                                }
                        }
                    }
                }
            }
        }.show()
    }

    private fun createuser() {

        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }


    private fun login(){
         if(email.text.toString().length.equals(0)){
            toast("Falta por rellenar el email")

        }else if(password.text.toString().length.equals(0)){
            toast("Falta por rellenar la contraseña")
        }else{
             auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                 .addOnCompleteListener(this) { task ->
                     if (task.isSuccessful) {
                         Log.d("", "signInWithEmail:success")
                         val user = auth.currentUser
                         Log.d("usuario","${user?.uid}")
                         updateUI(user)
                     } else {
                         Log.w("", "signInWithEmail:failure", task.exception)
                         Toast.makeText(baseContext, "Authentication failed.",
                             Toast.LENGTH_SHORT).show()
                         updateUI(null)
                     }
                 }
        }
    }




}


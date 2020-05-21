package net.azarquiel.mycuisine.view.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.activity_account.txtnombre
import kotlinx.android.synthetic.main.activity_register.*
import net.azarquiel.mycuisine.R
import net.azarquiel.mycuisine.view.MainActivity
import net.azarquiel.mycuisine.view.model.User
import org.jetbrains.anko.*

class Account : AppCompatActivity() {
    private lateinit var user:User
    private var imgcount:Int=0
    lateinit var  db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        db = FirebaseFirestore.getInstance()
         user=intent.getSerializableExtra("user") as User
         imgcount=user.img.toInt()
        txtnombre.text=user.nombre

        btncambiar.setOnClickListener{cambiarimg()}
        btneliminar.setOnClickListener{deleteaccount()}
        btnactualizar.setOnClickListener{actualizar()}
        refrescar()

    }

    private fun deleteaccount() {
        alert {
            title = "Eliminar cuenta"
            customView {
                verticalLayout {
                    lparams(width = wrapContent, height = wrapContent)


                    val etEmail = editText {
                        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        hint = "Email"
                        padding = dip(16)
                    }
                    val etPass = editText {
                        inputType=InputType.TYPE_TEXT_VARIATION_PASSWORD
                        hint = "Contaseña"
                        padding = dip(16)
                    }
                    positiveButton("Aceptar") {
                        if (etPass.text.toString().isEmpty() || etEmail.text.toString().isEmpty())
                            toast("Campos Obligatorios")
                        else{
                            alert("Si aceptas se eliminara la cuenta y no podras recuperarla") {

                                yesButton {
                                    val user = FirebaseAuth.getInstance().currentUser
                                    val credential = EmailAuthProvider
                                        .getCredential(etEmail.text.toString(), etPass.text.toString())
                                    user?.reauthenticate(credential)
                                        ?.addOnSuccessListener {
                                            Log.d("Usuario", "User re-authenticated.")
                                            user
                                            eliminardb(user)
                                            user?.delete()
                                                ?.addOnSuccessListener {
                                                        Log.d("Delete", "User account deleted.")
                                                        toast("Usuario eliminado correctamente")
                                                }.addOnSuccessListener {
                                                    FirebaseAuth.getInstance().signOut()
                                                    val intent = Intent(context, MainActivity::class.java)
                                                    startActivity(intent)
                                                    finish()
                                                }
                                        }
                                        ?.addOnFailureListener { toast("No se ha podido eliminar la cuenta, credenciales invalidas") }
                                }
                                noButton { }
                            }.show()

                        }
                    }
                }
            }
        }.show()

    }

    private fun eliminardb(user:FirebaseUser) {
        db.collection("users").document(user.uid).delete()
            .addOnSuccessListener { Log.d("Completado", "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w("Error", "Error deleting document", e) }

    }

    private fun actualizar() {
        val img = db.collection("users").document("${user.uid}")


        img
            .update("img", imgcount)
            .addOnSuccessListener { Log.d("", "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w("", "Error updating document", e) }


    }

    private fun cambiarimg() {
        imgcount++
        if(imgcount>12){
            imgcount=0
        }
        refrescar()
    }

    private fun refrescar(){

        when(imgcount){
            0->profile_imageacc.setImageResource(R.drawable.p0)
            1->profile_imageacc.setImageResource(R.drawable.p1)
            2->profile_imageacc.setImageResource(R.drawable.p2)
            3->profile_imageacc.setImageResource(R.drawable.p3)
            4->profile_imageacc.setImageResource(R.drawable.p4)
            5->profile_imageacc.setImageResource(R.drawable.p5)
            6->profile_imageacc.setImageResource(R.drawable.p6)
            7->profile_imageacc.setImageResource(R.drawable.p7)
            8->profile_imageacc.setImageResource(R.drawable.p8)
            9->profile_imageacc.setImageResource(R.drawable.p9)
            10->profile_imageacc.setImageResource(R.drawable.p10)
            11->profile_imageacc.setImageResource(R.drawable.p11)
            12->profile_imageacc.setImageResource(R.drawable.p12)

        }
    }

}

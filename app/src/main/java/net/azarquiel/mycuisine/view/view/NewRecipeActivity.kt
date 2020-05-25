package net.azarquiel.mycuisine.view.view


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_new_recipe.*
import net.azarquiel.mycuisine.R
import net.azarquiel.mycuisine.view.picker.PickerPacoPul
import org.jetbrains.anko.*
import java.io.ByteArrayOutputStream


class NewRecipeActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener
{


    private lateinit var picker: PickerPacoPul
    private var diff:Int=0
    private lateinit var ingredientes:ArrayList<String>
    private lateinit var db: FirebaseFirestore
    private lateinit var user:FirebaseAuth
    private lateinit var storage:FirebaseStorage

    companion object {
        const val REQUEST_PERMISSION = 200
        const val REQUEST_GALLERY = 100
        const val REQUEST_CAMERA = 2
        const val TAG = "ImgPicker"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_recipe)
        spinner.onItemSelectedListener= this
        storage = FirebaseStorage.getInstance()
        db= FirebaseFirestore.getInstance()
        user= FirebaseAuth.getInstance()
        ingredientes=ArrayList()
        picker= PickerPacoPul(this)
        btnimagen.setOnClickListener {picker.showPictureDialog() }
        btningredientes.setOnClickListener { dialogoingredientes() }
        btnsubir.setOnClickListener {checkcampos()}


    }

    private fun subirimg(IdImage:String) {
        val storageRef = storage.reference
        val imagenreceta = storageRef.child("images/${IdImage}.jpg")
        iv.isDrawingCacheEnabled = true
        iv.buildDrawingCache()
        val bitmap = (iv.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = imagenreceta.putBytes(data)
        uploadTask.addOnFailureListener {
        }.addOnSuccessListener {
        }
    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        picker.onActivityResult(requestCode, resultCode, data)
        iv.setImageBitmap(picker.bitmap)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        picker.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

            private fun checkcampos(){
            if(editNombre.length() == 0||editPasos.length() == 0){
                toast("Campos necesarios")
            }else if(diff==0){
                toast("Elige la dificultad de la receta")
            }else if(ingredientes.size==0) {
                toast("Al menos debe haber un ingrediente")
            }else{
                subirReceta(editNombre.text.toString(),editPasos.text.toString())

            }


    }
    private fun subirReceta(Nombre:String,Pasos:String){
        val receta: MutableMap<String, Any> = HashMap() // diccionario key value
        receta["nombre"] = Nombre
        receta["img"] = ""
        receta["ingredientes"]=ingredientes
        receta["pasos"]=Pasos
        receta["likes"]=0
        receta["dislikes"]=0
        receta["user"]=user.currentUser?.uid.toString()
        var difi=""
        when(diff){
            1->difi="Facil"
            2->difi="Normal"
            3->difi="Dificil"
        }
        receta["dificultad"]=difi

        db.collection("users")
            .document("${user.currentUser?.uid}").collection("recetas").add(receta)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                var idimage:String="${documentReference.id}"
                val img = db.collection("users").document("${user.currentUser?.uid}").collection("recetas").document(idimage)


                img.update("img", idimage)
                    .addOnSuccessListener {
                        subirimg(idimage)
                        Log.d("", "DocumentSnapshot successfully updated!")
                        val intent = Intent(this, RecipesAcivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e -> Log.w("", "Error updating document", e)
                        toast("Error al subir la imagen")}

            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                toast("Error al subir la receta")
            }
    }
        private fun dialogoingredientes(){
            alert {
                customView {
                    verticalLayout {
                        val etIngrediente = editText {
                            hint = "Ingrediente y peso"
                        }
                        positiveButton("Aceptar") {
                            if(etIngrediente.length()!=0)
                           ingredientes.add(etIngrediente.text.toString())
                        }
                        negativeButton("Añadir otro"){
                            if(etIngrediente.length()!=0) {
                                ingredientes.add(etIngrediente.text.toString())
                                dialogoingredientes()
                            }

                        }
                    }
                }
            }.show()
        }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
       diff=position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

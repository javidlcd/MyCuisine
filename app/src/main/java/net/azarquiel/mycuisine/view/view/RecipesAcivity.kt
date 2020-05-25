package net.azarquiel.mycuisine.view.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import net.azarquiel.mycuisine.R

import kotlinx.android.synthetic.main.activity_recipes_acivity.*
import kotlinx.android.synthetic.main.content_recipes_acivity.*
import net.azarquiel.mycuisine.view.MainActivity
import net.azarquiel.mycuisine.view.adapter.CustomAdapter
import net.azarquiel.mycuisine.view.model.Receta
import net.azarquiel.mycuisine.view.model.User
import org.jetbrains.anko.*


class RecipesAcivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    override fun onQueryTextChange(query: String): Boolean {
        val original = ArrayList(recetas)
        adapter.setRecetas(ArrayList(original.filter { receta -> receta.nombre.toUpperCase().contains(query.toUpperCase())}))
        return false
    }

    override fun onQueryTextSubmit(text: String): Boolean {
        return false
    }


    lateinit var db: FirebaseFirestore
    private var usuarios:ArrayList<User> = ArrayList()
    private lateinit var usuarioReceta:User
    private lateinit var searchView:SearchView
    private var recetas: ArrayList<Receta> = ArrayList()
    private lateinit var user: User
    private lateinit var adapter:CustomAdapter
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()
        auth=FirebaseAuth.getInstance()
        getUsers()
        setListener()
        setContentView(R.layout.activity_recipes_acivity)
        setSupportActionBar(toolbar)
        initRV()



        fab.setOnClickListener {
            val intent = Intent(this, NewRecipeActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
    private fun initRV() {
        adapter = CustomAdapter(this, R.layout.rowrecipe)
        rvRecetas.layoutManager = LinearLayoutManager(this)
        rvRecetas.adapter = adapter
    }

        private fun setListener() {
            val docRef = db.collectionGroup("recetas")
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    documentToList(snapshot.documents)
                    adapter.setRecetas(recetas)

                } else {
                    Log.d("", "Current data: null")
                }
            }

        }

        private fun documentToList(documents: List<DocumentSnapshot>) {
            recetas.clear()
            documents.forEach { d ->


                    val nombre = d["nombre"] as String
                    val img = d["img"] as String
                    val ingredientes = d["ingredientes"] as ArrayList<String>
                    val pasos = d["pasos"] as String
                    val dificultad = d["dificultad"] as String
                    val likes = d["likes"] as Long
                    val dislikes = d["dislikes"] as Long
                    val userrecipe = d["user"] as String
                    recetas.add(
                        Receta(
                            nombre = nombre,
                            img = img,
                            ingredientes = ingredientes,
                            pasos = pasos,
                            dificultad = dificultad,
                            likes = likes,
                            dislikes = dislikes,
                            user = userrecipe
                        )
                    )
            }

        }
    fun ClickRow(v: View){
        val recetapulsada = v.tag as Receta
        val intent = Intent(this, RecetaDetalle::class.java)
        usuarios.forEach {
            if (it.uid==recetapulsada.user){
                usuarioReceta=it

            }
        }

                intent.putExtra("receta", recetapulsada)
                intent.putExtra("user",usuarioReceta)




        startActivity(intent)

    }
    private fun getUsers() {
        val docRef = db.collectionGroup("users")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                parseUsers(snapshot.documents)

            } else {
                Log.d("", "Current data: null")
            }
        }
    }
    private fun parseUsers(documents: List<DocumentSnapshot>) {

        documents.forEach { d ->

            val nombre = d["nombre"] as String
            val img = d["img"] as Long
            val uid = d["uid"] as String

            if (uid==auth.currentUser?.uid){
                usuarios.add(User(nombre=nombre,img = img,uid = uid))
                user=User(nombre=nombre,img = img,uid = uid)

            }

            usuarios.add(User(nombre=nombre,img = img,uid = uid))
        }



    }






    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.

        menuInflater.inflate(R.menu.menu_recipes, menu)
        val searchItem = menu.findItem(R.id.search)
        searchView = searchItem.actionView as SearchView
        searchView.setQueryHint("Search...")
        searchView.setOnQueryTextListener(this)



        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        var id= item.getItemId()
        if(id==R.id.logout){
            alert("¿Estas seguro de que quieres salir de la cuenta?") {

                yesButton {logout() }
                noButton { }
            }.show()
        }else if(id==R.id.account){
            val intent = Intent(this, Account::class.java)
            intent.putExtra("user",user)
            startActivity(intent)


        }
        return true
    }
    private fun logout(){
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


}

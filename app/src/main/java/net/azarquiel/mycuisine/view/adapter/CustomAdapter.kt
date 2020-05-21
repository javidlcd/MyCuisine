package net.azarquiel.mycuisine.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.rowrecipe.view.*
import net.azarquiel.mycuisine.view.model.Receta
import com.google.firebase.storage.StorageReference
import net.azarquiel.mycuisine.view.view.NewRecipeActivity


class CustomAdapter(val context: Context,
                    val layout: Int
                    ) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

   private  var dataList :ArrayList<Receta> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewlayout = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(viewlayout, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    internal fun setRecetas(Recetas: ArrayList<Receta>) {
        this.dataList = Recetas
        notifyDataSetChanged()
    }



    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: Receta){

            // itemview es el item de diseño
            // al que hay que poner los datos del objeto dataItem


            Log.e("codigo",dataItem.img)

            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference



            storageRef.child("images/${dataItem.img}.jpg").downloadUrl.addOnSuccessListener {

                Glide.with(itemView).load(it).into(itemView.ivReceta)
            }.addOnFailureListener {
                // Handle any errors
            }




            itemView.txtNombreRecipe.text=dataItem.nombre
            itemView.txtDificultad.text=dataItem.dificultad




            itemView.tag = dataItem
        }

    }

}
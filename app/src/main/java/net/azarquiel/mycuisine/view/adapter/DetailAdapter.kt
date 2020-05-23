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
import kotlinx.android.synthetic.main.rowingredientes.view.*
import net.azarquiel.mycuisine.view.view.NewRecipeActivity


class DetailAdapter(val context: Context,
                    val layout: Int
                    ) : RecyclerView.Adapter<DetailAdapter.ViewHolder>() {

   private  var dataList :ArrayList<String> = ArrayList()

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

    internal fun setIngredientes(Ingredientes: ArrayList<String>) {
        this.dataList = Ingredientes
        notifyDataSetChanged()
    }



    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: String){

            itemView.txtIngredienterv.text=dataItem
            itemView.tag = dataItem
        }

    }

}
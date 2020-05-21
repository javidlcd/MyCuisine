package net.azarquiel.mycuisine.view.model

import java.io.Serializable

data class User(
    var uid:String,
    var nombre:String,
    var img:Long

):Serializable
data class Receta(
    var nombre:String,
    var img:String,
    var ingredientes:ArrayList<String>,
    var pasos:String,
    var dificultad:String,
    var likes:Long,
    var dislikes:Long,
    var user:String
):Serializable
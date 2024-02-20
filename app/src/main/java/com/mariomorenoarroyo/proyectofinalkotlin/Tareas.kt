package com.mariomorenoarroyo.proyectofinalkotlin

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class Tareas(
    val id:String,
    val nombreTarea: String,
    val descripcionTarea: String,
    val lenguaje: String,
    val tareasPendientes: String
) : Parcelable, Serializable

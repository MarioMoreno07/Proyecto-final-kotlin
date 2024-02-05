package com.mariomorenoarroyo.proyectofinalkotlin

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class Tareas(
    val nombreTarea: String,
    val descripcionTarea: String,
    val lenguaje: String,
    val listaAmpliable: MutableList<String> = mutableListOf()
) : Parcelable, Serializable

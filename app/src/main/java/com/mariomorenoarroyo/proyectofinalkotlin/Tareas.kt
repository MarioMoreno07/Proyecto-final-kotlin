package com.mariomorenoarroyo.proyectofinalkotlin

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Tareas(
    val nombreTarea: String,
    val descripcionTarea: String,
    val lenguaje: String,
    val numero: Int,
    val listaAmpliable: MutableList<String> = mutableListOf()
) : Parcelable

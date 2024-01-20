package com.mariomorenoarroyo.proyectofinalkotlin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PrimerFragment : Fragment() {

    // Lista de tareas
    private val listaDeTareas = mutableListOf<Tareas>()

    // Variable para llevar un seguimiento del último número de tarea utilizado
    private var ultimoNumeroDeTarea = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el diseño para este fragmento
        val view = inflater.inflate(R.layout.fragment_primer, container, false)

        // Configurar el RecyclerView y su adaptador
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val tareasAdapter = TareasAdapter(listaDeTareas, activity as TareasListener)
        recyclerView.adapter = tareasAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Notificar al adaptador que se ha actualizado la lista de tareas
        tareasAdapter.updateData(listaDeTareas)

        // Tareas de ejemplo
        listaDeTareas.add(crearNuevaTarea("Tarea de ejemplo", "Descripción de ejemplo", "Java"))
        listaDeTareas.add(crearNuevaTarea("Tarea de ejemplo 2", "Descripción de ejemplo 2", "C++"))
        listaDeTareas.add(crearNuevaTarea("Tarea de ejemplo 3", "Descripción de ejemplo 3", "Kotlin"))

        return view
    }

    // Función para crear una nueva tarea con número autoincremental
    private fun crearNuevaTarea(titulo: String, descripcion: String, lenguaje: String): Tareas {
        ultimoNumeroDeTarea++
        return Tareas(titulo, descripcion, lenguaje, ultimoNumeroDeTarea)
    }
}

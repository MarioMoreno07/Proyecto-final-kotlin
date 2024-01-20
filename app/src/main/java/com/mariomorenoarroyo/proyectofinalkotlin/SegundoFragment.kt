package com.mariomorenoarroyo.proyectofinalkotlin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment

class SegundoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_segundo, container, false)

        val nombreTarea = view.findViewById<EditText>(R.id.NombreTarea)
        val descripcion = view.findViewById<EditText>(R.id.Descripcion)
        val opcionesSpinner = view.findViewById<Spinner>(R.id.Opciones)
        val buttonAñadirTarea = view.findViewById<Button>(R.id.button)

        if (nombreTarea != null && descripcion != null && opcionesSpinner != null && buttonAñadirTarea != null) {
            val opciones = arrayOf("Java", "Kotlin", "Python", "C++", "C", "C#", "JavaScript", "PHP", "Otro")
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opciones)
            opcionesSpinner.adapter = adapter

            buttonAñadirTarea.setOnClickListener {
                val nombreTexto = nombreTarea.text.toString()
                val descripcionTexto = descripcion.text.toString()

                if (nombreTexto.isBlank() || descripcionTexto.isBlank()) {
                    Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                } else {
                    // Crear una nueva tarea con valores temporales
                    val nuevaTarea = Tareas(nombreTexto, descripcionTexto, "LenguajeEjemplo", 123)

                    // Obtener la actividad actual y verificar si implementa TareasListener
                    val tareasListener = activity as? TareasListener

                    // Si implementa TareasListener, agregar la nueva tarea
                    tareasListener?.agregarTarea(nuevaTarea)

                    // Mostrar mensaje y borrar campos
                    Toast.makeText(requireContext(), "¡Tarea añadida!", Toast.LENGTH_SHORT).show()

                    nombreTarea.setText("")
                    descripcion.setText("")
                }
            }
        }

        return view
    }
}

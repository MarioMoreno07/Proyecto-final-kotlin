package com.mariomorenoarroyo.proyectofinalkotlin

import PrimerFragment
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SegundoFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var currentUserEmail: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_segundo, container, false)

        val nombreTarea = view.findViewById<EditText>(R.id.NombreTarea)
        val descripcion = view.findViewById<EditText>(R.id.Descripcion)
        val opcionesSpinner = view.findViewById<Spinner>(R.id.Opciones)
        val buttonAñadirTarea = view.findViewById<Button>(R.id.button)

        // Obtener el email del usuario actual
        currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""

        if (nombreTarea != null && descripcion != null && opcionesSpinner != null && buttonAñadirTarea != null) {
            val opciones = arrayOf("Java", "Kotlin", "Python", "C++", "C", "C#", "JavaScript", "PHP", "Otro")
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opciones)
            opcionesSpinner.adapter = adapter

            buttonAñadirTarea.setOnClickListener {
                val nombreTexto = nombreTarea.text.toString()
                val descripcionTexto = descripcion.text.toString()
                val opcionSeleccionada = opciones[opcionesSpinner.selectedItemPosition] // Obtener la opción seleccionada del Spinner

                if (nombreTexto.isBlank() || descripcionTexto.isBlank()) {
                    Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                } else {

                    // Guardar los datos en Firestore con el email del usuario como identificador
                    db.collection("tareas").document(currentUserEmail).collection("misTareas").add(
                        hashMapOf(
                            "nombreTarea" to nombreTarea.text.toString(),
                            "descripcion" to descripcion.text.toString(),
                            "lenguaje" to opcionSeleccionada,
                        )
                    ).addOnSuccessListener { documentReference ->
                        Toast.makeText(requireContext(), "¡Tarea añadida!", Toast.LENGTH_SHORT).show()
                        //Vuelve al primer fragment
                        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment_main, PrimerFragment()).commit()
                    }.addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Error al guardar la tarea: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

                    // Limpiar campos
                    nombreTarea.setText("")
                    descripcion.setText("")
                }
            }
        }


        return view
    }



}

package com.mariomorenoarroyo.proyectofinalkotlin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditarTareaFragment : Fragment() {

    private lateinit var tarea: Tareas
    private lateinit var nombreEditText: EditText
    private lateinit var descripcionEditText: EditText
    private lateinit var tareaPendienteEditText: EditText
    private lateinit var guardarButton: Button

    private val db = FirebaseFirestore.getInstance()
    private val currentUserEmail: String = FirebaseAuth.getInstance().currentUser?.email ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tarea = it.getSerializable(ARG_TAREA) as Tareas
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_editar_tarea, container, false)

        // Inicializar vistas
        nombreEditText = view.findViewById(R.id.NombreTarea)
        descripcionEditText = view.findViewById(R.id.Descripcion)
        tareaPendienteEditText= view.findViewById(R.id.TareasPendientes)
        guardarButton = view.findViewById(R.id.button)

        // Mostrar datos de la tarea en los EditText
        mostrarDatosTarea()

        // Configurar click listener para el botón de guardar
        guardarButton.setOnClickListener {
            actualizarTareaEnFirestore()
        }

        return view
    }

    private fun mostrarDatosTarea() {
        nombreEditText.setText(tarea.nombreTarea)
        descripcionEditText.setText(tarea.descripcionTarea)
        tareaPendienteEditText.setText(tarea.tareasPendientes)
    }

    private fun actualizarTareaEnFirestore() {
        val nuevoNombre = nombreEditText.text.toString().trim()
        val nuevaDescripcion = descripcionEditText.text.toString().trim()
        val nuevaTareaPendiente = tareaPendienteEditText.text.toString().trim()

        // Verificar que los campos no estén vacíos
        if (nuevoNombre.isEmpty() || nuevaDescripcion.isEmpty()|| nuevaTareaPendiente.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Actualizar los datos de la tarea en Firestore
        db.collection("tareas").document(currentUserEmail).collection("misTareas")
            .document(tarea.id)
            .update(
                mapOf(
                    "nombreTarea" to nuevoNombre,
                    "descripcion" to nuevaDescripcion,
                    "tareaPendientes" to nuevaTareaPendiente
                )
            )
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Tarea actualizada correctamente", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al actualizar la tarea: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val ARG_TAREA = "tarea"

        fun newInstance(tarea: Tareas): EditarTareaFragment {
            val fragment = EditarTareaFragment()
            val args = Bundle()
            args.putSerializable(ARG_TAREA, tarea)
            fragment.arguments = args
            return fragment
        }
    }
}

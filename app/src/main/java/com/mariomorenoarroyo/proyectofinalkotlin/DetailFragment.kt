package com.mariomorenoarroyo.proyectofinalkotlin

import PrimerFragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mariomorenoarroyo.proyectofinalkotlin.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    private val db = FirebaseFirestore.getInstance()
    private val currentUserEmail: String = FirebaseAuth.getInstance().currentUser?.email ?: ""
    private val currentTask: String = FirebaseFirestore.getInstance().collection("tareas").document(currentUserEmail).collection("misTareas").document().id

    companion object {
        private const val ARG_TAREA = "tarea"

        // Método estático para crear una nueva instancia del DetailFragment con la tarea como argumento
        fun newInstance(tarea: Tareas): DetailFragment {
            val fragment = DetailFragment()
            val args = Bundle()
            args.putSerializable(ARG_TAREA, tarea)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnEliminar.setOnClickListener {
            deleteTask()
        }

        // Obtener la tarea pasada como argumento
        val tarea = arguments?.getSerializable(ARG_TAREA) as? Tareas
        if (tarea != null) {
            // Llamar a obtenerTareasDeFirestore() con la tarea
            obtenerTareasDeFirestore(tarea)
        } else {
           Toast.makeText(requireContext(), "Error al obtener la tarea", Toast.LENGTH_SHORT).show()
        }
    }

    private fun obtenerTareasDeFirestore(tarea: Tareas) {
        // Obtener los datos de la tarea y mostrarlos en el layout
        binding.nombre.text = tarea.nombreTarea
        binding.descripcion.text = tarea.descripcionTarea


    }

    private fun deleteTask() {
        db.collection("tareas").document(currentUserEmail).collection("misTareas")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Tarea eliminada correctamente", Toast.LENGTH_SHORT).show()
                            //navegar a PrimerFragment
                            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment_main, PrimerFragment()).commit()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Error al eliminar la tarea: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error al obtener las tareas: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mariomorenoarroyo.proyectofinalkotlin.EditarTareaFragment
import com.mariomorenoarroyo.proyectofinalkotlin.R
import com.mariomorenoarroyo.proyectofinalkotlin.Tareas
import com.mariomorenoarroyo.proyectofinalkotlin.databinding.FragmentDetailBinding
import kotlinx.coroutines.launch

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    private val db = FirebaseFirestore.getInstance()
    private val currentUserEmail: String = FirebaseAuth.getInstance().currentUser?.email ?: ""

    companion object {
        private const val ARG_TAREA = "tarea"

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
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEliminar.setOnClickListener {
            val tarea = arguments?.getSerializable(ARG_TAREA) as? Tareas
            tarea?.id?.let { taskId ->
                // Lanzar una corrutina para eliminar la tarea por su ID
                lifecycleScope.launch {
                    deleteTask(taskId)
                }
            }
        }

        binding.btnEditar.setOnClickListener{
            //Moverme al editTareaFragment
            navigateToEditTareaFragment()

        }

        val tarea = arguments?.getSerializable(ARG_TAREA) as? Tareas
        if (tarea != null) {
            mostrarDetallesTarea(tarea)
        } else {
            Toast.makeText(requireContext(), "Error al obtener la tarea", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarDetallesTarea(tarea: Tareas) {
        // Limpiar los campos antes de mostrar los nuevos datos
        binding.nombre.text = ""
        binding.descripcion.text = ""
        binding.TareasPen.text = ""

        // Obtener todos los detalles de la tarea desde Firebase utilizando su ID
        obtenerDetallesTarea(tarea.id)
    }

    private fun obtenerDetallesTarea(taskId: String) {
        // Consultar Firebase para obtener todos los detalles de la tarea utilizando su ID
        db.collection("tareas").document(currentUserEmail).collection("misTareas").document(taskId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val nombreTarea = documentSnapshot.getString("nombreTarea") ?: ""
                    val descripcion = documentSnapshot.getString("descripcion") ?: ""
                    val tareaPendientes = documentSnapshot.getString("tareaPendientes") ?: ""

                    // Mostrar los detalles en los campos correspondientes
                    binding.nombre.text = nombreTarea
                    binding.descripcion.text = descripcion
                    binding.TareasPen.text = tareaPendientes
                } else {
                    Toast.makeText(requireContext(), "No se encontraron detalles para esta tarea", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error al obtener los detalles de la tarea: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }



    private fun deleteTask(taskId: String) {
        db.collection("tareas").document(currentUserEmail).collection("misTareas").document(taskId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Tarea eliminada correctamente",
                    Toast.LENGTH_SHORT
                ).show()
                //Vuelve al primer fragment
                requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment_main, PrimerFragment()).commit()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Error al eliminar la tarea: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    private fun navigateToEditTareaFragment() {
        val tarea = arguments?.getSerializable(ARG_TAREA) as? Tareas
        if (tarea != null) {
            val editTareaFragment = EditarTareaFragment.newInstance(tarea)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_main, editTareaFragment) // Reemplaza R.id.fragment_container_view con el ID correcto del contenedor de fragmentos
                .addToBackStack(null) // Permite volver al fragmento anterior
                .commit()
        } else {
            Toast.makeText(requireContext(), "Error al obtener la tarea", Toast.LENGTH_SHORT).show()
        }
    }

}

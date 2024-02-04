import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mariomorenoarroyo.proyectofinalkotlin.R
import com.mariomorenoarroyo.proyectofinalkotlin.Tareas
import com.mariomorenoarroyo.proyectofinalkotlin.TareasAdapter
import com.mariomorenoarroyo.proyectofinalkotlin.TareasListener



class PrimerFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    private val listaDeTareas = mutableListOf<Tareas>()
    private lateinit var tareasAdapter: TareasAdapter





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_primer, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        tareasAdapter = TareasAdapter(listaDeTareas, activity as TareasListener)
        recyclerView.adapter = tareasAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Recuperar las tareas de Firestore
        obtenerTareasDeFirestore()



        return view


    }

    // En el método obtenerTareasDeFirestore en PrimerFragment
    private fun obtenerTareasDeFirestore() {
        // Limpiar la lista de tareas antes de agregar nuevas
        listaDeTareas.clear()

        // Obtener el correo electrónico del usuario actual
        val email = FirebaseAuth.getInstance().currentUser?.email

        if (email != null) {
            // Consulta Firestore para obtener las tareas del usuario
            db.collection("tareas").document(email).collection("misTareas")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val nombreTarea = document.getString("nombreTarea") ?: ""
                        val descripcion = document.getString("descripcion") ?: ""
                        val lenguaje = document.getString("lenguaje") ?: ""

                        // Crear objeto Tareas y agregarlo a la lista
                        val tarea = Tareas(nombreTarea, descripcion, lenguaje)
                        listaDeTareas.add(tarea)
                    }

                    // Notificar al adaptador que se han actualizado los datos
                    tareasAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error al obtener las tareas", exception)
                }
        }
    }

}

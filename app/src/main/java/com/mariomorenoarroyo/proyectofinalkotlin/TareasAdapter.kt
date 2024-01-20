package com.mariomorenoarroyo.proyectofinalkotlin

// TareasAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mariomorenoarroyo.proyectofinalkotlin.databinding.ViewTareasBinding

class TareasAdapter(
    private val tareas: MutableList<Tareas>,
    private val tareaListener: TareasListener
) : RecyclerView.Adapter<TareasAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var listaAmpliable: String
        private lateinit var lenguaje: String
        private lateinit var descripcionTarea: String
        private lateinit var nombreTarea: String
        val binding = ViewTareasBinding.bind(view)

        fun bind(tarea: Tareas, tareaListener: TareasListener) {
            with(binding) {
                // Asigna los valores de la tarea a los elementos de la vista
                nombreTarea = tarea.nombreTarea
                descripcionTarea = tarea.descripcionTarea
                lenguaje = tarea.lenguaje

                // Si la lista es no nula y no está vacía, muestra los elementos de la lista
                if (tarea.listaAmpliable.isNotEmpty()) {
                    listaAmpliable = tarea.listaAmpliable.joinToString(", ")
                } else {
                    // Si la lista está vacía, muestra un mensaje indicando eso
                    listaAmpliable = "Lista vacía"
                }

                // Configura el onClickListener de cada elemento de la lista
                root.setOnClickListener {
                    // Notifica al TareaListener que se ha hecho clic en una tarea
                    tareaListener.onTareaClick(tarea)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.view_tareas, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tarea = tareas[position]
        holder.bind(tarea, tareaListener)
    }

    override fun getItemCount() = tareas.size

    // Método para actualizar los datos del adaptador
    fun updateData(newTareas: List<Tareas>) {
        tareas.clear()
        tareas.addAll(newTareas)
        notifyDataSetChanged()
    }

    fun addTarea(tarea: Tareas) {
        // Obtener el último número autoincremental en la lista (o 0 si la lista está vacía)
        val ultimoNumero = tareas.lastOrNull()?.numero ?: 0

        // Generar un nuevo número autoincremental
        val nuevoNumero = ultimoNumero + 1

        // Copiar la tarea con el nuevo número
        val tareaConNumero = tarea.copy(numero = nuevoNumero)

        // Agregar la tarea a la lista
        tareas.add(tareaConNumero)

        // Notificar al adaptador que se ha agregado una nueva tarea
        notifyDataSetChanged()
    }
}

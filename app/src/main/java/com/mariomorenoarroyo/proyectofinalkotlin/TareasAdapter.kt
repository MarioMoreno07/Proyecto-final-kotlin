package com.mariomorenoarroyo.proyectofinalkotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mariomorenoarroyo.proyectofinalkotlin.databinding.ViewTareasBinding

class TareasAdapter(
    private val tareas: MutableList<Tareas>,
    private val tareaListener: TareasListener
) : RecyclerView.Adapter<TareasAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ViewTareasBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tarea: Tareas) {
            with(binding) {
                // Asigna los valores de la tarea a los elementos de la vista
                Titulo.text = tarea.nombreTarea
                textViewDescripcionTarea.text = tarea.descripcionTarea
                Lenguaje.text = tarea.lenguaje

                // Configura el onClickListener de cada elemento de la lista
                root.setOnClickListener {
                    // Notifica al TareasListener que se ha hecho clic en una tarea
                    tareaListener.onTareaClick(tarea)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewTareasBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tarea = tareas[position]
        holder.bind(tarea)
    }

    override fun getItemCount() = tareas.size

    // Método para actualizar los datos del adaptador
    fun updateData(newTareas: List<Tareas>) {
        tareas.clear()
        tareas.addAll(newTareas)
        notifyDataSetChanged()
    }
}

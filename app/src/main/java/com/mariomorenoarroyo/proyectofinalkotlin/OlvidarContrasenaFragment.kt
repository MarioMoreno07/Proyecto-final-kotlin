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

class OlvidarContrasenaFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_olvidar_contrasena, container, false)

        val restablecerButton: Button = view.findViewById(R.id.enviar)
        val correoElectronicoEditText: EditText = view.findViewById(R.id.correo)

        restablecerButton.setOnClickListener {
            val correoElectronico = correoElectronicoEditText.text.toString().trim()

            if (correoElectronico.isNotEmpty()) {
                // El correo electrónico no está vacío, procede con el restablecimiento
                restablecerContrasena(correoElectronico)
            } else {
                // Muestra un mensaje o realiza alguna acción cuando el correo electrónico esté vacío
                Toast.makeText(context, "Por favor, introduce un correo electrónico", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun restablecerContrasena(correoElectronico: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(correoElectronico)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Éxito al enviar la solicitud, manejar según tus necesidades
                    Toast.makeText(context, "Se ha enviado un correo para restablecer la contraseña", Toast.LENGTH_SHORT).show()
                } else {
                    // Error al enviar la solicitud, manejar según tus necesidades
                    Toast.makeText(context, "Error al enviar la solicitud", Toast.LENGTH_SHORT).show()
                }
            }
    }
}


package com.mariomorenoarroyo.proyectofinalkotlin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mariomorenoarroyo.proyectofinalkotlin.databinding.FragmentRegistrarseBinding

class RegistrarseFragment : Fragment() {

    private lateinit var binding: FragmentRegistrarseBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrarseBinding.inflate(inflater, container, false)
        val view = binding.root

        val btnRegistrarse: Button = view.findViewById(R.id.botonRegistrarse)

        btnRegistrarse.setOnClickListener {
            registrarUsuario()
        }

        return view
    }

    private fun registrarUsuario() {
        val correoElectronico = binding.editTextCorreo.text.toString()

        if (correoElectronico.isNotEmpty() && binding.editTextContrasena.text.isNotEmpty()) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                correoElectronico,
                binding.editTextContrasena.text.toString()
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val email = task.result?.user?.email ?: ""
                    // Guardar los datos en Firestore
                    guardarDatosFirestore(email)
                    showHome(email, ProviderType.BASIC)
                    findNavController().navigate(R.id.action_registrarseFragment_to_iniciarSesionFragment)
                } else {
                    Toast.makeText(context, "Error al registrar el usuario: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarDatosFirestore(email: String) {
        db.collection("users").document(email).set(
            hashMapOf(
                "email" to binding.editTextCorreo.text.toString(),
                "nombre" to binding.editTextNombre.text.toString(),
                "telefono" to binding.editTextPhone.text.toString(),

            )
        ).addOnSuccessListener {
            Toast.makeText(requireContext(), "Usuario registrado", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Error al guardar los datos en Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(requireContext(), MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }
}

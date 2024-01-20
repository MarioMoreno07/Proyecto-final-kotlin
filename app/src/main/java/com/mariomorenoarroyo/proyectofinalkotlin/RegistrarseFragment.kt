package com.mariomorenoarroyo.proyectofinalkotlin


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.mariomorenoarroyo.proyectofinalkotlin.databinding.FragmentRegistrarseBinding



class RegistrarseFragment : Fragment() {

    private lateinit var binding: FragmentRegistrarseBinding

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

        binding.botonRegistrarse.setOnClickListener() {
            if (binding.editTextCorreo.text.isNotEmpty() && binding.editTextContrasena.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.editTextCorreo.text.toString(),
                    binding.editTextContrasena.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                        findNavController().navigate(R.id.action_registrarseFragment_to_iniciarSesionFragment)
                    } else {
                        Toast.makeText(context, "Error al registrar el usuario", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }else{
                Toast.makeText(context, "Error al registrar el usuario", Toast.LENGTH_SHORT)
                    .show()
            }
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

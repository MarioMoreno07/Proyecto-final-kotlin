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
import com.google.firebase.auth.UserProfileChangeRequest

class EditarUsuarioFragment : Fragment() {

    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText


    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_editar_usuario, container, false)

        editTextUsername = view.findViewById(R.id.editTextNombre)
        editTextPassword = view.findViewById(R.id.editTextContrasena)


        val buttonSaveChanges = view.findViewById<Button>(R.id.botonGuardarCambios)
        val buttonCancel = view.findViewById<Button>(R.id.Cancerlar)

        buttonSaveChanges.setOnClickListener { saveChanges() }
        buttonCancel.setOnClickListener { requireActivity().onBackPressed() }

        auth = FirebaseAuth.getInstance()

        return view
    }

    private fun saveChanges() {
        val user = auth.currentUser

        val newUsername = editTextUsername.text.toString()
        val newPassword = editTextPassword.text.toString()


        val profileUpdatesBuilder = UserProfileChangeRequest.Builder()

        if (newUsername.isNotEmpty()) {
            profileUpdatesBuilder.setDisplayName(newUsername)
        }

        val profileUpdates = profileUpdatesBuilder.build()

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Nombre de usuario actualizado", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                } else {
                    Toast.makeText(context, "Error al actualizar el nombre de usuario", Toast.LENGTH_SHORT).show()
                }
            }

        if (newPassword.isNotEmpty()) {
            user?.updatePassword(newPassword)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                        requireActivity().onBackPressed()
                    } else {
                        Toast.makeText(context, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show()
                    }
                }
        }


    }
}


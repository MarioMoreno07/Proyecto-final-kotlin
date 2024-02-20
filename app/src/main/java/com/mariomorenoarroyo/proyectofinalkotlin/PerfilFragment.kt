package com.mariomorenoarroyo.proyectofinalkotlin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PerfilFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()

    companion object {
        private const val GALLERY_REQUEST_CODE = 100
    }

    private lateinit var imageViewProfile: ImageView
    private lateinit var textViewUsername: TextView
    private lateinit var textViewEmail: TextView
    private lateinit var currentUserUid: String
    private lateinit var progressBar: ProgressBar

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        // Obtener el ID del usuario actual
        currentUserUid = FirebaseAuth.getInstance().currentUser?.email ?: ""

        // Referencias a las vistas
        imageViewProfile = view.findViewById(R.id.imageViewProfile)
        textViewUsername = view.findViewById(R.id.textViewUsername)
        textViewEmail = view.findViewById(R.id.textViewEmail)
        val fabAddPhoto = view.findViewById<FloatingActionButton>(R.id.fabAddPhoto)
        val btnDeleteUser = view.findViewById<Button>(R.id.buttonBorrarProfile)
        val btnEditUser = view.findViewById<Button>(R.id.buttonEditProfile)

        // Referencia al ProgressBar
        progressBar = view.findViewById(R.id.progressbar)

        // Mostrar ProgressBar mientras se cargan los datos del perfil
        progressBar.visibility = View.VISIBLE

        // Iniciar una corrutina para introducir un retraso de 2 segundos antes de cargar los datos del perfil
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000) // Retraso de 2 segundos
            cargarDatosPerfil() // Llamada a la función para cargar los datos del perfil
        }

        // Manejador para el click del FloatingActionButton
        fabAddPhoto.setOnClickListener {
            // Crear un Intent para seleccionar una imagen de la galería
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
        }

        // Manejador para el click del botón eliminar usuario
        btnDeleteUser.setOnClickListener {
            deleteCurrentUser()
        }

        btnEditUser.setOnClickListener {
            navigateToEditarUsuarioFragment()
        }

        return view
    }

    private fun cargarDatosPerfil() {
        // Obtener nombre y correo del usuario
        val currentUser = FirebaseAuth.getInstance().currentUser
        val username = currentUser?.displayName
        val email = currentUser?.email

        // Mostrar nombre y correo del usuario
        textViewUsername.text = username
        textViewEmail.text = email

        // Obtener la foto del usuario y cargarla en el ImageView
        val storageRef=FirebaseStorage.getInstance().reference.child("profile_images/${currentUserUid}.jpg")
        storageRef.downloadUrl.addOnSuccessListener {
            Glide.with(this@PerfilFragment /* Context */)
                .load(it)
                .into(imageViewProfile)
            // Ocultar ProgressBar una vez que los datos se han cargado con éxito
            progressBar.visibility = View.GONE
        }.addOnFailureListener {
            // Manejar errores si no se puede obtener la URL de la imagen
            Toast.makeText(requireContext(), "Error al obtener la URL de la imagen de perfil", Toast.LENGTH_SHORT).show()
            // Ocultar ProgressBar en caso de error
            progressBar.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_REQUEST_CODE -> {
                    // Seleccionar imagen de la galería
                    val selectedImageUri = data?.data
                    selectedImageUri?.let { uri ->
                        uploadImageToFirebaseStorage(uri)
                    }
                }
            }
        }
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/${currentUserUid}.jpg")
        val uploadTask = storageRef.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                throw task.exception!!
            }
            // Continuar con la tarea para obtener la URL de la imagen
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                val imageUrl = downloadUri.toString()

                // Actualizar el documento del usuario en Firestore con la URL de la imagen
                updateProfileImageInFirestore(imageUrl)
            } else {
                // Manejar errores si no se puede obtener la URL de la imagen
                Toast.makeText(requireContext(), "Error al obtener la URL de la imagen de perfil", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateProfileImageInFirestore(imageUrl: String) {
        val userRef = db.collection("users").document(currentUserUid)
        userRef.update("foto", imageUrl)
            .addOnSuccessListener {
                // La URL de la imagen se ha guardado exitosamente en Firestore
                // Cargar la imagen en el ImageView usando Glide
                Glide.with(this /* Context */)
                    .load(imageUrl)
                    .into(imageViewProfile)
            }
            .addOnFailureListener { e ->
                // Manejar errores si la actualización falla
                Toast.makeText(requireContext(), "Error al actualizar la imagen de perfil: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteCurrentUser() {
        // Eliminar usuario de Firebase Authentication
        val user = FirebaseAuth.getInstance().currentUser
        user?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Usuario eliminado correctamente
                    Toast.makeText(requireContext(), "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show()

                    // Eliminar documento asociado en Firestore
                    db.collection("users").document(currentUserUid)
                        .delete()
                        .addOnSuccessListener {
                            // Documento eliminado correctamente
                            Toast.makeText(requireContext(), "Documento de usuario eliminado correctamente", Toast.LENGTH_SHORT).show()

                            // Eliminar tareas asociadas al usuario
                            deleteTareasDelUsuario()

                            // Navegar al IniciarSesionFragment después de eliminar el usuario
                            navigateToSignInFragment()
                        }
                        .addOnFailureListener { e ->
                            // Error al eliminar el documento
                            Toast.makeText(requireContext(), "Error al eliminar el documento de usuario: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Error al eliminar el usuario
                    Toast.makeText(requireContext(), "Error al eliminar el usuario", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun deleteTareasDelUsuario() {
        db.collection("tareas").document(currentUserUid).collection("misTareas")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Tarea eliminada correctamente", Toast.LENGTH_SHORT).show()
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

    private fun navigateToSignInFragment() {
        // Obtener el NavController y navegar al IniciarSesionFragment
        findNavController().navigate(R.id.action_perfilFragment_to_IniciarSesionFragment)
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.nav_host_fragment_main, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun navigateToEditarUsuarioFragment() {
        try {
            val editarUsuarioFragment = EditarUsuarioFragment()
            replaceFragment(editarUsuarioFragment)
        } catch (e: Exception) {
            // Manejar la excepción aquí
            Log.e("NavigationError", "Error al navegar a editarUsuarioFragment: ${e.message}")
        }
    }
}

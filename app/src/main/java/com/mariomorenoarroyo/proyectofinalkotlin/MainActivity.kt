package com.mariomorenoarroyo.proyectofinalkotlin

import DetailFragment
import PrimerFragment
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), TareasListener {
    private val listaDeTareas = mutableListOf<Tareas>()
    private lateinit var tareasAdapter: TareasAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar Firebase
        FirebaseApp.initializeApp(this)

        // Inicializar el adaptador y configurar el RecyclerView
        tareasAdapter = TareasAdapter(listaDeTareas, this)

        replaceFragment(PrimerFragment())
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottomNavigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    replaceFragmentWithAnimation(PrimerFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.menu_perfil -> {
                    replaceFragmentWithAnimation(PerfilFragment())
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.menu_logout -> {
                    // Cerrar sesión
                    FirebaseAuth.getInstance().signOut()
                    finish()
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }
        val añadir= findViewById<FloatingActionButton>(R.id.añadir)
        añadir.setOnClickListener {
            replaceFragmentWithAnimation(SegundoFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_main, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun replaceFragmentWithAnimation(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.side_right, // Animación de entrada
                R.anim.side_left, // Animación de salida

            )
            .replace(R.id.nav_host_fragment_main, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun agregarTarea(tarea: Tareas) {
        // Agregar tarea a la lista
        listaDeTareas.add(tarea)

        // Notificar al adaptador que se ha agregado una nueva tarea
        tareasAdapter.updateData(listaDeTareas)
    }

    override fun onTareaClick(tarea: Tareas) {
        // Navegar a DetailFragment
        val detailFragment = DetailFragment.newInstance(tarea)
        replaceFragmentWithAnimation(detailFragment)
    }

    fun navigateToDetailFragment() {
        replaceFragmentWithAnimation(DetailFragment())
    }
}

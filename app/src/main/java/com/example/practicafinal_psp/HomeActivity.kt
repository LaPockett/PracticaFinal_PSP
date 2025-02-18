package com.example.practicafinal_psp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.practicafinal_psp.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //setContentView(R.layout.activity_home)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.menuNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.juego -> fragmentos(Juego())
                R.id.traductor -> fragmentos(Traductor())
                R.id.conexion -> fragmentos(Conexion())
            }
            true
        }
    }

    fun fragmentos(fragmento: Fragment){
        val fragmento_manager = supportFragmentManager
        val transaccion = fragmento_manager.beginTransaction()
        transaccion.replace(R.id.frame_layout, fragmento).commit()
    }

}
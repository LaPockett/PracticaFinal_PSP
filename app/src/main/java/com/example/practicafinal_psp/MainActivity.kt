package com.example.practicafinal_psp

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practicafinal_psp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var textInputEmailLogin: EditText
    lateinit var textInputPasswordLogin: EditText

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //setContentView(R.layout.activity_main)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textInputEmailLogin = binding.textInputEmailLogin
        textInputPasswordLogin = binding.textInputPasswordLogin

    }

    fun onLogin(view: View) {
        val email = textInputEmailLogin.text.toString()
        val contraseña = textInputPasswordLogin.text.toString()

        if (email.isNotEmpty()&&contraseña.isNotEmpty()) {
            //Hasheamos la contraseña


        }else {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
        }

    }
}
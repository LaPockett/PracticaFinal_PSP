package com.example.practicafinal_psp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practicafinal_psp.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    lateinit var textInputEmailLogin: EditText
    lateinit var textInputPasswordLogin: EditText
    //val api:String ="AIzaSyA1YuMbhOpumPGnIsItqmd1oj8Enj_PYb8"

    lateinit var textViewSignUp: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)

        try {
            FirebaseApp.initializeApp(this)
            firebaseAuth = FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error al inicializar FirebaseAuth", e)
        }
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
        textViewSignUp = binding.textViewSignUp

        textViewSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    fun onLogin(view: View) {
        val email:String = binding.textInputEmailLogin.text.toString().trim()
        val password : String = binding.textInputPasswordLogin.text.toString().trim()
        Log.i(TAG, "Email ${email}, Password ${password}")
        if (email.isNotEmpty()&&password.isNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    it.exception?.let { exception ->
                        when (exception) {
                            is FirebaseAuthUserCollisionException ->
                                Toast.makeText(this, "El correo ya está registrado", Toast.LENGTH_SHORT).show()
                            is FirebaseAuthInvalidCredentialsException ->
                                Toast.makeText(this, "La contraseña no es correcta", Toast.LENGTH_SHORT).show()
                            else ->
                                Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        }else {
            Toast.makeText(this,"¡No se permitén campos vacíos!", Toast.LENGTH_SHORT).show()
        }
    }



}
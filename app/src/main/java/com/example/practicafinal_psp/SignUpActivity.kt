package com.example.practicafinal_psp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practicafinal_psp.databinding.ActivitySignUpBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class SignUpActivity : AppCompatActivity() {
    // Firebase Auth
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var binding: ActivitySignUpBinding

    // Botón, email y contraseñas
    lateinit var buttonSignUp : Button
    lateinit var textInputEmail: EditText
    lateinit var textInputPassword: EditText
    lateinit var textInputPassword2: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this) // Inicialización de Firebase
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firebaseAuth = FirebaseAuth.getInstance()
        buttonSignUp = binding.buttonSignUp
        textInputEmail = binding.textInputEmailSignUp
        textInputPassword = binding.textInputPasswordSignUp
        textInputPassword2 = binding.textInputPasswordAgain

    }

    fun onSignUp(view: View) {
        val email:String = binding.textInputEmailSignUp.text.toString().trim()
        val password : String = binding.textInputPasswordSignUp.text.toString().trim()
        val password2 : String = binding.textInputPasswordAgain.text.toString().trim()
        Log.i(TAG, "Email ${email}, Password ${password}, Password2 ${password2},")
        if (email.isNotEmpty()&&password.isNotEmpty()&&password2.isNotEmpty()) {
            if (password.equals(password2)) {
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        it.exception?.let { exception ->
                            when (exception) {
                                is FirebaseAuthUserCollisionException ->
                                    Toast.makeText(this, "El correo ya está registrado", Toast.LENGTH_SHORT).show()
                                is FirebaseAuthWeakPasswordException ->
                                    Toast.makeText(this, "La contraseña es muy débil", Toast.LENGTH_SHORT).show()
                                else ->
                                    Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
                            }
                        }                    }
                }
            } else {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            }

        }else {
            Toast.makeText(this,"No se permiten campos vacíos", Toast.LENGTH_SHORT).show()
        }

    }
}
package com.example.practicafinal_psp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.practicafinal_psp.databinding.FragmentTraductorBinding
import com.google.android.material.button.MaterialButton
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.InputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Traductor.newInstance] factory method to
 * create an instance of this fragment.
 */
class Traductor : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentTraductorBinding
    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner
    private lateinit var idTextoEditable: EditText
    private lateinit var idBotonTraducir: MaterialButton
    private lateinit var textoTraducido: TextView
    private var translator :Translator? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_traductor, container, false)
        binding = FragmentTraductorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Inicialización de elementos del xml
        spinnerFrom = binding.spinnerFrom
        spinnerTo = binding.spinnerTo
        idTextoEditable = binding.idTextoEditable
        idBotonTraducir = binding.idBotonTraducir
        textoTraducido = binding.textoTraducido

        val idiomas = mapOf(
            "Español" to "es",
            "Chino" to "zh",
            "Inglés" to "en"
        )

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, idiomas.keys.toList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFrom.adapter=adapter
        spinnerTo.adapter=adapter

        idBotonTraducir.setOnClickListener {
            val textoIngresado = idTextoEditable.text.toString()
            val idiomaOrigen = spinnerFrom.selectedItem.toString()
            val idiomaDestino = spinnerTo.selectedItem.toString()

            if (textoIngresado.isNotEmpty()) {
                traducirTexto(textoIngresado, idiomaOrigen, idiomaDestino)

            }else{
                Toast.makeText(requireContext(), "Por favor, ingrese un texto", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun traducirTexto(texto: String, idiomaOrigen: String, idiomaDestino: String){
        val idiomaOrigenCodigo = when(idiomaOrigen){
            "Español" -> TranslateLanguage.SPANISH
            "Chino" -> TranslateLanguage.CHINESE
            "Inglés" -> TranslateLanguage.ENGLISH
            else->return
        }

        val idiomaDestinoCodigo = when(idiomaDestino){
            "Español" -> TranslateLanguage.SPANISH
            "Chino" -> TranslateLanguage.CHINESE
            "Inglés" -> TranslateLanguage.ENGLISH
            else->return

        }

        val opciones = TranslatorOptions.Builder()
            .setSourceLanguage(idiomaOrigenCodigo)
            .setTargetLanguage(idiomaDestinoCodigo)
            .build()
        translator = Translation.getClient(opciones)

        translator?.downloadModelIfNeeded()?.addOnSuccessListener {
            //Toast.makeText(requireContext(), "Modelo descargado", Toast.LENGTH_SHORT).show()
            Log.i("Traductor", "Modelo descargado")
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val resultado = translator?.translate(texto)?.await()
                    withContext(Dispatchers.Main){
                        textoTraducido.text = resultado
                    }
                }catch(e: Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(requireContext(), "Error al traducir el texto", Toast.LENGTH_SHORT).show()
                    }

                }
            }

        }?.addOnFailureListener {
            Toast.makeText(requireContext(), "Error al descargar el modelo", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        translator?.close()
    }
}
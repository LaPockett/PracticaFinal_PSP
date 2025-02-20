package com.example.practicafinal_psp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.practicafinal_psp.databinding.FragmentConexionBinding
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.net.SocketException
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Conexion : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentConexionBinding
    private lateinit var textViewCambiante: TextView
    private lateinit var buttonIniciarGame: Button
    private lateinit var buttonEnviarJuego: Button
    private lateinit var textInputRespuesta: TextInputEditText
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var cliente: SSLSocket? = null
    private var flujoEntrada: DataInputStream? = null
    private var flujoSalida: DataOutputStream? = null

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
        binding = FragmentConexionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializamos las variables
        textViewCambiante = binding.textViewCambiante
        buttonIniciarGame = binding.buttonIniciarGame
        buttonEnviarJuego = binding.buttonEnviarJuego
        textInputRespuesta = binding.textInputRespuesta

        // Se deshabilita el botón de enviar respuesta al servidor hasta que inicie el juego
        buttonEnviarJuego.isEnabled = false

        buttonIniciarGame.setOnClickListener {
            iniciarJuego()
        }

        buttonEnviarJuego.setOnClickListener {
            enviarRespuesta()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun enviarRespuesta() {
        coroutineScope.launch(Dispatchers.IO) {

            try {

                val traduccionChino = textInputRespuesta.text.toString()
                flujoSalida?.writeUTF(traduccionChino)
                flujoSalida?.flush()

                // Queremos recibir el resultado del servidor
                val resultado = flujoEntrada?.readUTF()
                launch(Dispatchers.Main){
                    textViewCambiante.text = resultado
                    textInputRespuesta.text?.clear()
                }

            }catch (e: SocketException) {
                launch(Dispatchers.Main) {
                    textViewCambiante.text = "Error de conexión: ${e.message}"
                }
            }catch (e:Exception){
                e.printStackTrace()
                launch(Dispatchers.Main){
                    textViewCambiante.text = "Error al enviar la respuesta: ${e.message}"
                }

            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun iniciarJuego() {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val host = "pspdian.ddns.net" //Cambiar IP ajústandola a la de tu máquina
                val puerto = 6000

                val sslSocketFactory = createSSLSocketFactory(requireContext())
                cliente = sslSocketFactory.createSocket(host, puerto) as SSLSocket
                cliente!!.soTimeout = 10000

                flujoSalida = DataOutputStream(cliente!!.getOutputStream())
                flujoEntrada = DataInputStream(cliente!!.getInputStream())

                flujoSalida!!.writeUTF("Iniciar juego")
                flujoSalida!!.flush()

                val palabraSpanish = flujoEntrada!!.readUTF()

                launch(Dispatchers.Main){
                    textViewCambiante.text = "Traduce la palabra: $palabraSpanish"
                    buttonEnviarJuego.isEnabled = true // Habilitamos el botón de enviar respuesta
                }

            }catch (e:SocketException){
                launch(Dispatchers.Main){
                    textViewCambiante.text = "Error de conexión: ${e.message}"
                }
            }
            catch (e:Exception){
                e.printStackTrace()
                launch(Dispatchers.Main){
                    textViewCambiante.text = "Error al iniciar el juego: ${e.message}"
                }
            }
        }
    }

    private fun createSSLSocketFactory(contexto: Context): SSLSocketFactory {
        try {
            // Cargar el certificado directamente desde "res/raw/"
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val certInputStream:InputStream = contexto.resources.openRawResource(R.raw.servidor_publico)
            val certificado: Certificate = certificateFactory.generateCertificate(certInputStream)
            certInputStream.close()

            // Crear un KeyStore y agregar el certificado del servidor
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null,null)
            keyStore.setCertificateEntry("servidor", certificado)

            // Crear el TrustManager con el certificado
            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(keyStore)

            // Configurar SSLContext con TLS 1.3
            val sslContext = SSLContext.getInstance("TLSv1.3")
            sslContext.init(null, trustManagerFactory.trustManagers, null)

            return sslContext.socketFactory

        }catch (e:Exception) {
            throw RuntimeException("❌ Error al cargar el certificado en Android: ${e.message}", e)
        }

    }

    /**
     * Cancela el ámbito de corrutinas al destruir el fragmento
     */
    override fun onDestroy() {
        super.onDestroy()
        try {
            flujoEntrada?.close()
            flujoSalida?.close()
            cliente?.close()

        }catch (e:Exception){
            Log.e("SSLClient", "Error al cerrar el socket: ${e.message}", e)
        }
        coroutineScope.cancel()
    }
}
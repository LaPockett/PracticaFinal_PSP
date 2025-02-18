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
    private lateinit var buttonEnviar: Button
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

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
        buttonEnviar = binding.buttonEnviar

        buttonEnviar.setOnClickListener {
            enviarMensaje()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun enviarMensaje() {
        coroutineScope.launch(Dispatchers.IO) {
            var cliente: SSLSocket? = null
            var flujoEntrada: DataInputStream? = null
            var flujoSalida: DataOutputStream? = null

            try {
                val host = "192.168.1.23" //Cambiar IP ajústandola a la de tu máquina
                val puerto = 6000

                val sslSocketFactory = createSSLSocketFactory(requireContext())
                cliente = sslSocketFactory.createSocket(host, puerto) as SSLSocket
                cliente.soTimeout = 10000

                try {
                    cliente.startHandshake()
                    Log.d("SSLClient", "Handshake SSL completado correctamente")
                }catch(e:Exception){
                    launch(Dispatchers.Main) {
                        textViewCambiante.text = "Error en el handshake SSL/TLS: ${e.message}"
                    }
                    return@launch
                }
                flujoSalida = DataOutputStream(cliente.getOutputStream())
                flujoEntrada = DataInputStream(cliente.getInputStream())

                flujoSalida.writeUTF("Saludos al SERVIDOR DE DIAN desde el CLIENTE")
                flujoSalida.flush()
                Log.d("SSLClient", "Mensaje enviado al servidor, esperando respuesta...")

                try {
                    val serverResponse = flujoEntrada.readUTF()
                    launch(Dispatchers.Main) {
                        textViewCambiante.text = serverResponse
                        Log.d("SSLClient", "Respuesta recibida: $serverResponse")
                    }
                }catch (e:Exception){
                    Log.e("SSLClient", "Error al leer la respuesta del servidor: ${e.message}", e)
                }

            }catch (e:Exception){
                e.printStackTrace()
            } finally {
                try {
                    flujoEntrada?.close()
                    flujoSalida?.close()
                    cliente?.close()

                }catch (e:SocketException){

                }catch (e:Exception){
                    Log.e("SSLClient", "Error inesperado al cerrar el socket: ${e.message}", e)
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
        coroutineScope.cancel()
    }
}
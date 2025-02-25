package Practica_Final

import kotlinx.coroutines.*
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.EOFException
import java.io.FileInputStream
import java.net.SocketException
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLServerSocket
import javax.net.ssl.SSLSocket
import javax.net.ssl.TrustManagerFactory

class ServidorSSL {
    private val servidorScope = CoroutineScope(Dispatchers.IO)

    fun iniciarServidor() {
        try {
            // CARGAR ALMACÉN
            val keyStore = KeyStore.getInstance("JKS")
            keyStore.load(
                FileInputStream("C:\\Users\\diana\\Desktop\\2 DAM\\Programación de servicios y procesos\\2EV\\PSP_2EV\\servidor_keystore.p12"),
                "1234567".toCharArray()
            )

            // CONFIGURAR ADMINISTRADOR DE CLAVES
            val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            keyManagerFactory.init(keyStore, "1234567".toCharArray())

            // CONFIGURAR ADMINSITRADOR DE CONFIANZA
            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(keyStore)

            // CARGAR CONTEXTO SSL
            val contexto = SSLContext.getInstance("TLS")
            contexto.init(keyManagerFactory.keyManagers, trustManagerFactory.trustManagers, null)

            //CREAR SERVERSOCKET SEGURO
            val servidor = contexto.serverSocketFactory.createServerSocket(6000) as SSLServerSocket
            println("Servidor SSL esperando conexiones en el puerto 6000...")

            while (true) {
                val cliente = servidor.accept() as SSLSocket
                println("*** Cliente conectado ***")

                // Crear una corrutina para manejar el cliente
                servidorScope.launch {
                    manejarCliente(cliente)
                }
            }

        } catch (e: Exception) {
            println("❌ Error en el servidor: ${e.message}")
            e.printStackTrace()
        }
    }

    private suspend fun manejarCliente(cliente: SSLSocket) = withContext(Dispatchers.IO) {
        val pinyin = mapOf(
            "你好" to "nǐ hǎo", "再见" to "zài jiàn", "水" to "shuǐ", "火" to "huǒ",
            "上" to "shàng", "下" to "xià", "超市" to "chāo shì", "饭馆" to "fàn guǎn",
            "钱" to "qián", "钱包" to "qián bāo", "洗手间" to "xǐ shǒu jiān", "儿子" to "ér zi",
            "奴儿" to "nǚ ér", "老公" to "lǎo gōng", "老婆" to "lǎo pó", "猫" to "māo",
            "狗" to "gǒu", "口" to "kǒu", "山" to "shān", "要" to "yào",
            "我爱你" to "wǒ ài nǐ", "真的吗" to "zhēn de ma", "你吃了吗" to "nǐ chī le ma", "你好吗" to "nǐ hǎo ma",
            "我要喝水" to "wǒ yào hē shuǐ", "喝" to "hē", "热" to "rè", "冰" to "bīng",
            "糖" to "táng", "零食" to "líng shí", "美国人" to "měi guó rén", "意大利人" to "yì dà lì rén",
            "韩国人" to "hán guó rén", "本" to "běn", "书" to "shū", "不客气" to "bú kè qì",
            "不" to "bù", "好" to "hǎo", "菜" to "cài", "茶" to "chá",
            "米饭" to "mǐ fàn", "大" to "dà", "小" to "xiǎo", "点" to "diǎn",
            "汉堡包" to "hàn bǎo bāo", "东西" to "dōng xī", "对不起" to "duì bù qǐ", "和" to "hé",
            "很" to "hěn", "叫" to "jiào", "开" to "kāi", "看" to "kàn",
            "老师" to "lǎo shī", "医生" to "yī shēng", "服务员" to "fú wù yuán", "音乐" to "yīn yuè",
            "买" to "mǎi", "没关系" to "méi guān xì", "没有" to "méi yǒu", "明天" to "míng tiān",
            "名字" to "míng zi", "朋友" to "péng yǒu", "漂亮" to "piào liàng", "苹果" to "píng guǒ",
            "去" to "qù", "人" to "rén", "什么" to "shén me", "是" to "shì",
            "水果" to "shuǐ guǒ", "岁" to "suì", "喜欢" to "xǐ huān", "谢谢" to "xiè xiè",
            "学生" to "xué shēng", "学习" to "xué xí", "学校" to "xué xiào", "有" to "yǒu",
            "一" to "yī", "二" to "èr", "三" to "sān", "四" to "sì",
            "五" to "wǔ", "六" to "liù", "七" to "qī", "八" to "bā",
            "九" to "jiǔ", "十" to "shí", "月" to "yuè", "日" to "rì",
            "星期一" to "xīng qī yī", "星期二" to "xīng qī èr", "星期三" to "xīng qī sān", "星期四" to "xīng qī sì",
            "星期五" to "xīng qī wǔ", "星期六" to "xīng qī liù", "星期天" to "xīng qī tiān", "一月" to "yī yuè",
            "二月" to "èr yuè", "三月" to "sān yuè", "四月" to "sì yuè", "五月" to "wǔ yuè",
            "六月" to "liù yuè", "七月" to "qī yuè", "八月" to "bā yuè", "九月" to "jiǔ yuè",
            "十月" to "shí yuè", "十一月" to "shí yī yuè", "十二月" to "shí èr yuè", "今天" to "jīn tiān"
        )
        val palabrasSpanishtoChinese = mapOf(
            "hola" to "你好", "adiós" to "再见", "agua" to "水", "fuego" to "火",
            "arriba" to "上", "abajo" to "下", "supermercado" to "超市", "restaurante" to "饭馆",
            "dinero" to "钱", "billetera" to "钱包", "baño" to "洗手间", "hijo" to "儿子",
            "hija" to "奴儿", "esposo" to "老公", "esposa" to "老婆", "gato" to "猫",
            "perro" to "狗", "boca" to "口", "montaña" to "山", "querer" to "要",
            "te amo" to "我爱你", "¿en serio?" to "真的吗", "¿ya comiste?" to "你吃了吗", "¿estás bien?" to "你好吗",
            "yo quiero beber agua" to "我要喝水", "beber" to "喝", "caliente" to "热", "hielo" to "冰",
            "azúcar" to "糖", "snack" to "零食", "americano" to "美国人", "italiano" to "意大利人",
            "coreano" to "韩国人","libreta" to "本", "libro" to "书", "de nada" to "不客气",
            "no" to "不", "bien" to "好", "comida" to "菜", "té" to "茶",
            "arroz" to "米饭", "grande" to "大", "pequeño" to "小", "reloj" to "点",
            "hamburguesa" to "汉堡包", "cosa" to "东西", "lo siento" to "对不起", "y" to "和",
            "mucho" to "很", "llamar" to "叫", "abrir" to "开", "leer" to "看",
            "profesor" to "老师", "doctor" to "医生", "camarero" to "服务员", "música" to "音乐",
            "comprar" to "买", "no pasa nada" to "没关系", "no hay" to "没有", "mañana" to "明天",
            "nombre" to "名字", "amigo" to "朋友", "hermosa" to "漂亮", "manzana" to "苹果",
            "ir" to "去", "persona" to "人", "qué" to "什么", "ser" to "是",
            "fruta" to "水果", "edad" to "岁", "gustar" to "喜欢", "gracias" to "谢谢",
            "estudiante" to "学生", "estudiar" to "学习", "colegio" to "学校", "tener" to "有",
            "uno" to "一", "dos" to "二", "tres" to "三", "cuatro" to "四",
            "cinco" to "五", "seis" to "六", "siete" to "七", "ocho" to "八",
            "nueve" to "九", "diez" to "十", "luna" to "月", "sol" to "日",
            "lunes" to "星期一", "martes" to "星期二", "miércoles" to "星期三", "jueves" to "星期四",
            "viernes" to "星期五", "sábado" to "星期六", "domingo" to "星期天", "enero" to "一月",
            "febrero" to "二月", "marzo" to "三月", "abril" to "四月", "mayo" to "五月",
            "junio" to "六月", "julio" to "七月", "agosto" to "八月", "septiembre" to "九月",
            "octubre" to "十月", "noviembre" to "十一月", "diciembre" to "十二月", "hoy" to "今天",

            )

        try {
            val entrada = DataInputStream(cliente.inputStream)
            val salida = DataOutputStream(cliente.outputStream)

            while (true) {
                val mensajeCliente = try {
                    entrada.readUTF()
                } catch (e: SocketException) {
                    break
                }

                if (mensajeCliente != "Iniciar juego") {
                    salida.writeUTF("Error: Mensaje no reconocido")
                    salida.flush()
                    continue
                }

                val palabraRandom = palabrasSpanishtoChinese.keys.random()
                val palabraChino = palabrasSpanishtoChinese[palabraRandom]
                val pinyinDePalabraRandom = pinyin[palabraChino]

                salida.writeUTF(palabraRandom)
                salida.flush()

                println("Palabra en español: $palabraRandom")

                val traduccionCliente = entrada.readUTF()
                val respuesta = if (traduccionCliente == palabraChino) "Correcto" else "Incorrecto, es $palabraChino - $pinyinDePalabraRandom"

                salida.writeUTF(respuesta)
                salida.flush()
            }

        } catch (e: EOFException) {
            // Ningún mensaje
        }catch (e: Exception) {
            println("Error manejando cliente: ${e.message}")
            e.printStackTrace()
        } finally {
            try {
                cliente.close()
            } catch (e: Exception) {
                println("Error cerrando conexión: ${e.message}")
            }
        }
    }
}

fun main() {
    val servidor = ServidorSSL()
    servidor.iniciarServidor()
}

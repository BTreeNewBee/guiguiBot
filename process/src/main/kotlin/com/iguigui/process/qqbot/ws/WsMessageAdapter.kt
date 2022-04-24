package com.iguigui.process.qqbot.ws

import com.iguigui.process.qqbot.MessageAdapter
import com.iguigui.process.qqbot.dto.*
import io.ktor.util.*
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap


fun main() {
    val packageName = "com.iguigui.process.qqbot.dto";
    val clazzs = findClass(packageName)
    val map = HashMap<String,Class<*>>()
    clazzs.forEach { it ->
        val annotationsByType = it.getAnnotationsByType(SerialName::class.java)
        if (annotationsByType != null) {
            if (annotationsByType.size != 0) {
                var clazz = it
                while (clazz.superclass != null) {
                    clazz = clazz.superclass
                    print(" --> $clazz")
                }
                println()

                map[annotationsByType[0].value] = it
            }
        }
    }
}


/**
 * 提供直接调用的方法
 * @param packageName
 * @return
 * @throws IOException
 * @throws ClassNotFoundException
 */
@Throws(IOException::class, ClassNotFoundException::class)
fun findClass(packageName: String): List<Class<*>> {
    return findClass(packageName, ArrayList())
}

/**
 *
 * @param packageName
 * @param clazzs
 * @return
 * @throws ClassNotFoundException
 * @throws IOException
 */
@Throws(ClassNotFoundException::class, IOException::class)
private fun findClass(packageName: String, clazzs: MutableList<Class<*>>): List<Class<*>> {
    //将报名替换成目录
    val fileName = packageName.replace("\\.".toRegex(), "/")
    //通过classloader来获取文件列表
    val file = File(Thread.currentThread().contextClassLoader.getResource(fileName).file)
    val files = file.listFiles()
    for (f in files) {
        //如果是目录，这进一个寻找
        if (f.isDirectory) {
            //截取路径最后的文件夹名
            val currentPathName = f.absolutePath.substring(f.absolutePath.lastIndexOf(File.separator) + 1)
            //进一步寻找
            findClass("$packageName.$currentPathName", clazzs)
        } else {
            //如果是class文件
            if (f.name.endsWith(".class")) {
                //反射出实例
                val clazz = Thread.currentThread().contextClassLoader.loadClass(
                    packageName + "." + f.name.replace(
                        ".class",
                        ""
                    )
                )
                clazzs.add(clazz)
            }
        }
    }
    return clazzs
}

@Component
class WsMessageAdapter : MessageAdapter {

    /**
     * Json解析规则，需要注册支持的多态的类
     */
    private val json by lazy {
        Json {
            encodeDefaults = true
            isLenient = true
            ignoreUnknownKeys = true
        }
    }

    @Autowired
    lateinit var qqBotWsClient: QqBotWsClient

    lateinit var handler: (message: BaseResponse) -> Unit

    override fun registerHandler(handler: (message: BaseResponse) -> Unit) {
        this.handler = handler
        qqBotWsClient.registerHandler(this::handlerMessage)
    }

    override fun sendMessage(message: BaseRequest) {
        qqBotWsClient.sendMessage(message.toJson())
    }

    private fun handlerMessage(message: String) {
        this.handler(messageConverter(message))
    }

    private fun messageConverter(message: String): BaseResponse {
        val parseToJsonElement = json.parseToJsonElement(message)
        val command = parseToJsonElement.jsonObject["command"].toString()
//        val baseResponse = json.decodeFromString(BaseResponse::class.serializer(), message)
//        println("baseResponse ${baseResponse.toJson()}")
        return when (command) {
            Paths.reservedMessage -> reservedMessageConverter(message)
            else -> {
                commandMessageConverter(message)
            }
        }
    }


    @OptIn(InternalSerializationApi::class)
    private fun reservedMessageConverter(message: String) : BaseResponse {

        return BaseResponse("", "",null)
    }

    @OptIn(InternalSerializationApi::class)
    private fun commandMessageConverter(message: String) : BaseResponse {

        return BaseResponse("", "",null)
    }

}
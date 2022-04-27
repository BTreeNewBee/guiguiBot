package com.iguigui.process.qqbot.ws

import com.iguigui.process.qqbot.MessageAdapter
import com.iguigui.process.qqbot.dto.*
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.serializer
import org.reflections.Reflections
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File
import java.io.IOException
import java.util.*
import javax.annotation.PostConstruct
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation


//fun main() {
//
//    val wsMessageAdapter = WsMessageAdapter()
//    wsMessageAdapter.registerDTO()
//    wsMessageAdapter.handlerMessage("""{"command":"reservedMessage","data":{"type":"FriendMessage","sender":{"id":123,"nickname":"","remark":""},"messageChain":[{"type":"Source","id":123456,"time":123456},{"type":"At","target":123456,"display":"@Mirai"}]}}""")
//
//}


@Component
class WsMessageAdapter : MessageAdapter {

    var dtoMap = HashMap<String, KClass<out DTO>>()

    //扫描DTO包寻找class注入map中
    @PostConstruct
    fun registerDTO() {
        val packageName = "com.iguigui.process.qqbot.dto";
        val reflections = Reflections(packageName)
        val allClasses: Set<Class<out DTO>> = reflections.getSubTypesOf(DTO::class.java)
        val map = HashMap<String, KClass<out DTO>>()
        allClasses.forEach {
            val javaClass = it.kotlin
            javaClass.findAnnotation<SerialName>()?.let { annotation ->
                map[annotation.value] = javaClass
            }
        }
        dtoMap = map
    }

    /**
     * Json解析规则，需要注册支持的多态的类
     */
    @OptIn(InternalSerializationApi::class)
    private val json by lazy {
        Json {
            encodeDefaults = true
            isLenient = true
            ignoreUnknownKeys = true

            @Suppress("UNCHECKED_CAST")
            serializersModule = SerializersModule {
                polymorphicSealedClass(EventDTO::class, MessagePacketDTO::class)
                polymorphicSealedClass(EventDTO::class, BotEventDTO::class)
            }
        }
    }


    @InternalSerializationApi
    @Suppress("UNCHECKED_CAST")
    private fun <B : Any, S : B> SerializersModuleBuilder.polymorphicSealedClass(
        baseClass: KClass<B>,
        sealedClass: KClass<S>
    ) {
        sealedClass.sealedSubclasses.forEach {
            val c = it as KClass<S>
            polymorphic(baseClass, c, c.serializer())
        }
    }

    @Autowired
    lateinit var qqBotWsClient: QqBotWsClient

    lateinit var handler: (message: DTO) -> Unit

    override fun registerHandler(handler: (message: DTO) -> Unit) {
        this.handler = handler
        qqBotWsClient.registerHandler(this::handlerMessage)
    }

    override fun sendMessage(message: BaseRequest) {
        qqBotWsClient.sendMessage(message.toJson())
    }

     fun handlerMessage(message: String) {
        messageConverter(message)?.let(handler)
    }

    //消息转换，从json string转成DTO
    private fun messageConverter(message: String): DTO? {
        val parseToJsonElement = json.parseToJsonElement(message)
        val command = parseToJsonElement.jsonObject["command"]?.jsonPrimitive?.content.orEmpty()
        return when (command) {
            Paths.reservedMessage -> reservedMessageConverter(parseToJsonElement)
            else -> {
                commandMessageConverter(command, parseToJsonElement)
            }
        }
    }


    @OptIn(InternalSerializationApi::class)
    private fun reservedMessageConverter(message: JsonElement): DTO? {
        message.jsonObject["data"]?.jsonObject?.let { data ->
            val type = data["type"]?.jsonPrimitive?.content.orEmpty()
            val clazz = dtoMap[type]
            val let = clazz?.let { json.decodeFromJsonElement(clazz.serializer(), data) }
            return let
        }
        return null
    }

    @OptIn(InternalSerializationApi::class)
    private fun commandMessageConverter(command: String, message: JsonElement): DTO? {
        message.jsonObject["data"]?.let {
            return@let when (command) {
                Paths.groupList -> {
                    GroupListData(json.decodeFromJsonElement(ListSerializer(GroupDTO.serializer()), it))
                }
                Paths.memberList -> {
                    MemberListData(json.decodeFromJsonElement(ListSerializer(MemberDTO.serializer()), it))
                }
                else -> {
                    return null
                }
            }
        }


        return null
    }


    /**
     * 提供直接调用的方法
     * @param packageName
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Throws(IOException::class, ClassNotFoundException::class)
    private fun findClass(packageName: String): List<KClass<DTO>> {
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
    private fun findClass(packageName: String, clazzs: MutableList<KClass<DTO>>): List<KClass<DTO>> {
        //将报名替换成目录
        val fileName = packageName.replace("\\.".toRegex(), "/")
        //通过classloader来获取文件列表
        val file = File(Thread.currentThread().contextClassLoader.getResource(fileName).file)
        val files = file.listFiles()
        val dtoClazz = DTO::class
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
                    ).kotlin
                    clazzs.add(clazz as KClass<DTO>)
                }
            }
        }
        return clazzs
    }

}
package com.iguigui.qqbot.bot.wechatBot


import com.iguigui.qqbot.bot.Bot
import com.iguigui.qqbot.bot.Contact
import com.iguigui.qqbot.bot.Group
import com.iguigui.qqbot.bot.wechatBot.Constant.Companion.USER_LIST
import com.iguigui.qqbot.service.WechatMessageService
import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http.DefaultHttpHeaders
import io.netty.handler.codec.http.HttpClientCodec
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory
import io.netty.handler.codec.http.websocketx.WebSocketVersion
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler
import io.netty.handler.ssl.SslContext
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import io.netty.handler.stream.ChunkedWriteHandler
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.net.URI
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class WechatBot : Bot {
    @Autowired
    lateinit var wechatMessageService : WechatMessageService

    var groupList: List<Group> = ArrayList()

    var contactList: List<Contact>? = null

    var ctx: ChannelHandlerContext? = null


    fun setChannelHandlerContext(ctx: ChannelHandlerContext) {
        this.ctx = ctx
    }


    override fun getContactById(): Contact {
        TODO("Not yet implemented")
    }

    override fun getGroupById(id: String): Group? {
        groupList.forEach {
            if (it.getId().equals(id)) {
                return it
            }
        }
        return null
    }


    override fun login() {
        var handler: MessageHandler? = null
        val uri = URI("ws://192.168.50.55:5555")
        val scheme = if (uri.scheme == null) "ws" else uri.scheme
        val host = if (uri.host == null) "127.0.0.1" else uri.host
        val port: Int
        port = if (uri.port == -1) {
            if ("ws".equals(scheme, ignoreCase = true)) {
                80
            } else if ("wss".equals(scheme, ignoreCase = true)) {
                443
            } else {
                -1
            }
        } else {
            uri.port
        }
        val ssl = "wss".equals(scheme, ignoreCase = true)
        val sslCtx: SslContext?
        sslCtx = if (ssl) {
            SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build()
        } else {
            null
        }
        handler = MessageHandler(
            WebSocketClientHandshakerFactory.newHandshaker(
                uri, WebSocketVersion.V13, null, true, DefaultHttpHeaders()
            ), this
        )
        val group: EventLoopGroup = NioEventLoopGroup()
        val b = Bootstrap()
        b.group(group)
            .channel(NioSocketChannel::class.java)
            .handler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(ch: SocketChannel) {
                    val p = ch.pipeline()
                    p.addLast(
                        HttpClientCodec(),
                        HttpObjectAggregator(8192),
                        WebSocketClientCompressionHandler.INSTANCE,
                        handler
                    )
                }
            })
        val connect = b.connect(uri.host, port)
    }

    fun loadInfo() {
        send(Json.encodeToString(WeChatMessageDTO(USER_LIST, "null", "user list")))
        println("load info")
    }

    fun processMessage(message: String) {
        wechatMessageService.processMessage(message)
    }

    fun send(message: String) {
        println("send to server $message")
        ctx?.channel()?.writeAndFlush(TextWebSocketFrame(message))
    }

    fun addGroup(group: Group) {
        this.groupList += group
    }

}
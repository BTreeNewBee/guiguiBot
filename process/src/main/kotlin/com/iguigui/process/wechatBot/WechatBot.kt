package com.iguigui.bot.wechatBot



import com.iguigui.bot.wechatBot.Constant.Companion.USER_LIST
import com.iguigui.process.botInterface.Bot
import com.iguigui.process.botInterface.Contact
import com.iguigui.process.botInterface.Group
import com.iguigui.process.service.WechatMessageService
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
import io.netty.handler.timeout.IdleStateHandler
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.net.URI

@Component
class WechatBot : Bot {

    val log = LogFactory.getLog(WechatBot::class.java)!!

    @Autowired
    lateinit var wechatMessageService : WechatMessageService

    val group: EventLoopGroup = NioEventLoopGroup()

    var groups: MutableMap<String, Group> = mutableMapOf()

    var contactList: List<Contact>? = null

    var ctx: ChannelHandlerContext? = null


    fun setChannelHandlerContext(ctx: ChannelHandlerContext) {
        this.ctx = ctx
    }


    override fun getContactById(): Contact {
        TODO("Not yet implemented")
    }

    override fun getGroupById(id: String): Group? {
        return groups[id]
    }


    override fun login() {
        log.info("try connection")
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
                        IdleStateHandler(30,30,30),
                        handler
                    )
                }
            })
        val connect = b.connect(uri.host, port)
        log.info("connection finish")
    }

    fun loadContactInfo() {
        send(Json.encodeToString(WeChatMessageDTO(USER_LIST, "null", "user list")))
        log.info("load info")
    }

    fun processMessage(message: String) {
        wechatMessageService.processMessage(message)
    }

    fun send(message: String) {
        log.info("send to server $message")
        ctx?.channel()?.writeAndFlush(TextWebSocketFrame(message))
    }

    fun addGroup(group: Group) {
        group.getId()?.let {
            this.groups.put(it,group)
        }
    }

}
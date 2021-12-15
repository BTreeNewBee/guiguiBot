package com.iguigui.qqbot.bot.wechatBot


import com.iguigui.qqbot.bot.Bot
import com.iguigui.qqbot.bot.Contact
import com.iguigui.qqbot.bot.Group
import com.iguigui.qqbot.bot.wechatBot.dto.UserListDTO
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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import java.net.URI


class WechatBot : Bot {

    val HEART_BEAT = 5005
    val RECV_TXT_MSG = 1
    val RECV_PIC_MSG = 3
    val RECV_XML_MSG = 49 //其他奇怪类型的内容
    val USER_LIST = 5000
    val GET_USER_LIST_SUCCSESS = 5001
    val GET_USER_LIST_FAIL = 5002
    val TXT_MSG = 555
    val PIC_MSG = 500
    val AT_MSG = 550
    val CHATROOM_MEMBER = 5010//
    val CHATROOM_MEMBER_NICK = 5020
    val PERSONAL_INFO = 6500
    val DEBUG_SWITCH = 6000
    val PERSONAL_DETAIL = 6550
    val DESTROY_ALL = 9999
    val NEW_FRIEND_REQUEST = 37//微信好友请求消息
    val AGREE_TO_FRIEND_REQUEST = 10000//同意微信好友请求消息

    var ctx: ChannelHandlerContext? = null

    fun setChannelHandlerContext(ctx: ChannelHandlerContext) {
        this.ctx = ctx
    }


    override fun getGroups(): List<Group> {
        TODO("Not yet implemented")
    }

    override fun getContacts(): List<Contact> {
        TODO("Not yet implemented")
    }

    override fun getContactById(): Contact {
        TODO("Not yet implemented")
    }

    override fun getGroupById(): Group? {
        TODO("Not yet implemented")
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
        val encodeToString = Json.encodeToString(WeChatMessageDTO(USER_LIST, "null", "user list"))
        ctx?.channel()?.writeAndFlush(TextWebSocketFrame(encodeToString))
    }

    fun processMessage(message: String) {
        val parseToJsonElement = Json.parseToJsonElement(message)
        val type = parseToJsonElement.jsonObject["type"]?.jsonPrimitive?.int
        if (type != HEART_BEAT) {
            println(parseToJsonElement)
        }
        when (parseToJsonElement.jsonObject["type"]?.jsonPrimitive?.int) {
            HEART_BEAT -> {
            }
            GET_USER_LIST_SUCCSESS -> {
                println("GET_USER_LIST_SUCCSESS")
            }
            USER_LIST -> {
                println("RECV_PIC_MSG")
                val userListDTO = Json.decodeFromJsonElement<UserListDTO>(parseToJsonElement)
            }
            RECV_PIC_MSG -> {
                println("RECV_PIC_MSG")
            }
            RECV_TXT_MSG -> {
                println("RECV_TXT_MSG")
            }
            RECV_XML_MSG -> {
                println("RECV_XML_MSG")
            }
            GET_USER_LIST_FAIL -> {
                println("GET_USER_LIST_FAIL")
            }
            CHATROOM_MEMBER -> {
                println("CHATROOM_MEMBER")
            }
            CHATROOM_MEMBER_NICK -> {
                println("CHATROOM_MEMBER_NICK")
            }
            PERSONAL_INFO -> {
                println("PERSONAL_INFO")
            }
            DEBUG_SWITCH -> {
                println("DEBUG_SWITCH")
            }
            PERSONAL_DETAIL -> {
                println("PERSONAL_DETAIL")
            }
            DESTROY_ALL -> {
                println("DESTROY_ALL")
            }
            NEW_FRIEND_REQUEST -> {
                println("NEW_FRIEND_REQUEST")
            }
            AGREE_TO_FRIEND_REQUEST -> {
                println("AGREE_TO_FRIEND_REQUEST")
            }

        }

    }

}
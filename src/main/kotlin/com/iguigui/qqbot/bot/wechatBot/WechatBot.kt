package com.iguigui.qqbot.bot.wechatBot


import com.iguigui.qqbot.bot.Bot
import com.iguigui.qqbot.bot.Contact
import com.iguigui.qqbot.bot.Group
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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import java.net.URI


class WechatBot : Bot {

    var HEART_BEAT = 5005
    var RECV_TXT_MSG = 1
    var RECV_PIC_MSG = 3
    var USER_LIST = 5000
    var GET_USER_LIST_SUCCSESS = 5001
    var GET_USER_LIST_FAIL = 5002
    var TXT_MSG = 555
    var PIC_MSG = 500
    var AT_MSG = 550
    var CHATROOM_MEMBER = 5010//
    var CHATROOM_MEMBER_NICK = 5020
    var PERSONAL_INFO = 6500
    var DEBUG_SWITCH = 6000
    var PERSONAL_DETAIL = 6550
    var DESTROY_ALL = 9999
    var NEW_FRIEND_REQUEST = 37//微信好友请求消息
    var AGREE_TO_FRIEND_REQUEST = 10000//同意微信好友请求消息

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
        println(encodeToString)
        ctx?.channel()?.writeAndFlush(TextWebSocketFrame(encodeToString))
    }

    fun processMessage(message: String) {
        val parseToJsonElement = Json.parseToJsonElement(message)
        println(message)
        val get : JsonElement? = parseToJsonElement.jsonObject.get("type")
        println(get)
        println(get?.javaClass)
        when (1) {
            HEART_BEAT -> {
            }
            GET_USER_LIST_SUCCSESS -> {
                println(message)
            }
            USER_LIST -> {
            }
            RECV_PIC_MSG -> {
            }
            RECV_TXT_MSG -> {
            }
            AT_MSG -> {
            }
            PIC_MSG -> {
            }
            TXT_MSG -> {
            }
            GET_USER_LIST_FAIL -> {
            }
            CHATROOM_MEMBER -> {
            }
            CHATROOM_MEMBER_NICK -> {
            }
            PERSONAL_INFO -> {
            }
            DEBUG_SWITCH -> {
            }
            PERSONAL_DETAIL -> {
            }
            DESTROY_ALL -> {
            }
            NEW_FRIEND_REQUEST -> {
            }
            AGREE_TO_FRIEND_REQUEST -> {
            }

        }
        println(parseToJsonElement)

    }

}
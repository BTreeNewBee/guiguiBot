package com.iguigui.qqbot.bot.wechatBot


import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory
import io.netty.handler.codec.http.websocketx.WebSocketVersion
import io.netty.handler.codec.http.DefaultHttpHeaders
import io.netty.handler.codec.http.HttpClientCodec
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler
import io.netty.handler.ssl.SslContext
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import org.springframework.stereotype.Component
import java.net.URI
import javax.annotation.PostConstruct

fun main() {
    val wechatBotClient = WechatBotClient()
    wechatBotClient.connect()
}


@Component
class WechatBotClient {

    @PostConstruct
    fun connect() {
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
        if (!"ws".equals(scheme, ignoreCase = true) && !"wss".equals(scheme, ignoreCase = true)) {
            System.err.println("Only WS(S) is supported.")
            return
        }
        val ssl = "wss".equals(scheme, ignoreCase = true)
        val sslCtx: SslContext?
        sslCtx = if (ssl) {
            SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build()
        } else {
            null
        }
        val group: EventLoopGroup = NioEventLoopGroup()
        try {
            // Connect with V13 (RFC 6455 aka HyBi-17). You can change it to V08 or V00.
            // If you change it to V00, ping is not supported and remember to change
            // HttpResponseDecoder to WebSocketHttpResponseDecoder in the pipeline.
            val handler = MessageHandler(
                WebSocketClientHandshakerFactory.newHandshaker(
                    uri, WebSocketVersion.V13, null, true, DefaultHttpHeaders()
                )
            )
            val b = Bootstrap()
            b.group(group)
                .channel(NioSocketChannel::class.java)
                .handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel) {
                        val p = ch.pipeline()
                        if (sslCtx != null) {
                            p.addLast(sslCtx.newHandler(ch.alloc(), host, port))
                        }
                        p.addLast(
                            HttpClientCodec(),
                            HttpObjectAggregator(8192),
                            WebSocketClientCompressionHandler.INSTANCE,
                            handler
                        )
                    }
                })
            b.connect(uri.host, port)
        } finally {
        }
    }

}
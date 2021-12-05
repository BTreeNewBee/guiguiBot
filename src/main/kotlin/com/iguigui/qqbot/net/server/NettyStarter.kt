package com.iguigui.qqbot.net.server

import com.iguigui.qqbot.net.channelPool.ChannelPool
import com.iguigui.qqbot.net.protocol.Decode
import com.iguigui.qqbot.net.protocol.Encode
import com.iguigui.qqbot.net.protocol.MessageHandler
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import net.mamoe.mirai.Bot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class NettyStarter {
    val TCP_PORT : Int = 8848

    @Autowired
    lateinit var messageHandler: MessageHandler

    @PostConstruct
    fun starter() {
        var boss = NioEventLoopGroup()
        var worker = NioEventLoopGroup()
        val bootstrap = ServerBootstrap()
        bootstrap.group(boss, worker).channel(NioServerSocketChannel::class.java)
            .option(ChannelOption.SO_BACKLOG, 100)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .childOption(ChannelOption.SO_REUSEADDR, true)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childHandler(object : ChannelInitializer<NioSocketChannel>() {
                @Throws(Exception::class)
                override fun initChannel(nioSocketChannel: NioSocketChannel) {
                    nioSocketChannel.pipeline()
                        .addLast(Decode())
                        .addLast(Encode())
                        .addLast(messageHandler)
                }
            })
        val future: ChannelFuture = bootstrap.bind(TCP_PORT)

        future.addListener(object : ChannelFutureListener {
            @Throws(Exception::class)
            override fun operationComplete(channelFuture: ChannelFuture) {
                if (channelFuture.isSuccess) {
                } else {
                }
            }
        })

    }


}
package com.iguigui.process.net.protocol

import com.iguigui.process.dto.BaseNetData
import com.iguigui.process.net.channelPool.ChannelPool
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
@ChannelHandler.Sharable
class MessageHandler : SimpleChannelInboundHandler<BaseNetData>() {

    @Autowired
    lateinit var channelPool: ChannelPool

    val MAGIC_NUMBER = 0x08080408

    val HEART_BEAT: Byte = 0x01

    val REGISTER_STARTER: Byte = 0x02

    val REGISTER_MONITOR: Byte = 0x03

    val STARTUP: Byte = 0x04

    val REBOOT: Byte = 0x05

    override fun channelRegistered(ctx: ChannelHandlerContext?) {
        ctx!!.channel().eventLoop().scheduleAtFixedRate({
            ctx.channel().writeAndFlush(BaseNetData(MAGIC_NUMBER, HEART_BEAT, 0))
        }, 10, 10, TimeUnit.SECONDS)
    }

    override fun channelRead0(channelHandlerContext: ChannelHandlerContext?, baseNetData: BaseNetData?) {
        when (baseNetData!!.messageType) {
            HEART_BEAT -> {
            }
            REGISTER_STARTER -> {
                channelPool.registerStarter(channelHandlerContext!!.channel())
            }
            REGISTER_MONITOR -> {
                channelPool.registerMonitor(channelHandlerContext!!.channel())
            }
            STARTUP -> {
            }
            REBOOT -> {
            }
        }
    }


    fun startUp() {
        channelPool.getStarter() ?: apply { println("no starter usable!") }
        channelPool.getStarter()?.writeAndFlush(BaseNetData(MAGIC_NUMBER, STARTUP, 0))
    }

    fun reboot() {
        channelPool.getStarter() ?: apply { println("no starter usable!") }
        channelPool.getStarter()?.writeAndFlush(BaseNetData(MAGIC_NUMBER, REBOOT, 0))
    }

}
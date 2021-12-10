package com.iguigui.qqbot.bot.wechatBot

import kotlin.Throws
import io.netty.handler.codec.http.FullHttpResponse
import java.lang.IllegalStateException
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.websocketx.*
import io.netty.util.CharsetUtil
import java.lang.Exception

//@Component
class MessageHandler(private val handshaker: WebSocketClientHandshaker) : SimpleChannelInboundHandler<Any?>() {

    private var handshakeFuture: ChannelPromise? = null
    fun handshakeFuture(): ChannelFuture? {
        return handshakeFuture
    }

    override fun handlerAdded(ctx: ChannelHandlerContext) {
        handshakeFuture = ctx.newPromise()
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        handshaker.handshake(ctx.channel())
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        println("WebSocket Client disconnected!")
    }

    @Throws(Exception::class)
    public override fun channelRead0(ctx: ChannelHandlerContext, msg: Any?) {
        val ch = ctx.channel()
        if (!handshaker.isHandshakeComplete) {
            try {
                handshaker.finishHandshake(ch, msg as FullHttpResponse?)
                println("WebSocket Client connected!")
                handshakeFuture!!.setSuccess()
            } catch (e: WebSocketHandshakeException) {
                println("WebSocket Client failed to connect")
                handshakeFuture!!.setFailure(e)
            }
            return
        }
        if (msg is FullHttpResponse) {
            val response = msg
            throw IllegalStateException(
                "Unexpected FullHttpResponse (getStatus=" + response.status() +
                        ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')'
            )
        }
        val frame = msg as WebSocketFrame?
        if (frame is TextWebSocketFrame) {
            println("WebSocket Client received message: " + frame.text())
        } else if (frame is PongWebSocketFrame) {
            println("WebSocket Client received pong")
        } else if (frame is CloseWebSocketFrame) {
            println("WebSocket Client received closing")
            ch.close()
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        if (!handshakeFuture!!.isDone) {
            handshakeFuture!!.setFailure(cause)
        }
        ctx.close()
    }
}
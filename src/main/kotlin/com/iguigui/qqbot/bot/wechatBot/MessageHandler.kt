package com.iguigui.qqbot.bot.wechatBot

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.websocketx.*
import io.netty.util.CharsetUtil

class MessageHandler(private val handshaker: WebSocketClientHandshaker,private val wechatBot: WechatBot) : SimpleChannelInboundHandler<Any?>() {

    private var handshakeFuture: ChannelPromise? = null

    override fun handlerAdded(ctx: ChannelHandlerContext) {
        handshakeFuture = ctx.newPromise()
        this.wechatBot.setChannelHandlerContext(ctx)
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        handshaker.handshake(ctx.channel())
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        println("WebSocket Client disconnected!")
        wechatBot.login()
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
            wechatBot.processMessage(frame.text())
        } else if (frame is PongWebSocketFrame) {
        } else if (frame is CloseWebSocketFrame) {
            ch.close()
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        if (!handshakeFuture!!.isDone) {
            handshakeFuture!!.setFailure(cause)
        }
        ctx.close()
        wechatBot.login()
    }
}
package com.iguigui.qqbot.net.protocol

import com.iguigui.qqbot.dto.BaseNetData
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.LengthFieldBasedFrameDecoder

class Decode() :
    LengthFieldBasedFrameDecoder(1024, 5, 4) {

    override fun decode(ctx: ChannelHandlerContext?, `in`: ByteBuf?): Any {
        val bytes :ByteBuf = super.decode(ctx, `in`) as ByteBuf
        val magicNumber = bytes.readInt()
        val messageType = bytes.readByte()
        val length = bytes.readInt()
        return BaseNetData(magicNumber,messageType,length)
    }


}
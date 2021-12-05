package com.iguigui.qqbot.net.protocol

import com.iguigui.qqbot.dto.BaseNetData
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class Encode : MessageToByteEncoder<BaseNetData>() {

    override fun encode(channelHandlerContext: ChannelHandlerContext?, baseNetData: BaseNetData?, byteBuf: ByteBuf?) {
        byteBuf!!.writeInt(baseNetData!!.magicNumber)
        byteBuf.writeByte(baseNetData.messageType.toInt())
        byteBuf.writeInt(baseNetData.messageLength)
    }

}
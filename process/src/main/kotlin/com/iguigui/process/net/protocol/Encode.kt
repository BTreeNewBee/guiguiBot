package com.iguigui.process.net.protocol

import com.iguigui.process.dto.BaseNetData
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
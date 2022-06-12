/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package com.iguigui.process.qqbot.dto

import com.iguigui.common.interfaces.DTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class MessagePacketDTO : EventDTO() {
    lateinit var messageChain: MessageChainDTO
}

typealias MessageChainDTO = List<MessageDTO>

@Serializable
@SerialName("FriendMessage")
data class FriendMessagePacketDTO(val sender: QQDTO) : MessagePacketDTO()

@Serializable
@SerialName("FriendSyncMessage")
data class FriendSyncMessagePacketDTO(val subject: QQDTO) : MessagePacketDTO()

@Serializable
@SerialName("GroupMessage")
data class GroupMessagePacketDTO(val sender: MemberDTO) : MessagePacketDTO()

@Serializable
@SerialName("GroupSyncMessage")
data class GroupSyncMessagePacketDTO(val subject: GroupDTO) : MessagePacketDTO()

@Serializable
@SerialName("TempMessage")
data class TempMessagePacketDTO(val sender: MemberDTO) : MessagePacketDTO()

@Serializable
@SerialName("TempSyncMessage")
data class TempSyncMessagePacketDTO(val subject: MemberDTO) : MessagePacketDTO()

@Serializable
@SerialName("StrangerMessage")
data class StrangerMessagePacketDTO(val sender: QQDTO) : MessagePacketDTO()

@Serializable
@SerialName("StrangerSyncMessage")
data class StrangerSyncMessagePacketDTO(val subject: QQDTO) : MessagePacketDTO()

@Serializable
@SerialName("OtherClientMessage")
data class OtherClientMessagePacketDTO(val sender: OtherClientDTO) : MessagePacketDTO()

// Message
@Serializable
@SerialName("Source")
data class MessageSourceDTO(val id: Int, val time: Int) : MessageDTO() {
    override fun toString(): String {
        return ""
    }
}

@Serializable
@SerialName("At")
data class AtDTO(val target: Long, val display: String = "") : MessageDTO() {
    override fun toString(): String {
        return "@$target"
    }
}

@Serializable
@SerialName("AtAll")
data class AtAllDTO(val target: Long = 0) : MessageDTO() {
    override fun toString(): String {
        return "@All"
    }
}

@Serializable
@SerialName("Face")
data class FaceDTO(val faceId: Int = -1, val name: String = "") : MessageDTO() {
    override fun toString(): String {
        return "FaceId $faceId"
    }
}

@Serializable
@SerialName("Plain")
data class PlainDTO(val text: String) : MessageDTO(){
    override fun toString(): String {
        return text
    }
}

internal interface ImageLikeDTO {
    val imageId: String?
    val url: String?
    val path: String?
    val base64: String?
}

internal interface VoiceLikeDTO {
    val voiceId: String?
    val url: String?
    val path: String?
    val base64: String?
    val length: Long
}

@Serializable
@SerialName("Image")
data class ImageDTO(
    override val imageId: String? = null,
    override val url: String? = null,
    override val path: String? = null,
    override val base64: String? = null,
) : MessageDTO(), ImageLikeDTO {
    override fun toString(): String {
        return "图片消息$url"
    }
}

@Serializable
@SerialName("FlashImage")
data class FlashImageDTO(
    override val imageId: String? = null,
    override val url: String? = null,
    override val path: String? = null,
    override val base64: String? = null
) : MessageDTO(), ImageLikeDTO {
    override fun toString(): String {
        return "闪图消息$url"
    }
}

@Serializable
@SerialName("Voice")
data class VoiceDTO(
    override val voiceId: String? = null,
    override val url: String? = null,
    override val path: String? = null,
    override val base64: String? = null,
    override val length: Long = 0L,
) : MessageDTO(), VoiceLikeDTO {
    override fun toString(): String {
        return "语音消息$url"
    }
}

@Serializable
@SerialName("Xml")
data class XmlDTO(val xml: String) : MessageDTO() {
    override fun toString(): String {
        return xml
    }
}

@Serializable
@SerialName("Json")
data class JsonDTO(val json: String) : MessageDTO() {
    override fun toString(): String {
        return json
    }
}

@Serializable
@SerialName("App")
data class AppDTO(val content: String) : MessageDTO() {
    override fun toString(): String {
        return content
    }
}

@Serializable
@SerialName("Quote")
data class QuoteDTO(
    val id: Int,
    val senderId: Long,
    val targetId: Long,
    val groupId: Long,
    val origin: MessageChainDTO
) : MessageDTO() {
    override fun toString(): String {
        return "回复消息 ${origin}"
    }
}


@Serializable
@SerialName("Poke")
data class PokeMessageDTO(
    val name: String
) : MessageDTO() {
    override fun toString(): String {
        return name
    }
}


@Serializable
@SerialName("Dice")
data class DiceDTO(
    val value: Int
) : MessageDTO() {
    override fun toString(): String {
        return "丢了一个骰子结果为{$value}点"
    }
}

@Serializable
@SerialName("MarketFace")
data class MarketFaceDTO(
    val id: Int,
    val name: String,
) : MessageDTO()

@Serializable
@SerialName("MusicShare")
data class MusicShareDTO(
    val kind: String,
    val title: String,
    val summary: String,
    val jumpUrl: String,
    val pictureUrl: String,
    val musicUrl: String,
    val brief: String,
) : MessageDTO() {
    override fun toString(): String {
        return "分享音乐{$title} $jumpUrl"
    }
}

@Serializable
@SerialName("Forward")
data class ForwardMessageDTO(
    val nodeList: List<ForwardMessageNode>
) : MessageDTO() {
    override fun toString(): String {
        return "转发消息"
    }
}

@Serializable
data class ForwardMessageNode(
    val senderId: Long? = null,
    val time: Int? = null,
    val senderName: String? = null,
    val messageChain: MessageChainDTO? = null,
    val messageId: Int? = null,
)

@Serializable
@SerialName("File")
data class FileDTO(
    val id: String,
    val name: String,
    val size: Long,
) : MessageDTO() {
    override fun toString(): String {
        return "文件$name"
    }
}


@Serializable
@SerialName("MiraiCode")
data class MiraiCodeDTO(
    val code: String
) : MessageDTO()

@Serializable
@SerialName("Unknown")
object UnknownMessageDTO : MessageDTO() {
    override fun toString(): String {
        return "未知类型消息"
    }
}

@Serializable
sealed class MessageDTO : DTO


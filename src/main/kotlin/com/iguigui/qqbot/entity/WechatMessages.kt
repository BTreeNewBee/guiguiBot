package com.iguigui.qqbot.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import java.time.LocalDateTime
import java.io.Serializable
/**
 * <p>
 * 
 * </p>
 *
 * @author iguigui
 * @since 2021-12-18
 */
class WechatMessages : BaseEntity() {

    /**
     * 消息类型，0私聊1群聊
     */
    var messageType: Int? = null

    /**
     * 发送方
     */
    var senderWxId: String? = null

    /**
     * 接收方
     */
    var receiverWxId: String? = null

    var groupWxId: String? = null

    /**
     * 发送方昵称
     */
    var senderName: String? = null

    /**
     * 接收方昵称
     */
    var receiverName: String? = null

    var groupName: String? = null

    /**
     * 消息详情
     */
    var messageDetail: String? = null

    var messageId: String? = null


    override fun toString(): String {
        return "WechatMessages{" +
        "id=" + id +
        ", createTime=" + createTime +
        ", creator=" + creator +
        ", modifyTime=" + modifyTime +
        ", modifier=" + modifier +
        ", messageType=" + messageType +
        ", senderWxId=" + senderWxId +
        ", receiverWxId=" + receiverWxId +
        ", groupWxId=" + groupWxId +
        ", senderName=" + senderName +
        ", receiverName=" + receiverName +
        ", groupName=" + groupName +
        ", messageDetail=" + messageDetail +
        ", messageId=" + messageId +
        "}"
    }
}

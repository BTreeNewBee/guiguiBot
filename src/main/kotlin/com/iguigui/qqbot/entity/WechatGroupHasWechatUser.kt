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
class WechatGroupHasWechatUser : BaseEntity() {


    var wechatGroupWxId: String? = null

    var wechatUserWxId: String? = null

    /**
     * 累计统计到的消息数量
     */
    var messageCount: Int? = null

    var nameCard: String? = null

    var nickName: String? = null


    override fun toString(): String {
        return "WechatGroupHasWechatUser{" +
        "id=" + id +
        ", createTime=" + createTime +
        ", creator=" + creator +
        ", modifyTime=" + modifyTime +
        ", modifier=" + modifier +
        ", wechatGroupWxId=" + wechatGroupWxId +
        ", wechatUserWxId=" + wechatUserWxId +
        ", messageCount=" + messageCount +
        ", nameCard=" + nameCard +
        ", nickName=" + nickName +
        "}"
    }
}

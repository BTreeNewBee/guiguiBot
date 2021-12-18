package com.iguigui.qqbot.entity

import com.baomidou.mybatisplus.annotation.IdType
import java.time.LocalDateTime
import com.baomidou.mybatisplus.annotation.TableId
import java.io.Serializable
/**
 * <p>
 * 
 * </p>
 *
 * @author iguigui
 * @since 2021-12-18
 */
class WechatUser : BaseEntity() {

    var wxId: String? = null

    var nickName: String? = null


    override fun toString(): String {
        return "WechatUser{" +
        "id=" + id +
        ", createTime=" + createTime +
        ", creator=" + creator +
        ", modifyTime=" + modifyTime +
        ", modifier=" + modifier +
        ", wxId=" + wxId +
        ", nickName=" + nickName +
        "}"
    }
}

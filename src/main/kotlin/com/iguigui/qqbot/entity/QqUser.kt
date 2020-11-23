package com.iguigui.qqbot.entity

import com.baomidou.mybatisplus.annotation.TableId

/**
 * <p>
 * 
 * </p>
 *
 * @author iguigui
 * @since 2020-11-21
 */
class QqUser : BaseEntity() {

    @TableId(value = "id")
    override var id: Long? = null

    var nickName: String? = null

    override fun toString(): String {
        return "QqUser{" +
        "id=" + id +
        ", createTime=" + createTime +
        ", creator=" + creator +
        ", modifyTime=" + modifyTime +
        ", modifier=" + modifier +
        ", nickName=" + nickName +
        "}"
    }
}

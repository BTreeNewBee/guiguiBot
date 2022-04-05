package com.iguigui.process.entity

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

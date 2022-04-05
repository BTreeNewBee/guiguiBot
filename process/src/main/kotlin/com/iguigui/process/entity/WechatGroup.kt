package com.iguigui.process.entity

/**
 * <p>
 * 
 * </p>
 *
 * @author iguigui
 * @since 2021-12-18
 */
class WechatGroup : BaseEntity() {


    var wxId: String? = null

    /**
     * 群名
     */
    var name: String? = null

    /**
     * 群员数量
     */
    var userCount: Int? = null

    /**
     * 累计统计到的消息数量
     */
    var messageCount: Int? = null


    override fun toString(): String {
        return "WechatGroup{" +
        "id=" + id +
        ", createTime=" + createTime +
        ", creator=" + creator +
        ", modifyTime=" + modifyTime +
        ", modifier=" + modifier +
        ", wxId=" + wxId +
        ", name=" + name +
        ", userCount=" + userCount +
        ", messageCount=" + messageCount +
        "}"
    }
}

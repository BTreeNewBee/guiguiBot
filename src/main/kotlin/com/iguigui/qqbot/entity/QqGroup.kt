package com.iguigui.qqbot.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId


/**
 * <p>
 * 
 * </p>
 *
 * @author iguigui
 * @since 2020-11-21
 */
class QqGroup : BaseEntity() {

    @TableId(value = "id")
    override var id: Long? = null
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
        return "Group{" +
        "id=" + id +
        ", createTime=" + createTime +
        ", creator=" + creator +
        ", modifyTime=" + modifyTime +
        ", modifier=" + modifier +
        ", name=" + name +
        ", userCount=" + userCount +
        ", messageCount=" + messageCount +
        "}"
    }
}

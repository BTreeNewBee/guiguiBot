package com.iguigui.process.entity


/**
 * <p>
 * 
 * </p>
 *
 * @author iguigui
 * @since 2020-11-21
 */
class GroupHasQqUser : BaseEntity() {

    var groupId: Long? = null

    var qqUserId: Long? = null

    /**
     * 累计统计到的消息数量
     */
    var messageCount: Int? = null

    var nameCard: String? = null

    var nickName: String? = null


    override fun toString(): String {
        return "GroupHasQqUser{" +
        "id=" + id +
        ", createTime=" + createTime +
        ", creator=" + creator +
        ", modifyTime=" + modifyTime +
        ", modifier=" + modifier +
        ", groupId=" + groupId +
        ", qqUserId=" + qqUserId +
        ", messageCount=" + messageCount +
        ", nameCard=" + nameCard +
        ", nickName=" + nickName +
        "}"
    }
}

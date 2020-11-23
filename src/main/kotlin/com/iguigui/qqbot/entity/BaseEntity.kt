package com.iguigui.qqbot.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import java.time.LocalDateTime
import java.io.Serializable

open class BaseEntity : Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    open var id: Long? = null

    var createTime: LocalDateTime? = null

    var creator: String? = null

    var modifyTime: LocalDateTime? = null

    var modifier: String? = null

}
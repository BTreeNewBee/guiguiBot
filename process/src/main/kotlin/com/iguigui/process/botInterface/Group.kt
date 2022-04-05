package com.iguigui.process.botInterface

interface Group {
    fun getMemberlist()

    fun sendTextMessage(content: String)

    fun getId() : String?

    fun setId(id :String)

    fun syncGroupMember()

    fun syncGroupMemberNick(it: String)

}
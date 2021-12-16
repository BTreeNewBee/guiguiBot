package com.iguigui.qqbot.bot

interface Bot {
    fun getContactById() : Contact?
    fun getGroupById(id:String) : Group?
    fun login()
}
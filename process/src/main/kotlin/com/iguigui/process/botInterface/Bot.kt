package com.iguigui.process.botInterface

interface Bot {
    fun getContactById() : Contact?
    fun getGroupById(id:String) : Group?
    fun login()
}
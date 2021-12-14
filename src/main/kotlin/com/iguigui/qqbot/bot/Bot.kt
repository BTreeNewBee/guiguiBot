package com.iguigui.qqbot.bot

interface Bot {
    fun getGroups() : List<Group>
    fun getContacts() : List<Contact>
    fun getContactById() : Contact?
    fun getGroupById() : Group?
    fun login()
}
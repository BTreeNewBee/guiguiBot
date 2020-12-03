package com.iguigui.qqbot.service

import net.mamoe.mirai.contact.ContactList
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.MessageRecallEvent
import net.mamoe.mirai.message.FriendMessageEvent
import net.mamoe.mirai.message.GroupMessageEvent

interface MessageService {

    fun processMessage(event : GroupMessageEvent)

    fun processCancelMessage(event : MessageRecallEvent.GroupRecall)

    fun processGroups(groups: ContactList<Group>)

    fun dailyGroupMessageCount()

    fun processFriendMessage(friendMessageEvent: FriendMessageEvent)

}
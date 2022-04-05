package com.iguigui.common.service

import net.mamoe.mirai.contact.ContactList
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MemberCardChangeEvent
import net.mamoe.mirai.event.events.MessageRecallEvent

interface MessageService {

    fun processMessage(event : GroupMessageEvent)

    fun processCancelMessage(event : MessageRecallEvent.GroupRecall)

    fun processGroups(groups: ContactList<Group>)

    fun dailyGroupMessageCount()

    fun processFriendMessage(friendMessageEvent: FriendMessageEvent)

    fun listeningCryptocurrencySchedule()

    fun sendWeather(friend : Friend, city : String)

    fun processMemberCardChangeEvent(memberCardChangeEvent: MemberCardChangeEvent)

}
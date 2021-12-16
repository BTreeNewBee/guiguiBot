package com.iguigui.qqbot.bot.wechatBot

public class Constant {

    companion object {
        const val HEART_BEAT = 5005
        const val RECV_TXT_MSG = 1
        const val RECV_PIC_MSG = 3
        const val RECV_XML_MSG = 49 //其他奇怪类型的内容
        const val USER_LIST = 5000
        const val GET_USER_LIST_SUCCSESS = 5001
        const val GET_USER_LIST_FAIL = 5002
        const val TXT_MSG = 555
        const val PIC_MSG = 500
        const val AT_MSG = 550
        const val CHATROOM_MEMBER = 5010//
        const val CHATROOM_MEMBER_NICK = 5020
        const val PERSONAL_INFO = 6500
        const val DEBUG_SWITCH = 6000
        const val PERSONAL_DETAIL = 6550
        const val DESTROY_ALL = 9999
        const val NEW_FRIEND_REQUEST = 37//微信好友请求消息
        const val AGREE_TO_FRIEND_REQUEST = 10000//同意微信好友请求消息
    }

}
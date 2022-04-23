/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package com.iguigui.process.qqbot.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.event.events.*

@Serializable
sealed class BotEventDTO : EventDTO()

@Serializable
@SerialName("BotOnlineEvent")
data class BotOnlineEventDTO(val qq: Long) : BotEventDTO()

@Serializable
@SerialName("BotOfflineEventActive")
data class BotOfflineEventActiveDTO(val qq: Long) : BotEventDTO()

@Serializable
@SerialName("BotOfflineEventForce")
data class BotOfflineEventForceDTO(
    val qq: Long,
    val title: String,
    val message: String
) : BotEventDTO()

@Serializable
@SerialName("BotOfflineEventDropped")
data class BotOfflineEventDroppedDTO(val qq: Long) : BotEventDTO()

@Suppress("SpellCheckingInspection")
@Serializable
@SerialName("BotReloginEvent")
data class BotReloginEventDTO(val qq: Long) : BotEventDTO()

@Serializable
@SerialName("GroupRecallEvent")
data class GroupRecallEventDTO(
    val authorId: Long,
    val messageId: Int,
    val time: Long,
    val group: GroupDTO,
    val operator: MemberDTO?
) : BotEventDTO()

@Serializable
@SerialName("FriendRecallEvent")
data class FriendRecallEventDTO(
    val authorId: Long,
    val messageId: Int,
    val time: Long,
    val operator: Long
) : BotEventDTO()

@Serializable
@SerialName("BotGroupPermissionChangeEvent")
data class BotGroupPermissionChangeEventDTO(
    val origin: MemberPermission,
    val current: MemberPermission,
    val group: GroupDTO
) : BotEventDTO()

@Serializable
@SerialName("BotMuteEvent")
data class BotMuteEventDTO(
    val durationSeconds: Int,
    val operator: MemberDTO
) : BotEventDTO()

@Serializable
@SerialName("BotUnmuteEvent")
data class BotUnmuteEventDTO(val operator: MemberDTO) : BotEventDTO()

@Serializable
@SerialName("BotJoinGroupEvent")
data class BotJoinGroupEventDTO(
    val group: GroupDTO,
    val invitor: MemberDTO? = null
) : BotEventDTO()

@Serializable
@SerialName("BotLeaveEventActive")
data class BotLeaveEventActiveDTO(
    val group: GroupDTO
) : BotEventDTO()

@Serializable
@SerialName("BotLeaveEventKick")
data class BotLeaveEventKickDTO(
    val group: GroupDTO,
    val operator: MemberDTO
) : BotEventDTO()

@Serializable
@SerialName("GroupNameChangeEvent")
data class GroupNameChangeEventDTO(
    val origin: String,
    val current: String,
    val group: GroupDTO,
    val operator: MemberDTO?
) : BotEventDTO()

@Serializable
@SerialName("GroupEntranceAnnouncementChangeEvent")
data class GroupEntranceAnnouncementChangeEventDTO(
    val origin: String,
    val current: String,
    val group: GroupDTO,
    val operator: MemberDTO?
) : BotEventDTO()

@Serializable
@SerialName("GroupMuteAllEvent")
data class GroupMuteAllEventDTO(
    val origin: Boolean,
    val current: Boolean,
    val group: GroupDTO,
    val operator: MemberDTO?
) : BotEventDTO()

@Serializable
@SerialName("GroupAllowAnonymousChatEvent")
data class GroupAllowAnonymousChatEventDTO(
    val origin: Boolean,
    val current: Boolean,
    val group: GroupDTO,
    val operator: MemberDTO?
) : BotEventDTO()

@Serializable
@SerialName("GroupAllowConfessTalkEvent")
data class GroupAllowConfessTalkEventDTO(
    val origin: Boolean,
    val current: Boolean,
    val group: GroupDTO,
    val isByBot: Boolean
) : BotEventDTO()

@Serializable
@SerialName("GroupAllowMemberInviteEvent")
data class GroupAllowMemberInviteEventDTO(
    val origin: Boolean,
    val current: Boolean,
    val group: GroupDTO,
    val operator: MemberDTO?
) : BotEventDTO()

@Serializable
@SerialName("MemberJoinEvent")
data class MemberJoinEventDTO(
    val member: MemberDTO,
    val invitor: MemberDTO? = null
) : BotEventDTO()

@Serializable
@SerialName("MemberLeaveEventKick")
data class MemberLeaveEventKickDTO(
    val member: MemberDTO,
    val operator: MemberDTO?
) : BotEventDTO()

@Serializable
@SerialName("MemberLeaveEventQuit")
data class MemberLeaveEventQuitDTO(val member: MemberDTO) : BotEventDTO()

@Serializable
@SerialName("MemberCardChangeEvent")
data class MemberCardChangeEventDTO(
    val origin: String,
    val current: String,
    val member: MemberDTO,
) : BotEventDTO()

@Serializable
@SerialName("MemberSpecialTitleChangeEvent")
data class MemberSpecialTitleChangeEventDTO(
    val origin: String,
    val current: String,
    val member: MemberDTO
) : BotEventDTO()

@Serializable
@SerialName("MemberPermissionChangeEvent")
data class MemberPermissionChangeEventDTO(
    val origin: MemberPermission,
    val current: MemberPermission,
    val member: MemberDTO
) : BotEventDTO()

@Serializable
@SerialName("MemberMuteEvent")
data class MemberMuteEventDTO(
    val durationSeconds: Int,
    val member: MemberDTO,
    val operator: MemberDTO?
) : BotEventDTO()

@Serializable
@SerialName("MemberUnmuteEvent")
data class MemberUnmuteEventDTO(
    val member: MemberDTO,
    val operator: MemberDTO?
) : BotEventDTO()

@Serializable
@SerialName("NewFriendRequestEvent")
data class NewFriendRequestEventDTO(
    val eventId: Long,
    val message: String,
    val fromId: Long,
    val groupId: Long,
    val nick: String
) : BotEventDTO()

@Serializable
@SerialName("MemberJoinRequestEvent")
data class MemberJoinRequestEventDTO(
    val eventId: Long,
    val message: String,
    val fromId: Long,
    val groupId: Long,
    val groupName: String,
    val nick: String
) : BotEventDTO()

@Serializable
@SerialName("BotInvitedJoinGroupRequestEvent")
data class BotInvitedJoinGroupRequestEventDTO(
    val eventId: Long,
    val message: String,
    val fromId: Long,
    val groupId: Long,
    val groupName: String,
    val nick: String
) : BotEventDTO()

@Serializable
@SerialName("NudgeEvent")
data class NudgeEventDTO(
    val fromId: Long,
    val target: Long,
    val subject: ComplexSubjectDTO,
    val action: String,
    val suffix: String,
) : BotEventDTO()

@Serializable
@SerialName("FriendInputStatusChangedEvent")
data class FriendInputStatusChangedEventDTO(
    val friend: QQDTO,
    val inputting: Boolean,
) : BotEventDTO()

@Serializable
@SerialName("FriendNickChangedEvent")
data class FriendNickChangedEventDTO(
    val friend: QQDTO,
    val from: String,
    val to: String,
) : BotEventDTO()

@Serializable
@SerialName("MemberHonorChangeEvent")
data class MemberHonorChangeEventDTO(
    val member: MemberDTO,
    val action: String,
    val honor: String,
) : BotEventDTO()

@Serializable
@SerialName("OtherClientOnlineEvent")
data class OtherClientOnlineEventDTO(
    val client: OtherClientDTO,
) : BotEventDTO()

@Serializable
@SerialName("OtherClientOfflineEvent")
data class OtherClientOfflineEventDTO(
    val client: OtherClientDTO,
) : BotEventDTO()

@Serializable
@SerialName("CommandExecutedEvent")
data class CommandExecutedEventDTO(
    val name: String,
    val friend: QQDTO?,
    val member: MemberDTO?,
    val args: MessageChainDTO
) : BotEventDTO()


@Serializable
@SerialName("verifyRet")
data class VerifyRetEventDTO(
    val code: Int,
    val session: String,
) : BotEventDTO()
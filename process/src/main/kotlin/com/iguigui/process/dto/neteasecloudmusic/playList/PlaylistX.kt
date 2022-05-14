package com.iguigui.process.dto.neteasecloudmusic.playList


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistX(
    @SerialName("adType")
    val adType: Int,
    @SerialName("backgroundCoverId")
    val backgroundCoverId: Int,
    @SerialName("cloudTrackCount")
    val cloudTrackCount: Int,
    @SerialName("commentCount")
    val commentCount: Int,
    @SerialName("commentThreadId")
    val commentThreadId: String,
    @SerialName("coverImgId")
    val coverImgId: Long,
    @SerialName("coverImgId_str")
    val coverImgIdStr: String,
    @SerialName("coverImgUrl")
    val coverImgUrl: String,
    @SerialName("createTime")
    val createTime: Long,
    @SerialName("creator")
    val creator: Creator,
    @SerialName("description")
    val description: String,
    @SerialName("highQuality")
    val highQuality: Boolean,
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("newImported")
    val newImported: Boolean,
    @SerialName("opRecommend")
    val opRecommend: Boolean,
    @SerialName("ordered")
    val ordered: Boolean,
    @SerialName("playCount")
    val playCount: Int,
    @SerialName("privacy")
    val privacy: Int,
    @SerialName("shareCount")
    val shareCount: Int,
    @SerialName("specialType")
    val specialType: Int,
    @SerialName("status")
    val status: Int,
    @SerialName("subscribedCount")
    val subscribedCount: Int,
    @SerialName("subscribers")
    val subscribers: List<Subscriber>,
    @SerialName("tags")
    val tags: List<String>,
    @SerialName("titleImage")
    val titleImage: Int,
    @SerialName("trackCount")
    val trackCount: Int,
    @SerialName("trackIds")
    val trackIds: List<TrackId>,
    @SerialName("trackNumberUpdateTime")
    val trackNumberUpdateTime: Long,
    @SerialName("trackUpdateTime")
    val trackUpdateTime: Long,
    @SerialName("tracks")
    val tracks: List<Track>,
    @SerialName("updateTime")
    val updateTime: Long,
    @SerialName("userId")
    val userId: Int
)
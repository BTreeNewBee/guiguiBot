package com.iguigui.qqbot.dto.neteasecloudmusic

import com.alibaba.fastjson.JSONObject
import top.yumbo.util.music.MusicEnum
import top.yumbo.util.music.musicImpl.netease.NeteaseCloudMusicInfo

fun main() {
    MusicEnum.setBASE_URL_163Music("http://192.168.50.185:3000")
    val neteaseCloudMusicInfo = NeteaseCloudMusicInfo()

//    val json = JSONObject()
//    json["id"] = "7008518343"
//    val playlistDetail = neteaseCloudMusicInfo.playlistDetail(json).toJavaObject(PlaylistDetail::class.java)

    val json = JSONObject()
    json["ids"] = "601391"
    val songDetail = neteaseCloudMusicInfo.songDetail(json)
    println(songDetail)
    

}


suspend fun getTrackInfo() {

}
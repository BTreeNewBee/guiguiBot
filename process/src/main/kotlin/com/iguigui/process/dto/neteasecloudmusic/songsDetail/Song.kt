package com.iguigui.process.dto.neteasecloudmusic.songsDetail


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Song(
    @SerialName("al")
    val al: Al,
    @SerialName("alia")
    val alia: List<String>,
    @SerialName("ar")
    val ar: List<Ar>,
    @SerialName("cd")
    val cd: String,
    @SerialName("cf")
    val cf: String,
    @SerialName("copyright")
    val copyright: Int,
    @SerialName("cp")
    val cp: Int,
    @SerialName("djId")
    val djId: Int,
    @SerialName("dt")
    val dt: Int,
    @SerialName("fee")
    val fee: Int,
    @SerialName("ftype")
    val ftype: Int,
    @SerialName("h")
    val h: H,
    @SerialName("id")
    val id: Int,
    @SerialName("l")
    val l: L,
    @SerialName("m")
    val m: M,
    @SerialName("mark")
    val mark: Long,
    @SerialName("mst")
    val mst: Int,
    @SerialName("mv")
    val mv: Int,
    @SerialName("name")
    val name: String,
    @SerialName("no")
    val no: Int,
    @SerialName("originCoverType")
    val originCoverType: Int,
    @SerialName("pop")
    val pop: Int,
    @SerialName("pst")
    val pst: Int,
    @SerialName("publishTime")
    val publishTime: Long,
    @SerialName("resourceState")
    val resourceState: Boolean,
    @SerialName("rt")
    val rt: String,
    @SerialName("rtUrls")
    val rtUrls: List<String>,
    @SerialName("rtype")
    val rtype: Int,
    @SerialName("s_id")
    val sId: Int,
    @SerialName("single")
    val single: Int,
    @SerialName("st")
    val st: Int,
    @SerialName("t")
    val t: Int,
    @SerialName("v")
    val v: Int,
    @SerialName("version")
    val version: Int
)
package com.iguigui.process.dto.neteasecloudmusic.songsDetail


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Privilege(
    @SerialName("chargeInfoList")
    val chargeInfoList: List<ChargeInfo>,
    @SerialName("cp")
    val cp: Int,
    @SerialName("cs")
    val cs: Boolean,
    @SerialName("dl")
    val dl: Int,
    @SerialName("dlLevel")
    val dlLevel: String,
    @SerialName("downloadMaxBrLevel")
    val downloadMaxBrLevel: String,
    @SerialName("downloadMaxbr")
    val downloadMaxbr: Int,
    @SerialName("fee")
    val fee: Int,
    @SerialName("fl")
    val fl: Int,
    @SerialName("flLevel")
    val flLevel: String,
    @SerialName("flag")
    val flag: Int,
    @SerialName("freeTrialPrivilege")
    val freeTrialPrivilege: FreeTrialPrivilege,
    @SerialName("id")
    val id: Int,
    @SerialName("maxBrLevel")
    val maxBrLevel: String,
    @SerialName("maxbr")
    val maxbr: Int,
    @SerialName("payed")
    val payed: Int,
    @SerialName("pl")
    val pl: Int,
    @SerialName("plLevel")
    val plLevel: String,
    @SerialName("playMaxBrLevel")
    val playMaxBrLevel: String,
    @SerialName("playMaxbr")
    val playMaxbr: Int,
    @SerialName("preSell")
    val preSell: Boolean,
    @SerialName("sp")
    val sp: Int,
    @SerialName("st")
    val st: Int,
    @SerialName("subp")
    val subp: Int,
    @SerialName("toast")
    val toast: Boolean
)
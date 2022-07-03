package com.iguigui.process.controller

import com.iguigui.process.dto.BaseResponseDto
import com.iguigui.process.dto.tracks17.WebHookTracks17
import com.iguigui.process.service.ExpressService
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


@RequestMapping("/webHook")
@RestController
class WebHook17Tracks {

    val log = org.slf4j.LoggerFactory.getLogger(WebHook17Tracks::class.java)

    @Value("\${track17Key}")
    lateinit var track17Key: String

    @Autowired
    lateinit var expressService: ExpressService


    @PostMapping("/17TrackNotify")
    fun notify(
        @RequestBody str: String,
        @RequestHeader sign: String
    ): BaseResponseDto<Any> {
        log.info("17TrackNotify $str")
        getGeneratedSignature(str, track17Key).let {
            if (it != sign) {
                return BaseResponseDto(
                    "签名错误",
                    "签名错误",
                    null
                )
            }
        }
        val decodeFromString = Json.decodeFromString(WebHookTracks17.serializer(), str)
        expressService.notify(decodeFromString)
        return BaseResponseDto("ok", "ok", null)
    }


    @Throws(NoSuchAlgorithmException::class)
    private fun getGeneratedSignature(requestText: String, key: String): String? {
        val src = "$requestText/$key"
        val md: MessageDigest = MessageDigest.getInstance("SHA-256")
        val hash: ByteArray = md.digest(src.toByteArray(StandardCharsets.UTF_8))
        val number = BigInteger(1, hash)
        val hexString: StringBuilder = StringBuilder(number.toString(16))
        while (hexString.length < 64) {
            hexString.insert(0, '0')
        }
        return hexString.toString()
    }

}

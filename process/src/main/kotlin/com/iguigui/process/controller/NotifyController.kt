package com.iguigui.process.controller

import com.iguigui.process.dto.BaseResponseDto
import com.iguigui.process.dto.tracks17.WebHookTracks17
import com.iguigui.process.service.ExpressService
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


@RequestMapping("/notify")
@RestController
class NotifyController {

    val log = org.slf4j.LoggerFactory.getLogger(NotifyController::class.java)

    @Autowired
    lateinit var expressService: ExpressService


    @PostMapping("/brandNotify")
    fun notify(
        @RequestBody str: String
    ): BaseResponseDto<Any> {
        log.info("brandNotify $str")
        val decodeFromString = Json.decodeFromString(WebHookTracks17.serializer(), str)
        expressService.notify(decodeFromString)
        return BaseResponseDto("ok", "ok", null)
    }


}

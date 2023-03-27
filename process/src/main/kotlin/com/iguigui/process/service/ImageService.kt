package com.iguigui.process.service

import cn.hutool.http.HttpUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct


@Component
class ImageService {

    //baseFilePath
    @Value("\${baseFilePath}")
    lateinit var baseFilePath: String


    fun getAvatarImage(id: Long): String {
        val file = java.io.File("$baseFilePath/avatar/$id.jpg")
        if (file.exists()) {
            return file.absolutePath
        }
        //下载图片
        val url = "http://q1.qlogo.cn/g?b=qq&nk=$id&s=100"
        HttpUtil.downloadFile(url, file)
        return file.absolutePath
    }

}
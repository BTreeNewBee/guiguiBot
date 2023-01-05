package com.iguigui.process.imagegenerator

import com.ruiyun.jvppeteer.core.Puppeteer
import com.ruiyun.jvppeteer.core.browser.Browser
import com.ruiyun.jvppeteer.core.browser.BrowserFetcher
import com.ruiyun.jvppeteer.options.LaunchOptions
import com.ruiyun.jvppeteer.options.LaunchOptionsBuilder
import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.io.File

@Component
class Config {


    @Value("\${templateFilePath}")
    lateinit var templateFilePath: String

    @Value("\${tmpFilePath}")
    lateinit var tmpFilePath: String

    @Bean
    fun freemarkerConfig() : Configuration {
        val cfg = Configuration(Configuration.VERSION_2_3_31)
        cfg.setDirectoryForTemplateLoading(File(templateFilePath))
        cfg.defaultEncoding = "UTF-8"
        cfg.templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
        cfg.logTemplateExceptions = false
        cfg.wrapUncheckedExceptions = true
        cfg.fallbackOnNullLoopVariable = false
        return cfg
    }


    @Bean
    fun browser() : Browser{
        BrowserFetcher.downloadIfNotExist(null)
        val arrayList = ArrayList<String>()
        val options: LaunchOptions = LaunchOptionsBuilder().withArgs(arrayList).withHeadless(true).build()
        arrayList.add("--no-sandbox")
        arrayList.add("--disable-setuid-sandbox")
        val browser: Browser = Puppeteer.launch(options)
        return browser
    }


}
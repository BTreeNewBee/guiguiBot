package com.iguigui.process.imagegenerator

import com.ruiyun.jvppeteer.core.browser.Browser
import com.ruiyun.jvppeteer.core.page.Page
import com.ruiyun.jvppeteer.options.Clip
import com.ruiyun.jvppeteer.options.ScreenshotOptions
import com.ruiyun.jvppeteer.options.Viewport
import freemarker.template.Configuration
import freemarker.template.Template
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.concurrent.atomic.AtomicInteger
import javax.annotation.Resource

@Service
class GeneratorService {

    @Autowired
    lateinit var config: Config

    @Autowired
    lateinit var browser: Browser

    @Resource(name = "freemarkerConfig")
    lateinit var freemarkerConfiguration: Configuration

    val index = AtomicInteger(0)

    fun generateImage(
        templateName: String,
        data: Any,
        iamgeWidth: Int = 500,
        imageHeight: Int = 800,
    ): File {
        val imageHeightTmp = if (data is String) {
            getHeight(data) * 1.1
        } else {
            imageHeight * 1.1
        }
        val dataTmp: Any = when (data) {
            is String -> {
                mapOf("content" to data)
            }

            is List<*> -> {
                mapOf("list" to data)
            }
            else -> {
                data
            }
        }
        val fileIndex = index.incrementAndGet()
        //生成html
        val template: Template = freemarkerConfiguration.getTemplate(templateName)
        val htmlFile = File(config.tmpFilePath, "tmpFile${System.currentTimeMillis()}_$fileIndex.html")
        FileOutputStream(htmlFile).use { fos ->
            OutputStreamWriter(fos).use { osw ->
                template.process(dataTmp, osw)
            }
        }
        //打开HTML
        val page: Page = browser.newPage()
        println(htmlFile.absolutePath)
        page.goTo("file://" + htmlFile.absolutePath)
        page.setViewport(Viewport(iamgeWidth, imageHeightTmp.toInt(), 2.0, false, false, false))
        //执行截图
        val image = File(config.tmpFilePath, "tmpFile${System.currentTimeMillis()}_$fileIndex.png")
        val screenshotOptions = ScreenshotOptions()
        val clip = Clip(0.0, 0.0, iamgeWidth.toDouble(), imageHeightTmp)
        screenshotOptions.clip = clip
        screenshotOptions.path = image.absolutePath
        page.screenshot(screenshotOptions)

        //清理资源
        page.close()
//        htmlFile.delete()
        return image
    }

    private fun getHeight(content: String): Int {
        val split = content.split("\n")
        return 20 + 20 * split.size
    }

}
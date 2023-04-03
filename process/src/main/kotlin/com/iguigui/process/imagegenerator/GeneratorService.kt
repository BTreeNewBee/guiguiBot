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
        iamgeWidth: Int = 800,
        imageHeight: Int = 500,
        viewWidth: Int = iamgeWidth,
        viewHeight: Int = imageHeight
    ): File {
        val fileIndex = index.incrementAndGet()
        //生成html
        val template: Template = freemarkerConfiguration.getTemplate(templateName)
        val htmlFile = File(config.tmpFilePath, "tmpFile${System.currentTimeMillis()}-$fileIndex.html")
        FileOutputStream(htmlFile).use { fos ->
            OutputStreamWriter(fos).use { osw ->
                template.process(data, osw)
            }
        }
        //打开HTML
        val page: Page = browser.newPage()
        page.goTo(htmlFile.absolutePath)
        page.setViewport(Viewport(viewWidth, viewHeight, 4.0, false, false, false))
        //执行截图
        val image = File(config.tmpFilePath, "tmpFile${System.currentTimeMillis()}-$fileIndex.png")
        val screenshotOptions = ScreenshotOptions()
        val clip = Clip(0.0, 0.0, iamgeWidth.toDouble(), imageHeight.toDouble())
        screenshotOptions.clip = clip
        screenshotOptions.path = image.absolutePath
        page.screenshot(screenshotOptions)
        
        //清理资源
        page.close()
//        htmlFile.delete()
        return image
    }

}
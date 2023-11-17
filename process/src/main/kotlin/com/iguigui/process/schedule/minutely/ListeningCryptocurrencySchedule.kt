package com.iguigui.process.schedule.minutely

import org.apache.logging.log4j.kotlin.logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ListeningCryptocurrencySchedule {

    @Scheduled(cron = "*/5 * * * * ?")
    fun listeningCryptocurrencySchedule() {
//        logger.trace("entry");    //trace级别的信息，和logger.entry() 基本一个意思，但已经过时
//        logger.debug("我是debug信息");
//        logger.info("我是info信息");
//        logger.warn("我是warning信息");
//        logger.error("我是error信息");
//        logger.fatal("我是fatal信息");
//        logger.trace("exit");// //和entry()对应的结束方法，和logger.exit()一个意思,同样已经过时

    }

}
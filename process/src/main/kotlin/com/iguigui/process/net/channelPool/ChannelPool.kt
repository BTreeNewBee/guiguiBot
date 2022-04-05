package com.iguigui.process.net.channelPool

import io.netty.channel.Channel
import org.springframework.stereotype.Component

@Component
class ChannelPool {

    private var starter : Channel? = null

    private var monitor : Channel? = null

    fun registerStarter(channel : Channel) {
        starter?.apply { println("more than one starter register!") }
        starter?.close()
        println("new starter register")
        starter = channel
    }

    fun registerMonitor(channel : Channel) {
        starter?.apply { println("more than one monitor register!") }
        monitor?.close()
        println("new monitor register")
        monitor = channel
    }

    fun getStarter(): Channel? {
        return starter
    }

    fun getMonitor(): Channel? {
        return monitor
    }

}
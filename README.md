# guiguiBot
基于Kotlin书写,通过WS协议调用Mirai-HTTP接口实现的一个娱乐机器人.
基本上都是个人需求,可供参考实现.

使用到的主要组件和技术
JDK11
Kotlin 1.6.10
Gradle 6.5.1
SpringBoot 2.4.0
MyBatis-plus 3.4.1
MySQL 8.0 
MongoDB 5.0.7
Mirai-HTTP(进行了个人改造)
KSP(Kotlin Symbol Processing)


之前基于Mirai原生开发，现改成Mirai-HTTP WS接口开发.
功能清单
1. 群消息收集,包含几乎所有类型的消息都存.
2. 每日群成员消息量统计日报.
3. 实时成员消息量查询.
4. 撤回消息自动重发,支持几乎所有类型的消息重发,包括图片语音分享卡片等等.
5. 闪图重发.
6. 搜索辅助.
7. 网易云点歌台,基于此项目实现[NeteaseCloudMusicApi](https://github.com/Binaryify/NeteaseCloudMusicApi).
8. 快递订阅,帮助查询快递信息进行推送(嫖的百度快递接口).
9. 图片自动下载存储.
10. 群名片修改提醒.

Mirai WS会对所有输出对象进行如下封装

    @Serializable
    internal data class WsOutgoing(
        val syncId: String?,
        val data: JsonElement
    ) : DTO

syncId为此次输出对应的输入的ID，例如进行一次获取groupList，syncId = 1，则你会收到一个 syncId = 1 的响应对象，data里的内容即为groupList信息。

小麻烦在于data的类型是JsonElement，这里可能是一个Object (或者说JsonObject)也可能是一个List<Object> (或者说JsonArray)，于是产生了解析上的麻烦。

另一个麻烦的点在于WsOutgoing这个对象缺少一个type,不能标记这次消息的具体类型(当然很多类型在data里面是有type的).
这个问题在查询groupList,memberList的时候尤其糟糕,这种时候data会是一个list而不是一个object,在这种情况下只能通过syncId自行进行关联,查询这次响应是对应哪一次请求,再得以知道对应的类型.
我对Mirai-HTTP插件进行了一点微调,给所有的WsOutgoing增加了type来使用辅助解析.


~~wechat依赖此项目实现[wechat-bot](https://github.com/cixingguangming55555/wechat-bot)
微信啥辣鸡体验给我砍了~~

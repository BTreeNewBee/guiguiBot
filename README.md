# guiguiBot
需要以下依赖
JDK11
Kotlin 1.6.10
Gradle 6.5.1
SpringBoot 2.4.0
MyBatis-plus 3.4.1
MySQL 8.0 

曾经基于Mirai原生开发，现在全部改成Mirai-HTTP WS接口开发

Mirai WS会对所有输出对象进行如下封装

    @Serializable
    internal data class WsOutgoing(
        val syncId: String?,
        val data: JsonElement
    ) : DTO

syncId为此次输出对应的输入的ID，例如进行一次获取groupList，syncId = 1，则你会收到一个 syncId = 1 的响应对象，data里的内容即为groupList信息。

小麻烦在于data的类型是JsonElement，这里可能是一个Object (或者说JsonObject)也可能是一个List<Object> (或者说JsonArray)，于是产生了解析上的麻烦。




wechat依赖此项目实现[wechat-bot](https://github.com/cixingguangming55555/wechat-bot)

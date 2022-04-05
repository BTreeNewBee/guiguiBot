package com.iguigui.process.dao;

import com.iguigui.process.entity.WechatMessages;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iguigui.process.entity.WechatGroupMessageCountEntity
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author iguigui
 * @since 2021-12-18
 */
@Mapper
interface WechatMessagesMapper : BaseMapper<WechatMessages> {

    //查询指定群在指定时间内消息发送量最高的前十名
    @Select("select sender_wx_id as userWxId , count(*) as messageCount from wechat_messages where create_time between #{startTime} and #{endTime} and group_wx_id = #{groupWxId} group by sender_wx_id order by messageCount desc limit 10")
    fun getDailyGroupMessageCount(@Param("startTime") startTime: String,
                                  @Param("endTime") endTime: String,
                                  @Param("groupWxId") groupWxId: String): List<WechatGroupMessageCountEntity>

    @Select("select count(*) as count from wechat_messages where create_time between #{startTime} and #{endTime} and group_wx_id = #{groupWxId}")
    fun getDailyGroupMessageSum(@Param("startTime")startTime: String,
                                @Param("endTime") endTime: String,
                                @Param("groupWxId") groupWxId: String): Int

}

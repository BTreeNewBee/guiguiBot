package com.iguigui.process.dao;

import com.iguigui.process.entity.Messages;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iguigui.process.entity.GroupMessageCountEntity
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author iguigui
 * @since 2020-11-21
 */
@Mapper
interface MessagesMapper : BaseMapper<Messages> {

    //查询指定群在指定时间内消息发送量最高的前十名
    @Select("select sender_id as qqUserId , count(*) as messageCount from messages where create_time between #{startTime} and #{endTime} and group_id = #{groupId} group by sender_id order by messageCount desc limit 10")
    fun getDailyGroupMessageCount(@Param("startTime") startTime: String,
                                  @Param("endTime") endTime: String,
                                  @Param("groupId") groupId: Long): List<GroupMessageCountEntity>

    @Select("select count(*) as count from messages where create_time between #{startTime} and #{endTime} and group_id = #{groupId}")
    fun getDailyGroupMessageSum(@Param("startTime")startTime: String,
                                @Param("endTime") endTime: String,
                                @Param("groupId") groupId: Long): Int

    @Select("select * from messages where create_time between #{startTime} and #{endTime} and group_id = #{groupId} and message_id = #{messageId} limit 1")
    fun getMessageByMessageId(@Param("startTime")startTime: String,
                              @Param("endTime") endTime: String,
                              @Param("messageId")messageId: Int,
                              @Param("groupId") groupId: Long): Messages?

}

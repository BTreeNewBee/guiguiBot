package com.iguigui.qqbot.dao;

import com.iguigui.qqbot.entity.GroupHasQqUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
interface GroupHasQqUserMapper : BaseMapper<GroupHasQqUser> {

    @Select("select * from group_has_qq_user where group_id = #{groupId}")
    fun selectByGroupId(@Param("groupId") groupId: Long): List<GroupHasQqUser>


    @Select("select * from group_has_qq_user where group_id = #{groupId} and qq_user_id = #{qqUserId}")
    fun selectByGroupIdAndQqUserId(@Param("groupId") groupId: Long,@Param("qqUserId") qqUserId: Long): GroupHasQqUser

}

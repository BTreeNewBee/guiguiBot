package com.iguigui.process.dao;

import com.iguigui.process.entity.WechatGroupHasWechatUser;
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
 * @since 2021-12-18
 */
@Mapper
interface WechatGroupHasWechatUserMapper : BaseMapper<WechatGroupHasWechatUser> {

    @Select("select * from wechat_group_has_wechat_user where wechat_group_wx_id = #{groupWxId} and wechat_user_wx_id = #{userWxId}")
    fun selectByGroupIdAndUserId(@Param("groupWxId") groupWxId: String, @Param("userWxId") userWxId: String): WechatGroupHasWechatUser?

    @Select("select * from wechat_group_has_wechat_user where wechat_group_wx_id = #{groupWxId} ")
    fun selectByGroupId(@Param("groupWxId") groupWxId: String): List<WechatGroupHasWechatUser>


}

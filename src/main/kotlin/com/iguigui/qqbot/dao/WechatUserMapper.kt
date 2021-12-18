package com.iguigui.qqbot.dao;

import com.iguigui.qqbot.entity.WechatUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author iguigui
 * @since 2021-12-18
 */
@Mapper
interface WechatUserMapper : BaseMapper<WechatUser>

package com.iguigui.qqbot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iguigui.qqbot.entity.QqGroup
import org.apache.ibatis.annotations.Mapper

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author iguigui
 * @since 2020-11-21
 */
@Mapper
interface QqGroupMapper : BaseMapper<QqGroup>

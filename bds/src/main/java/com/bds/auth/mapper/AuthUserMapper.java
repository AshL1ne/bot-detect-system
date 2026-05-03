package com.bds.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bds.auth.entity.AuthUserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthUserMapper extends BaseMapper<AuthUserEntity> {
}


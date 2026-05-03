package com.bds.tweet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bds.tweet.entity.TweetEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TweetMapper extends BaseMapper<TweetEntity> {
}


package com.bds.tweet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bds.common.PageResult;
import com.bds.tweet.dto.TweetQuery;
import com.bds.tweet.entity.TweetEntity;
import com.bds.tweet.mapper.TweetMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TweetService {
	private final TweetMapper tweetMapper;

	public TweetService(TweetMapper tweetMapper) {
		this.tweetMapper = tweetMapper;
	}

	public PageResult<TweetEntity> pageTweets(TweetQuery query) {
		String userId = query.getUserId();
		if (userId == null || userId.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
		}
		LambdaQueryWrapper<TweetEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(TweetEntity::getUserId, userId)
				.orderByDesc(TweetEntity::getCreatedAt);
		Page<TweetEntity> page = new Page<>(query.getPageNum(), query.getPageSize());
		Page<TweetEntity> result = tweetMapper.selectPage(page, wrapper);
		return new PageResult<>(result.getRecords(), result.getTotal(), query.getPageNum(), query.getPageSize());
	}
}


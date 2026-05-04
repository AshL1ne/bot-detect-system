package com.bds.profile.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bds.profile.dto.ActiveHourDTO;
import com.bds.profile.dto.WordCloudDTO;
import com.bds.profile.util.WordCloudGenerator;
import com.bds.tweet.dto.TweetHourActivityRow;
import com.bds.tweet.entity.TweetEntity;
import com.bds.tweet.mapper.TweetMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserProfileService {
	private static final int WORD_CLOUD_MAX_TWEETS = 1000;
	private static final int WORD_CLOUD_TOP_N = 120;

	private final TweetMapper tweetMapper;

	public UserProfileService(TweetMapper tweetMapper) {
		this.tweetMapper = tweetMapper;
	}

	public List<WordCloudDTO> getWordCloud(String userId) {
		requireUserId(userId);
		LambdaQueryWrapper<TweetEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(TweetEntity::getUserId, userId)
				.orderByDesc(TweetEntity::getCreatedAt)
				.last("LIMIT " + WORD_CLOUD_MAX_TWEETS);
		List<TweetEntity> tweets = tweetMapper.selectList(wrapper);
		List<String> corpus = new ArrayList<>();
		for (TweetEntity t : tweets) {
			if (t.getContent() != null && !t.getContent().isBlank()) {
				corpus.add(t.getContent());
			}
			if (Boolean.TRUE.equals(t.getIsRetweet())
					&& t.getRtOriginContent() != null
					&& !t.getRtOriginContent().isBlank()) {
				corpus.add(t.getRtOriginContent());
			}
		}
		return WordCloudGenerator.topWords(corpus, WORD_CLOUD_TOP_N);
	}

	public List<ActiveHourDTO> getActiveHours(String userId) {
		requireUserId(userId);
		List<TweetHourActivityRow> rows = tweetMapper.selectHourActivity(userId);
		Map<Integer, TweetHourActivityRow> bySlot = new HashMap<>();
		for (TweetHourActivityRow row : rows) {
			if (row.getPeriodSlot() != null) {
				bySlot.put(row.getPeriodSlot(), row);
			}
		}
		List<ActiveHourDTO> result = new ArrayList<>(12);
		for (int slot = 0; slot < 12; slot++) {
			TweetHourActivityRow row = bySlot.get(slot);
			ActiveHourDTO dto = new ActiveHourDTO();
			dto.setPeriodStartHour(slot * 2);
			if (row != null) {
				dto.setOriginalCount(nz(row.getOriginalCount()));
				dto.setRetweetCount(nz(row.getRetweetCount()));
				dto.setTotalCount(nz(row.getTotalCount()));
			} else {
				dto.setOriginalCount(0L);
				dto.setRetweetCount(0L);
				dto.setTotalCount(0L);
			}
			result.add(dto);
		}
		return result;
	}

	private static long nz(Long v) {
		return v == null ? 0L : v;
	}

	private static void requireUserId(String userId) {
		if (userId == null || userId.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
		}
	}
}


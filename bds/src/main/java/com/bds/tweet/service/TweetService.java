package com.bds.tweet.service;

import com.bds.tweet.dto.TweetQuery;
import com.bds.tweet.entity.TweetEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class TweetService {
	public List<TweetEntity> listTweets(TweetQuery query) {
		// TODO: implement tweet paging by user
		return Collections.emptyList();
	}
}


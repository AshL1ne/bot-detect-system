package com.bds.tweet.controller;

import com.bds.common.PageResult;
import com.bds.common.Result;
import com.bds.tweet.dto.TweetQuery;
import com.bds.tweet.entity.TweetEntity;
import com.bds.tweet.service.TweetService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tweets")
public class TweetController {
	private final TweetService tweetService;

	public TweetController(TweetService tweetService) {
		this.tweetService = tweetService;
	}

	@PostMapping("/search")
	public Result<PageResult<TweetEntity>> pageTweets(@RequestBody TweetQuery query) {
		return Result.success(tweetService.pageTweets(query));
	}
}


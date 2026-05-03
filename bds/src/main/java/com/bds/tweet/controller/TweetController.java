package com.bds.tweet.controller;

import com.bds.common.Result;
import com.bds.tweet.dto.TweetQuery;
import com.bds.tweet.entity.TweetEntity;
import com.bds.tweet.service.TweetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tweets")
public class TweetController {
	private final TweetService tweetService;

	public TweetController(TweetService tweetService) {
		this.tweetService = tweetService;
	}

	@PostMapping("/search")
	public Result<List<TweetEntity>> listTweets(@RequestBody TweetQuery query) {
		return Result.success(tweetService.listTweets(query));
	}
}


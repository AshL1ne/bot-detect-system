package com.bds.tweet.dto;

import com.bds.common.QueryPageParam;
import lombok.Data;

@Data
public class TweetQuery extends QueryPageParam {
	private String userId;
}


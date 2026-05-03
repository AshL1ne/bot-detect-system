package com.bds.tweet.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tweets")
public class TweetEntity {
	@TableId
	private String tweetId;
	private String userId;
	private LocalDateTime createdAt;
	private Integer repostsCount;
	private Integer attitudesCount;
	private String content;
	private Boolean isRetweet;
	private Boolean isLongText;
	private String rtOriginTweetId;
	private String rtOriginUserId;
	private String rtOriginContent;
	private Boolean rtOriginIsLongText;
}


package com.bds.user.dto;

import lombok.Data;

@Data
public class UserDetailVO {
	private String userId;
	private String nickName;
	private String description;
	private Integer gender;
	private Long followersCount;
	private Integer followCount;
	private Integer statusesCount;
	private Boolean verified;
	private Integer mbrank;
	private Integer mbtype;
	private Integer originalCount;
	private Integer forwardCount;
	private Double malProb;
	private Boolean isMalicious;
}


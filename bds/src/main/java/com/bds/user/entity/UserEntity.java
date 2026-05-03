package com.bds.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("users")
public class UserEntity {
	@TableId
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


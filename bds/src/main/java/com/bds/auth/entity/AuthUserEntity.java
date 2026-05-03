package com.bds.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("auth_users")
public class AuthUserEntity {
	@TableId(type = IdType.ASSIGN_ID)
	private String id;
	private String username;
	private String passwordHash;
	private String passwordSalt;
	private String role;
	private Integer status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}


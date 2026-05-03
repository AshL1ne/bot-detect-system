package com.bds.relation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("relations")
public class RelationEntity {
	private String followerId;
	private String followeeId;
}


package com.bds.profile.dto;

import lombok.Data;

@Data
public class ActiveHourDTO {
	/** 时段起点小时：0,2,4,…,22（每 2 小时一段，共 12 段） */
	private Integer periodStartHour;
	/** 该时段原创微博条数 */
	private Long originalCount;
	/** 该时段转发微博条数 */
	private Long retweetCount;
	/** 该时段全部微博条数 */
	private Long totalCount;
}


package com.bds.user.dto;

import com.bds.common.QueryPageParam;
import lombok.Data;

@Data
public class UserListQuery extends QueryPageParam {
	private String keyword;
	/** null：不按该维度筛选 */
	private Boolean verified;
	private Boolean isMalicious;
}


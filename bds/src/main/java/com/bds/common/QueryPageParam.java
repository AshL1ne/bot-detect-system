package com.bds.common;

import lombok.Data;

@Data
public class QueryPageParam {
	private int pageNum = 1;
	private int pageSize = 10;
}


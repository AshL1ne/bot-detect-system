package com.bds.tweet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bds.tweet.dto.TweetHourActivityRow;
import com.bds.tweet.entity.TweetEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TweetMapper extends BaseMapper<TweetEntity> {

	@Select("""
			SELECT (HOUR(created_at) DIV 2) AS period_slot,
				SUM(CASE WHEN IFNULL(is_retweet, 0) = 0 THEN 1 ELSE 0 END) AS original_count,
				SUM(CASE WHEN IFNULL(is_retweet, 0) <> 0 THEN 1 ELSE 0 END) AS retweet_count,
				COUNT(*) AS total_count
			FROM tweets
			WHERE user_id = #{userId}
			GROUP BY (HOUR(created_at) DIV 2)
			ORDER BY period_slot
			""")
	List<TweetHourActivityRow> selectHourActivity(@Param("userId") String userId);
}


package com.bds.relation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bds.relation.dto.RelationUserDTO;
import com.bds.relation.entity.RelationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RelationMapper extends BaseMapper<RelationEntity> {

	@Select("""
			SELECT r.follower_id AS userId, u.nick_name AS nickName
			FROM relations r
			LEFT JOIN users u ON u.user_id = r.follower_id
			WHERE r.followee_id = #{userId}
			ORDER BY r.follower_id
			""")
	List<RelationUserDTO> selectFollowers(@Param("userId") String userId);

	@Select("""
			SELECT r.followee_id AS userId, u.nick_name AS nickName
			FROM relations r
			LEFT JOIN users u ON u.user_id = r.followee_id
			WHERE r.follower_id = #{userId}
			ORDER BY r.followee_id
			""")
	List<RelationUserDTO> selectFollowees(@Param("userId") String userId);
}


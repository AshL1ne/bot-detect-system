package com.bds.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bds.common.PageResult;
import com.bds.user.dto.UserDetailVO;
import com.bds.user.dto.UserListQuery;
import com.bds.user.entity.UserEntity;
import com.bds.user.mapper.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
	private final UserMapper userMapper;

	public UserService(UserMapper userMapper) {
		this.userMapper = userMapper;
	}

	public PageResult<UserEntity> listUsers(UserListQuery query) {
		LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
		String keyword = query.getKeyword();
		if (keyword != null && !keyword.isBlank()) {
			wrapper.and(q -> q.like(UserEntity::getUserId, keyword)
					.or()
					.like(UserEntity::getNickName, keyword));
		}
		if (query.getVerified() != null) {
			wrapper.eq(UserEntity::getVerified, query.getVerified());
		}
		if (query.getIsMalicious() != null) {
			wrapper.eq(UserEntity::getIsMalicious, query.getIsMalicious());
		}
		Page<UserEntity> page = new Page<>(query.getPageNum(), query.getPageSize());
		Page<UserEntity> result = userMapper.selectPage(page, wrapper);
		return new PageResult<>(result.getRecords(), result.getTotal(), query.getPageNum(), query.getPageSize());
	}

	public UserDetailVO getUserDetail(String userId) {
		UserEntity entity = userMapper.selectById(userId);
		if (entity == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
		}
		UserDetailVO vo = new UserDetailVO();
		vo.setUserId(entity.getUserId());
		vo.setNickName(entity.getNickName());
		vo.setDescription(entity.getDescription());
		vo.setGender(entity.getGender());
		vo.setFollowersCount(entity.getFollowersCount());
		vo.setFollowCount(entity.getFollowCount());
		vo.setStatusesCount(entity.getStatusesCount());
		vo.setVerified(entity.getVerified());
		vo.setMbrank(entity.getMbrank());
		vo.setMbtype(entity.getMbtype());
		vo.setOriginalCount(entity.getOriginalCount());
		vo.setForwardCount(entity.getForwardCount());
		vo.setMalProb(entity.getMalProb());
		vo.setIsMalicious(entity.getIsMalicious());
		return vo;
	}
}

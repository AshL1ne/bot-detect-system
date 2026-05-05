package com.bds.relation.service;

import com.bds.relation.dto.RelationUserDTO;
import com.bds.relation.mapper.RelationMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RelationService {
	private final RelationMapper relationMapper;

	public RelationService(RelationMapper relationMapper) {
		this.relationMapper = relationMapper;
	}

	public List<RelationUserDTO> listFollowers(String userId) {
		requireUserId(userId);
		return relationMapper.selectFollowers(userId);
	}

	public List<RelationUserDTO> listFollowees(String userId) {
		requireUserId(userId);
		return relationMapper.selectFollowees(userId);
	}

	private void requireUserId(String userId) {
		if (userId == null || userId.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
		}
	}
}


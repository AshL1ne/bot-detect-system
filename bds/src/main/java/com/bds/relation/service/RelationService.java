package com.bds.relation.service;

import com.bds.relation.entity.RelationEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class RelationService {
	public List<RelationEntity> listFollowers(String userId) {
		// TODO: implement follower list
		return Collections.emptyList();
	}

	public List<RelationEntity> listFollowees(String userId) {
		// TODO: implement followee list
		return Collections.emptyList();
	}
}


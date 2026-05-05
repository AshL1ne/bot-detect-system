package com.bds.relation.controller;

import com.bds.common.Result;
import com.bds.relation.dto.RelationUserDTO;
import com.bds.relation.service.RelationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relations")
public class RelationController {
	private final RelationService relationService;

	public RelationController(RelationService relationService) {
		this.relationService = relationService;
	}

	@GetMapping("/{id}/followers")
	public Result<List<RelationUserDTO>> listFollowers(@PathVariable("id") String userId) {
		return Result.success(relationService.listFollowers(userId));
	}

	@GetMapping("/{id}/followees")
	public Result<List<RelationUserDTO>> listFollowees(@PathVariable("id") String userId) {
		return Result.success(relationService.listFollowees(userId));
	}
}


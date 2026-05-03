package com.bds.user.controller;

import com.bds.common.PageResult;
import com.bds.common.Result;
import com.bds.profile.dto.ActiveHourDTO;
import com.bds.profile.dto.WordCloudDTO;
import com.bds.profile.service.UserProfileService;
import com.bds.user.dto.UserDetailVO;
import com.bds.user.dto.UserListQuery;
import com.bds.user.entity.UserEntity;
import com.bds.user.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
	private final UserService userService;
	private final UserProfileService userProfileService;

	public UserController(UserService userService, UserProfileService userProfileService) {
		this.userService = userService;
		this.userProfileService = userProfileService;
	}

	@PostMapping("/search")
	public Result<PageResult<UserEntity>> listUsers(@RequestBody UserListQuery query) {
		return Result.success(userService.listUsers(query));
	}

	@GetMapping("/{id}")
	public Result<UserDetailVO> getUserDetail(@PathVariable("id") String userId) {
		return Result.success(userService.getUserDetail(userId));
	}

	@GetMapping("/{id}/wordcloud")
	public Result<List<WordCloudDTO>> getWordCloud(@PathVariable("id") String userId) {
		return Result.success(userProfileService.getWordCloud(userId));
	}

	@GetMapping("/{id}/active-hours")
	public Result<List<ActiveHourDTO>> getActiveHours(@PathVariable("id") String userId) {
		return Result.success(userProfileService.getActiveHours(userId));
	}
}


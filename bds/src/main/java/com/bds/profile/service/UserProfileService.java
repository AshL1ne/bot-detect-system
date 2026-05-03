package com.bds.profile.service;

import com.bds.profile.dto.ActiveHourDTO;
import com.bds.profile.dto.WordCloudDTO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserProfileService {
	public List<WordCloudDTO> getWordCloud(String userId) {
		// TODO: compute word cloud from tweets
		return Collections.emptyList();
	}

	public List<ActiveHourDTO> getActiveHours(String userId) {
		// TODO: compute activity distribution by hour
		return Collections.emptyList();
	}
}


package com.bds.profile.util;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import com.bds.profile.dto.WordCloudDTO;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public final class WordCloudGenerator {
	private static final Pattern DIGITS = Pattern.compile("^\\d+$");
	private static final Pattern NON_WORD = Pattern.compile("^[^\\p{L}\\p{N}]+$");

	private WordCloudGenerator() {
	}

	public static List<WordCloudDTO> topWords(Iterable<String> texts, int topN) {
		if (topN <= 0) {
			return List.of();
		}
		Set<String> stopwords = loadStopwords();
		JiebaSegmenter segmenter = new JiebaSegmenter();
		Map<String, Long> freq = new HashMap<>();
		for (String raw : texts) {
			if (raw == null || raw.isBlank()) {
				continue;
			}
			String normalized = raw.replace('\u00a0', ' ').trim();
			if (normalized.isEmpty()) {
				continue;
			}
			for (SegToken token : segmenter.process(normalized, JiebaSegmenter.SegMode.SEARCH)) {
				String word = token.word != null ? token.word.trim() : "";
				if (!acceptToken(word, stopwords)) {
					continue;
				}
				freq.merge(word, 1L, Long::sum);
			}
		}
		List<WordCloudDTO> list = new ArrayList<>();
		for (Map.Entry<String, Long> e : freq.entrySet()) {
			WordCloudDTO dto = new WordCloudDTO();
			dto.setWord(e.getKey());
			dto.setCount(e.getValue());
			list.add(dto);
		}
		list.sort(Comparator.comparingLong(WordCloudDTO::getCount).reversed());
		if (list.size() > topN) {
			return list.subList(0, topN);
		}
		return list;
	}

	private static boolean acceptToken(String word, Set<String> stopwords) {
		if (word.length() < 2) {
			return false;
		}
		String lower = word.toLowerCase(Locale.ROOT);
		if (stopwords.contains(lower)) {
			return false;
		}
		if (DIGITS.matcher(word).matches()) {
			return false;
		}
		if (NON_WORD.matcher(word).matches()) {
			return false;
		}
		return true;
	}

	private static Set<String> loadStopwords() {
		InputStream in = WordCloudGenerator.class.getResourceAsStream("/nlp/stopwords.txt");
		if (in == null) {
			return Collections.emptySet();
		}
		Set<String> set = new HashSet<>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (!line.isEmpty() && !line.startsWith("#")) {
					set.add(line.toLowerCase(Locale.ROOT));
				}
			}
		} catch (Exception ignored) {
			return Collections.emptySet();
		}
		return set;
	}
}

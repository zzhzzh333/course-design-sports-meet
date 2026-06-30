package com.sportsmeet.service;

import com.sportsmeet.entity.Score;
import java.util.List;
import java.util.Map;

public interface ScoreService {
    void submitScores(Long eventId, List<Map<String, String>> scoreData);
    List<Score> findByEventId(Long eventId);
    List<Score> findPersonalScores(String keyword);
    List<Map<String, Object>> findTeamScoreSummary();
}
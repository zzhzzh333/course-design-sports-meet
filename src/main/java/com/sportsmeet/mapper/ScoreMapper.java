package com.sportsmeet.mapper;

import com.sportsmeet.entity.Score;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface ScoreMapper {
    int insert(Score score);
    List<Score> findByEventId(Long eventId);
    List<Score> findByAthleteId(@Param("athleteId") Long athleteId);
    void deleteByEventId(Long eventId);
    List<Map<String, Object>> findTeamScoreSummary();
    List<Score> findPersonalScores(@Param("keyword") String keyword);
}
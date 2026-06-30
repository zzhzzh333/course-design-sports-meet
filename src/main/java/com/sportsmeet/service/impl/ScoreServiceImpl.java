package com.sportsmeet.service.impl;

import com.sportsmeet.entity.Event;
import com.sportsmeet.entity.Registration;
import com.sportsmeet.entity.Score;
import com.sportsmeet.mapper.EventMapper;
import com.sportsmeet.mapper.RegistrationMapper;
import com.sportsmeet.mapper.ScoreMapper;
import com.sportsmeet.service.ScoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class ScoreServiceImpl implements ScoreService {

    private static final int[] POINTS = {7, 5, 3, 2, 1};

    private final ScoreMapper scoreMapper;
    private final RegistrationMapper registrationMapper;
    private final EventMapper eventMapper;

    public ScoreServiceImpl(ScoreMapper scoreMapper, RegistrationMapper registrationMapper, EventMapper eventMapper) {
        this.scoreMapper = scoreMapper;
        this.registrationMapper = registrationMapper;
        this.eventMapper = eventMapper;
    }

    @Override
    @Transactional
    public void submitScores(Long eventId, List<Map<String, String>> scoreData) {
        Event event = eventMapper.findById(eventId);
        if (event == null) {
            throw new RuntimeException("比赛项目不存在");
        }

        scoreMapper.deleteByEventId(eventId);

        int topN = event.getTopN() != null ? event.getTopN() : 5;

        for (Map<String, String> row : scoreData) {
            String athleteIdStr = row.get("athleteId");
            String rankStr = row.get("rank");
            String resultStr = row.get("result");

            if (athleteIdStr == null || rankStr == null || rankStr.trim().isEmpty()) {
                continue;
            }

            Long athleteId = Long.parseLong(athleteIdStr);
            int rank = Integer.parseInt(rankStr.trim());

            int points = (rank <= POINTS.length && rank <= topN) ? POINTS[rank - 1] : 0;

            Registration reg = registrationMapper.findByAthleteAndEvent(athleteId, eventId);

            Score score = new Score();
            score.setRegistrationId(reg != null ? reg.getId() : 0L);
            score.setAthleteId(athleteId);
            score.setEventId(eventId);
            score.setRank(rank);
            score.setResult(resultStr);
            score.setPoints(points);
            scoreMapper.insert(score);
        }

        registrationMapper.updateStatusByEvent(eventId, 1);
        if (event.getStatus() != 2) {
            event.setStatus(2);
            eventMapper.update(event);
        }
    }

    @Override
    public List<Score> findByEventId(Long eventId) {
        return scoreMapper.findByEventId(eventId);
    }

    @Override
    public List<Score> findPersonalScores(String keyword) {
        return scoreMapper.findPersonalScores(keyword);
    }

    @Override
    public List<Map<String, Object>> findTeamScoreSummary() {
        return scoreMapper.findTeamScoreSummary();
    }
}
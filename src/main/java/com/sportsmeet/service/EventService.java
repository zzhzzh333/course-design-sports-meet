package com.sportsmeet.service;

import com.sportsmeet.entity.Event;
import java.util.List;

public interface EventService {
    List<Event> findAll(String category, String keyword);
    Event findById(Long id);
    void save(Event event);
    void update(Event event);
    void deleteById(Long id);
    int countTotal();
    int countByStatus(Integer status);
    List<Event> findUpcoming(int limit);
    List<Event> findEndedWithoutScores();
}
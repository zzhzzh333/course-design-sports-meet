package com.sportsmeet.service.impl;

import com.sportsmeet.entity.Event;
import com.sportsmeet.mapper.EventMapper;
import com.sportsmeet.service.EventService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private final EventMapper eventMapper;

    public EventServiceImpl(EventMapper eventMapper) {
        this.eventMapper = eventMapper;
    }

    @Override
    public List<Event> findAll(String category, String keyword) {
        return eventMapper.findAll(category, keyword);
    }

    @Override
    public Event findById(Long id) {
        return eventMapper.findById(id);
    }

    @Override
    public void save(Event event) {
        event.setStatus(0);
        eventMapper.insert(event);
    }

    @Override
    public void update(Event event) {
        eventMapper.update(event);
    }

    @Override
    public void deleteById(Long id) {
        eventMapper.deleteById(id);
    }

    @Override
    public int countTotal() {
        return eventMapper.countTotal();
    }

    @Override
    public int countByStatus(Integer status) {
        return eventMapper.countByStatus(status);
    }

    @Override
    public List<Event> findUpcoming(int limit) {
        return eventMapper.findUpcoming(limit);
    }
}
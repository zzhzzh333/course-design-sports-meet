package com.sportsmeet.service.impl;

import com.sportsmeet.entity.Event;
import com.sportsmeet.entity.Registration;
import com.sportsmeet.mapper.EventMapper;
import com.sportsmeet.mapper.RegistrationMapper;
import com.sportsmeet.service.RegistrationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationMapper registrationMapper;
    private final EventMapper eventMapper;

    public RegistrationServiceImpl(RegistrationMapper registrationMapper, EventMapper eventMapper) {
        this.registrationMapper = registrationMapper;
        this.eventMapper = eventMapper;
    }

    @Override
    public List<Registration> findAll(Long eventId, String keyword) {
        return registrationMapper.findAll(eventId, keyword);
    }

    @Override
    @Transactional
    public void register(Long athleteId, Long eventId) {
        Event event = eventMapper.findById(eventId);
        if (event == null) {
            throw new RuntimeException("比赛项目不存在");
        }
        if (event.getStatus() == 2) {
            throw new RuntimeException("该项目已结束，不可报名");
        }
        Registration exist = registrationMapper.findByAthleteAndEvent(athleteId, eventId);
        if (exist != null) {
            throw new RuntimeException("该运动员已报名此项目");
        }
        Registration reg = new Registration();
        reg.setAthleteId(athleteId);
        reg.setEventId(eventId);
        reg.setStatus(0);
        registrationMapper.insert(reg);
    }

    @Override
    public void cancel(Long registrationId) {
        registrationMapper.deleteById(registrationId);
    }

    @Override
    public boolean hasRegistration(Long athleteId, Long eventId) {
        return registrationMapper.findByAthleteAndEvent(athleteId, eventId) != null;
    }

    @Override
    public int countTotal() {
        return registrationMapper.countTotal();
    }

    @Override
    public int countByEventId(Long eventId) {
        return registrationMapper.countByEventId(eventId);
    }

    @Override
    public List<Registration> findByEventId(Long eventId) {
        return registrationMapper.findByEventId(eventId);
    }
}
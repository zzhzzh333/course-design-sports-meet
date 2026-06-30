package com.sportsmeet.service.impl;

import com.sportsmeet.entity.Athlete;
import com.sportsmeet.mapper.AthleteMapper;
import com.sportsmeet.service.AthleteService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AthleteServiceImpl implements AthleteService {

    private final AthleteMapper athleteMapper;

    public AthleteServiceImpl(AthleteMapper athleteMapper) {
        this.athleteMapper = athleteMapper;
    }

    @Override
    public List<Athlete> findAll(Long deptId, String keyword) {
        return athleteMapper.findAll(deptId, keyword);
    }

    @Override
    public Athlete findById(Long id) {
        return athleteMapper.findById(id);
    }

    @Override
    public void save(Athlete athlete) {
        athleteMapper.insert(athlete);
    }

    @Override
    public void update(Athlete athlete) {
        athleteMapper.update(athlete);
    }

    @Override
    public void deleteById(Long id) {
        athleteMapper.deleteById(id);
    }

    @Override
    public int countTotal() {
        return athleteMapper.countTotal();
    }

    @Override
    public int countByDeptId(Long deptId) {
        return athleteMapper.countByDeptId(deptId);
    }

    @Override
    public boolean hasRegistration(Long athleteId) {
        return athleteMapper.countRegistrationByAthleteId(athleteId) > 0;
    }
}
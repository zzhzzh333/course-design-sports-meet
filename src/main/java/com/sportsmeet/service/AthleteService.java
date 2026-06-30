package com.sportsmeet.service;

import com.sportsmeet.entity.Athlete;
import java.util.List;

public interface AthleteService {
    List<Athlete> findAll(Long deptId, String keyword);
    Athlete findById(Long id);
    void save(Athlete athlete);
    void update(Athlete athlete);
    void deleteById(Long id);
    int countTotal();
    int countByDeptId(Long deptId);
    boolean hasRegistration(Long athleteId);
}
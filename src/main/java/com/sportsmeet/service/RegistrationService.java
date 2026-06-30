package com.sportsmeet.service;

import com.sportsmeet.entity.Registration;
import java.util.List;

public interface RegistrationService {
    List<Registration> findAll(Long eventId, String keyword);
    void register(Long athleteId, Long eventId);
    void cancel(Long registrationId);
    boolean hasRegistration(Long athleteId, Long eventId);
    int countTotal();
    int countByEventId(Long eventId);
    List<Registration> findByEventId(Long eventId);
}
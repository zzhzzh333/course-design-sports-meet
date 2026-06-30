package com.sportsmeet.mapper;

import com.sportsmeet.entity.Registration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface RegistrationMapper {
    List<Registration> findAll(@Param("eventId") Long eventId, @Param("keyword") String keyword);
    Registration findByAthleteAndEvent(@Param("athleteId") Long athleteId, @Param("eventId") Long eventId);
    int insert(Registration registration);
    int deleteById(Long id);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    int updateStatusByEvent(@Param("eventId") Long eventId, @Param("status") Integer status);
    List<Registration> findByEventId(Long eventId);
    int countTotal();
    int countByEventId(Long eventId);
}
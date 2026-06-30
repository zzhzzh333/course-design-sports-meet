package com.sportsmeet.mapper;

import com.sportsmeet.entity.Event;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface EventMapper {
    List<Event> findAll(@Param("category") String category, @Param("keyword") String keyword);
    Event findById(Long id);
    int insert(Event event);
    int update(Event event);
    int deleteById(Long id);
    int countByStatus(@Param("status") Integer status);
    int countTotal();
    int countRegistrationByEventId(Long eventId);
    List<Event> findUpcoming(@Param("limit") int limit);
}
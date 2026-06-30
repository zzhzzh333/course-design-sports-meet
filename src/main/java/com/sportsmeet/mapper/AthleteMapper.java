package com.sportsmeet.mapper;

import com.sportsmeet.entity.Athlete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AthleteMapper {
    List<Athlete> findAll(@Param("deptId") Long deptId, @Param("keyword") String keyword);
    Athlete findById(Long id);
    Athlete findByStudentNo(String studentNo);
    int insert(Athlete athlete);
    int update(Athlete athlete);
    int deleteById(Long id);
    int countByDeptId(Long deptId);
    int countTotal();
    int countRegistrationByAthleteId(Long athleteId);
}
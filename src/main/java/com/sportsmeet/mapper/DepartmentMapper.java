package com.sportsmeet.mapper;

import com.sportsmeet.entity.Department;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface DepartmentMapper {
    List<Department> findAll();
    Department findById(Long id);
}
package com.sportsmeet.service;

import com.sportsmeet.entity.Department;
import java.util.List;

public interface DepartmentService {
    List<Department> findAll();
    Department findById(Long id);
}
package com.sportsmeet.service.impl;

import com.sportsmeet.entity.Department;
import com.sportsmeet.mapper.DepartmentMapper;
import com.sportsmeet.service.DepartmentService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentMapper departmentMapper;

    public DepartmentServiceImpl(DepartmentMapper departmentMapper) {
        this.departmentMapper = departmentMapper;
    }

    @Override
    public List<Department> findAll() {
        return departmentMapper.findAll();
    }

    @Override
    public Department findById(Long id) {
        return departmentMapper.findById(id);
    }
}
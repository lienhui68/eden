package com.eh.eden.ssm.service;

import com.eh.eden.ssm.orm.bean.Employee;
import com.eh.eden.ssm.orm.dao.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    public List<Employee> getAllEmployees() {
        return employeeMapper.getAllEmployees();
    }
}

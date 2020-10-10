package com.eh.eden.ssm.orm.dao;


import com.eh.eden.ssm.orm.bean.Employee;

import java.util.List;

public interface EmployeeMapper {

    List<Employee> getAllEmployees();

}

package com.eh.eden.mybatis.orm.dao;

import com.eh.eden.mybatis.orm.bean.Employee;

import java.util.List;

public interface EmployeeMapperDynamic {

    Employee getEmployeeByConditionIf(Employee employee);

    Employee getEmployeeByConditionChoose(Employee employee);

    void updateEmployeeById(Employee employee);

    List<Employee> getEmployeesByIds(List<Integer> ids);

    void insertEmployees(List<Employee> employees);
}

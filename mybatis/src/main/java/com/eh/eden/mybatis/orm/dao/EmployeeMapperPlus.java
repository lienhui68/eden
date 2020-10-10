package com.eh.eden.mybatis.orm.dao;

import com.eh.eden.mybatis.orm.bean.Employee;

import java.util.List;

public interface EmployeeMapperPlus {

    Employee getEmployeeById(Integer id);

    Employee getUnionEmployeeById(Integer id);

    Employee getEmployeeByIdStepDis(Integer id);

    List<Employee> getEmployeesByDeptId(Integer deptId);

}

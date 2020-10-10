package com.eh.eden.mybatis.orm.dao;

import com.eh.eden.mybatis.orm.bean.Employee;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface EmployeeMapper {

    void insertEmployee(Employee employee);

    void deleteEmployee(Integer id);

    void updateEmployeeById(Employee employee);

    Employee getEmployeeById(Integer id);

    Employee getEmployeeByIdAndName(@Param("id") Integer id, @Param("lastName") String lastName);

    Employee getEmployeeByMap(Map map);

    Employee getEmployeeByDynamicTable(Map<String, Object> map);

    List<Employee> getEmployeeByLastNameLike(String lastNameLike);

    Map<String, Object> getSingleMapById(Integer id);

    @MapKey("id")
    Map<Integer, Employee> getMultiMapByLastNameLike(String lastNameLike);

}

package com.eh.eden.ssm.controller;

import com.eh.eden.ssm.orm.bean.Employee;
import com.eh.eden.ssm.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @RequestMapping("/employees")
    public String employees(Map<String, Object> map) {
        List<Employee> employees = employeeService.getAllEmployees();
        map.put("employees", employees);
        return "list";
    }
}

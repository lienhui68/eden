package com.eh.eden.mybatis.orm.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Dept {
    private Integer id;
    private String deptName;
    private List<Employee> employees;
}

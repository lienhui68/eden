package com.eh.eden.mybatis.orm.dao;

import com.eh.eden.mybatis.orm.bean.Dept;
import com.eh.eden.mybatis.orm.bean.Employee;
import com.google.common.collect.Lists;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class EmployeeMapperTest {

    public SqlSessionFactory getSqlSessionFactory() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        return new SqlSessionFactoryBuilder().build(inputStream);
    }

    //    @Test
    public void test() throws IOException {
        // 1. 获取SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();

        // 2. 从 SqlSessionFactory 中获取 SqlSession，能直接执行已经映射的sql语句
        try (SqlSession session = sqlSessionFactory.openSession()) {
            // 3. 获取接口实现类对象
            // mybatis会为接口自动创建一个代理对象，代理对象去执行增删改查方法
            EmployeeMapper employeeMapper = session.getMapper(EmployeeMapper.class);
            System.out.println(employeeMapper.getEmployeeById(1));
        }
    }

    /**
     * 测试增删改
     * 1. mybatis允许增删改直接定义以下类型返回值
     * Integer、Long、Boolean
     * 2. 需要手动提交数据
     * sqlSessionFactory.openSession(true) 自动提交
     */
//    @Test
    public void test1() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();

        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            EmployeeMapperDynamic employeeMapper = session.getMapper(EmployeeMapperDynamic.class);
            List<Employee> employees = employeeMapper.getEmployeesByIds(Arrays.asList(1, 2, 3, 4));
            System.out.println(employees);
        }
    }

    /**
     * 部门
     *
     * @throws IOException
     */
//    @Test
    public void test2() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();

        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            DeptMapper deptMapper = session.getMapper(DeptMapper.class);
            Dept dept = deptMapper.getDeptEmpCollectionByIdStep(1);
            System.out.println(dept.getDeptName());
            List<Employee> employees = dept.getEmployees();
            System.out.println(employees.size());
        }
    }

    //    @Test
    public void test3() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();

        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            EmployeeMapperDynamic employeeMapper = session.getMapper(EmployeeMapperDynamic.class);
            List<Employee> employees = Lists.newArrayList(
                    new Employee(null, "李逵", "1", "lk@sh.com", new Dept(1, null, null)),
                    new Employee(null, "鲁达", "1", "ld@sh.com", new Dept(2, null, null))
            );
            employeeMapper.insertEmployees(employees);
        }
    }

    /**
     * 缓存
     */
    @Test
    public void test4() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();

        // 第一次会话
        try (SqlSession session = sqlSessionFactory.openSession(true)
        ) {
            EmployeeMapper employeeMapper = session.getMapper(EmployeeMapper.class);
            Employee employee = employeeMapper.getEmployeeById(1);
            System.out.println(employee);
        }

        // 第二次会话
        try (SqlSession session = sqlSessionFactory.openSession(true)
        ) {
            EmployeeMapper employeeMapper = session.getMapper(EmployeeMapper.class);
            Employee employee = employeeMapper.getEmployeeById(1);
            System.out.println(employee);
        }
    }
}

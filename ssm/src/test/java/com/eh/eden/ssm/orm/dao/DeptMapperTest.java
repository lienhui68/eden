package com.eh.eden.ssm.orm.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;

/**
 * todo
 *
 * @author David Li
 * @date 2020/10/10
 */
@ContextConfiguration({"classpath:/application.xml"})
public class DeptMapperTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private DeptMapper deptMapper;

    @Test
    public void test() {
        System.out.println(deptMapper.selectByPrimaryKey(1).getDeptName());
    }

}
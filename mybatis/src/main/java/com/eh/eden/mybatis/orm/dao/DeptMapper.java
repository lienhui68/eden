package com.eh.eden.mybatis.orm.dao;

import com.eh.eden.mybatis.orm.bean.Dept;

/**
 * todo
 *
 * @author David Li
 * @create 2020/10/09
 */
public interface DeptMapper {
    Dept getDeptById(Integer id);

    Dept getDeptEmpCollectionById(Integer id);

    Dept getDeptEmpCollectionByIdStep(Integer id);
}

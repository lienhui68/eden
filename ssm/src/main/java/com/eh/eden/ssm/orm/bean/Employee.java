package com.eh.eden.ssm.orm.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Employee implements Serializable {

    private static final long serialVersionUID = 5591903708394742480L;
    private Integer id;
    private String lastName;
    private String gender;
    private String email;

}

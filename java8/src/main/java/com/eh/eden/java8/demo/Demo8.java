package com.eh.eden.java8.demo;

import java.util.Optional;
import java.util.Properties;

/**
 * todo
 *
 * @author David Li
 * @create 2020/08/13
 */
public class Demo8 {

    Properties props = new Properties() {
        {
            setProperty("a", "5");
            setProperty("b", "true");
            setProperty("c", "-3");
        }
    };

    public int readDuration(Properties props, String name) {
        return Optional.ofNullable(props.getProperty(name)).flatMap(OptionalUtil::string2Int)
                .filter(i -> i > 0).orElse(0);
    }


    public static void main(String[] args) {
        Demo8 demo8 = new Demo8();
        assert demo8.readDuration(demo8.props, "a") == 5;
        assert demo8.readDuration(demo8.props, "b") == 0;
        assert demo8.readDuration(demo8.props, "c") == 0;
        assert demo8.readDuration(demo8.props, "d") == 0;
    }

}

class OptionalUtil {
    public static Optional<Integer> string2Int(String s) {
        try {
            return Optional.of(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}

class Person {
    private Optional<Car> car; // 人可能有车，也可能没 有车，因此将这个字段 声明为Optional

    public Optional<Car> getCar() {
        return car;
    }
}

class Car {
    private Optional<Insurance> insurance; // 车可能进行了保险，也可 能没有保险，所以将这个 字段声明为Optional

    public Optional<Insurance> getInsurance() {
        return insurance;
    }
}

class Insurance {
    private String name; // 保险公司必 须有名字

    public String getName() {
        return name;
    }
}



package com.eh.eden.java8.demo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.*;

/**
 * todo
 *
 * @author David Li
 * @create 2020/08/06
 */
public class Demo1 {
    void main(String[] args) throws FileNotFoundException {
        new BufferedInputStream(new FileInputStream(new File(".")));
    }

}

@Data
@AllArgsConstructor
class Sheep implements Cloneable {
    private String name;
    private int age;
    private String color;

    /**
     * 克隆该实例，使用默认的clone方法来完成
     */
    @Override
    protected Sheep clone() {
        Sheep sheep = null;
        try {
            sheep = (Sheep) super.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sheep;
    }

    @Override
    public String toString() {
        return "Sheep [name=" + name + ", age=" + age + ", color=" + color + "]";
    }

}

class Client {
    public static void main(String[] args) {
        Sheep sheep = new Sheep("小样", 12, "白色");
        Sheep sheep2 = sheep.clone();
        Sheep sheep3 = sheep.clone();
        Sheep sheep4 = sheep.clone();
        System.out.println(sheep.toString());
        System.out.println(sheep2.toString());
        System.out.println(sheep3.toString());
        System.out.println(sheep4.toString());
    }
}

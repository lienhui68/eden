package com.eh.eden.xml;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

public class XMLTest {
    public static void main(String[] args) throws Exception {

        // 1. 加载xml文件到jvm中，形成数据流
        InputStream is = XMLTest.class.getClassLoader().getResourceAsStream("test.xml");

        // 2. 创建解析对象
        SAXReader sax = new SAXReader();

        // 3. 获得文档对象（整个xml文件），将数据流转换成一个文档对象
        Document doc = sax.read(is);

        // 4. 获得根元素
        Element root = doc.getRootElement();

        System.out.println("=======获取所有学生的名字========");
        // 获取所有学生的名字
        List<Node> names = root.selectNodes("student/name");
        names.forEach(node -> System.out.println(node.getText()));

        System.out.println("=======获取带有属性type的学生名字========");

        // 获取带有属性type的学生名字
        List<Node> namesWithType = root.selectNodes("student[@type]/name");
        namesWithType.forEach(node -> System.out.println(node.getText()));

        System.out.println("=======获取带有属性type并且属性值等于good的学生名字========");

        // 获取带有属性type并且属性值等于good的学生名字
        List<Node> namesWithTypeEqGood = root.selectNodes("student[@type='good']/name");
        namesWithTypeEqGood.forEach(node -> System.out.println(node.getText()));

        System.out.println("=======获取年龄超过10岁的学生名字========");

        // 获取年龄超过10岁的学生名字
        List<Node> namesWithAgeGT10 = root.selectNodes("student[age>10]/name");
        namesWithAgeGT10.forEach(node -> System.out.println(node.getText()));

        System.out.println("=======获取第一个学生的名字========");
        // 获取第一个学生的名字
        List<Node> firstStudentName = root.selectNodes("student[1]/name");
        firstStudentName.forEach(node -> System.out.println(node.getText()));

        System.out.println("=======获取最后一个学生的名字========");
        // 获取最后一个学生的名字
        List<Node> lastStudentName = root.selectNodes("student[last()]/name");
        lastStudentName.forEach(node -> System.out.println(node.getText()));

        System.out.println("=======获取倒数第二个学生的名字========");
        // 获取倒数第二个学生的名字
        List<Node> last2StudentName = root.selectNodes("student[last() - 1]/name");
        last2StudentName.forEach(node -> System.out.println(node.getText()));
    }
}

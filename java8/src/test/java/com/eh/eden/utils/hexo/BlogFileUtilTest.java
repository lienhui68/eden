package com.eh.eden.utils.hexo;

import org.testng.annotations.Test;

import java.io.File;

import static com.eh.eden.utils.hexo.BlogFileUtil.*;

/**
 * 博客辅助工具操作类
 *
 * @author David Li
 * @create 2020/09/16
 */
public class BlogFileUtilTest {

    /**
     *
     */
    @Test
    public void test() {
//        renameCategory(new File(RootFile, "技术"), "工作");
    }

    /**
     * 打印所有影响到的站内链接
     */
    @Test
    public void test1() {
        printAllArticleQuotes("");
    }

    /**
     * 更改文件夹名
     */
//    @Test
    public void test2() {
        renameCategory(new File(RootFile, "工作/910_toolkit"), "910_toolbox");
//        renameCategory(new File(RootFile, "工作/数据结构与算法"), "理论");
    }

    /**
     * 移动文件夹
     */
    @Test
    public void test3() {
//        move("工作/计科基础", "工作/c");
        move("工作/500_中间件/分布式/uncategorized", "工作/400_开发环境/缓存");
    }

    public static void main(String[] args) {
        String line = "- 数据结构与算法";
        System.out.println(line.replace("数据结构与算法", "理论"));
    }
}
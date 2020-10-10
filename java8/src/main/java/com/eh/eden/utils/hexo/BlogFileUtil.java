package com.eh.eden.utils.hexo;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * 博客辅助工具类
 *
 * @author David Li
 * @create 2020/09/16
 */
public class BlogFileUtil {

    private static final Log log = LogFactory.getLog(BlogFileUtil.class);

    private static final String BLOG_ARTICLE_ROOT_DIR = "/Users/david/my/study/project/blog/source/_posts";
    public static final File RootFile = new File(BLOG_ARTICLE_ROOT_DIR);

    /**
     * 将src文件夹移动到dest文件夹下
     * case1: dest新建， 分类层次中没有dest
     * case2: 分类层次中有dest
     * 为了统一处理，将两个文件夹路径合并构造分类层次写入到文件中。
     * eg: src文件夹下的文件分类层次是
     * - 工作
     * - x1
     * - src
     * - x2
     * dest文件路径是工作/dest
     * 移动之后
     * - 工作
     * - dest
     * - src
     * - x2
     * 所以需要先获取dest文件夹的分类层次 工作/dest，然后与src/x2合并
     * 此时有两种case
     * case1 遇到src时还没写完， 需要插入直到遇到src
     * case2 在遇到src之前已经写完 需要删除知道遇到src
     * <p>
     * 1. 创建临时文件000，往临时文件拷贝直到遇到category 001
     * 2. 写入dest分类层次 101
     * 3. 分类层次写完之后，先判断是否遇到src 111，没遇到则跳过，遇到之后继续拷贝。
     *
     * @param destDirectoryPath
     * @param srcDirectoryPaths
     */
    public static void move(String destDirectoryPath, String... srcDirectoryPaths) {
        File destDirectory = new File(RootFile, destDirectoryPath);
        // 验证
        if (destDirectory.exists()) {
            assertDirectory(destDirectory);
        } else {
            destDirectory.mkdirs();
        }
        if (srcDirectoryPaths.length < 1) {
            return;
        }

        List<String> destCategoryLayers = calculateCategoryLayers(destDirectory);
        // 创建临时文件用来组装新的文件内容
        Arrays.asList(srcDirectoryPaths).forEach(src -> {
            File srcDirectory = new File(RootFile, src);
            // 断言srcFile是个文件夹,单个文件自己动手搞更快
            assertDirectory(srcDirectory);
            // 扫描srcDirectory目录下所有文件，修改分类
            scanFile(srcDirectory, srcFile -> {
                try {
                    File file = new File(destDirectory, srcDirectory.getName() + "/" + srcFile.getName());
                    // 验证
                    if (file.exists()) {
                        throw new RuntimeException("操作有误？" + file.getAbsolutePath());
                    } else {
                        File dir = new File(destDirectory, srcDirectory.getName());
                        dir.mkdirs();
                        file.createNewFile();
                    }
                    try (
                            FileChannel fileChannel = new RandomAccessFile(file, "rw").getChannel();
                            Stream<String> lines = Files.lines(srcFile.toPath(), Charsets.UTF_8)
                    ) {
                        // 使用引用类型突破闭包限制
                        @Data
                        final class Flag {
                            byte flag;

                            public void plus(int i) {
                                flag = (byte) (flag + i);
                            }

                            public boolean eq(int i) {
                                return flag == (byte) i;
                            }
                        }
                        Flag flag = new Flag();
                        lines.forEach(line -> {
                            if (flag.eq(1)) {
                                // 写入分类层次
                                destCategoryLayers.forEach(layer -> {
                                    try {
                                        fileChannel.write(ByteBuffer.wrap(("    - " + layer + "\n").getBytes(Charsets.UTF_8)));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                                flag.plus(4);
                            } else if (flag.eq(5) && !line.contains(srcDirectory.getName())) {
                                // 从较多层级目录移动到较少层级目录，需要跳过多余的层级
                                return;
                            } else {
                                try {
                                    fileChannel.write(ByteBuffer.wrap((line + "\n").getBytes()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (flag.eq(7)) { // 加快运行速度
                                return;
                            }
                            if (flag.eq(0) && line.contains("categories")) { // // 在遇到categories之前写
                                flag.plus(1);
                            } else if (!flag.eq(0) && line.contains(srcDirectory.getName())) {
                                flag.plus(2);
                            }
                        });

                        // 删除原文件
                        srcFile.delete();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // 删除原文件夹，有DS_STORE隐藏文件
            try {
                FileDeleteStrategy.FORCE.delete(srcDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

    }

    /**
     * 扫描给定文件夹下的所有文件，执行consumer
     *
     * @param file     文件夹或者文件
     * @param consumer
     */
    private static void scanFile(File file, Consumer<File> consumer) {
        assertNotNull(file);
        // 对单个文件处理
        if (file.isFile() && file.getName().endsWith("md")) {
            consumer.accept(file);
            return;
        }
        List<File> files = getAllFiles(file);
        if (files.isEmpty()) {
            return;
        }
        // 对文件夹处理
        files.stream().forEach(f -> scanFile(f, consumer));
    }

    /**
     * 获取分类层次
     * eg: src文件夹下的文件分类层次是
     * * - 工作
     * * - x1
     * * - src
     * * - x2
     * * dest文件路径是工作/dest
     * * 移动之后
     * * - 工作
     * * - dest
     * * - src
     * * - x2
     * * 所以需要先获取文件夹的分类层次 工作/dest
     *
     * @param file
     * @return [工作, dest]
     */
    private static List<String> calculateCategoryLayers(File file) {
        List<String> result = Lists.newArrayList(file.getName());
        while (true) {
            File parent = file.getParentFile();
            if (RootFile.equals(parent)) {
                break;
            }
            file = parent;
            result.add(parent.getName());
        }
        Collections.reverse(result);
        return result;
    }


    /**
     * 修改文件夹名
     * 1. 修改文件夹下所有文件指定分类
     * 2. 重命名文件夹
     *
     * @param file
     * @param name
     */
    public static void renameCategory(File file, String name) {
        assertDirectory(file);
        // 1. 修改文件夹下所有文件指定分类
        scanFile(file, f -> modifyRow(f, file.getName(), name));
        // 2. 重命名文件夹
        renameDirectory(file, name);
    }

    /**
     * 修改某一行(替换内容)
     * 修改分类名/标签名
     * 1. 创建临时文件 tmp_原文件名.md
     * 2. 往临时文件写
     * 3. 删除原文件
     * 4. 临时文件改名
     *
     * @param file
     * @param newStr
     */
    private static void modifyRow(File file, String oldStr, String newStr) {
        // 1. 创建临时文件
        File tmpFile = new File(file.getParent(), "tmp_" + file.getName());
        // 验证
        if (tmpFile.exists()) {
            throw new RuntimeException("操作有误？" + file.getAbsolutePath());
        }
        try (
                FileChannel fileChannel = new RandomAccessFile(tmpFile, "rw").getChannel();
                Stream<String> lines = Files.lines(file.toPath(), StandardCharsets.UTF_8)
        ) {
            lines.forEach(line -> {
                try {
                    if (line.contains(oldStr)) {
                        fileChannel.write(ByteBuffer.wrap((line.replace(oldStr, newStr) + "\n").getBytes(Charsets.UTF_8)));
                    } else {
                        fileChannel.write(ByteBuffer.wrap((line + "\n").getBytes(Charsets.UTF_8)));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            String oldName = file.getName();
            // 删除原文件
            file.delete();
            // 更改临时文件名
            tmpFile.renameTo(new File(tmpFile.getParent(), oldName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改站内引用
     * 用于修改文章里的站内链接
     * old: {%post_link oldStr 别名 %}
     * new: {%post_link newStr 别名 %}
     * {%post_link 数据结构与算法应用/迷宫路径(回溯+递归) 迷宫路径(回溯+递归) %}
     *
     * @param file
     * @param oldPath /xxx/xxx
     * @param newPath /yyy/yyy
     */
    private static void modifyQuote(File file, String oldPath, String newPath) {
        try (
                RandomAccessFile raf = new RandomAccessFile(file, "rw")
        ) {
            String lineRegex = ".*\\{%.*post_link.*" + oldPath + ".*%}.*";
            String line;
            long lastPointer = 0; // 记住上一次的偏移量
            while ((line = raf.readLine()) != null) {
                if (StringUtils.isEmpty(line)) {
                    continue;
                }
                if (line.matches(lineRegex)) {
                    raf.seek(lastPointer);

                    String fileName = file.getName();
                    // old: {%post_link /xxx/xxx/fileName 别名 %}
                    // new: {%post_link /yyy/yyy/fileName 别名 %}
                    String contentRegex = "{%.*post_link.*" + oldPath + "/" + fileName;
                    newPath = "{%post_link " + newPath + "/" + fileName;
                    raf.writeBytes(line.replace(contentRegex, newPath));
                    return;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * * 输出
     * * path1
     * * line1
     * * path1
     * * line2
     *
     * @param influencePath 可能的原链接path
     */
    public static void printAllArticleQuotes(String influencePath) {

        scanFile(RootFile, f -> {
            try (
                    RandomAccessFile raf = new RandomAccessFile(f, "r")
            ) {
                String regex = ".*\\{%.*post_link.*" + influencePath + ".*%}.*";
                String line;
                while ((line = readLine(raf)) != null) {
                    if (StringUtils.isEmpty(line)) {
                        continue;
                    }
                    if (line.matches(regex)) {
                        System.out.println(f.getAbsolutePath());
                        System.out.println(line);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 使用 RandomAccessFile对象方法的 readLine() 都会将编码格式转换成 ISO-8859-1 所以 输出显示是还要在进行一次转码
     *
     * @param raf
     * @return
     */
    private static String readLine(RandomAccessFile raf) throws IOException {
        String line = raf.readLine();
        if (line == null) {
            return null;
        }
        return new String(line.getBytes(Charsets.ISO_8859_1), Charsets.UTF_8);
    }

    /**
     * 重命名文件夹
     *
     * @param file
     * @param name
     * @return
     */
    private static boolean renameDirectory(File file, String name) {
        if (!file.exists()) {
            log.debug("not exits.");
            return false;
        }
        File newFile = new File(file.getParent(), name);
        return file.renameTo(newFile);
    }


    /**
     * 获取给定文件夹的所有文件夹
     *
     * @param file
     * @return
     */
    private static List<File> getAllDirectories(File file) {
        assertNotNull(file);
        assertDirectory(file);
        File[] files = file.listFiles((dir, name) -> dir.isDirectory());
        List<File> result = Lists.newArrayList();
        Collections.addAll(result, files);
        return result;
    }

    /**
     * 获取给定文件夹下的所有普通文件
     *
     * @param file
     * @return
     */
    private static List<File> getAllNormalFiles(File file) {
        assertNotNull(file);
        assertDirectory(file);
        File[] files = file.listFiles((dir, name) -> dir.isFile());
        List<File> result = Lists.newArrayList();
        Collections.addAll(result, files);
        return result;
    }

    /**
     * 获取给定文件夹下的所有文件
     *
     * @param file
     * @return
     */
    private static List<File> getAllFiles(File file) {
        if (file.getName().endsWith("DS_Store") || file.getName().endsWith("zip")) {
            return Lists.newArrayList();
        }
        assertNotNull(file);
        assertDirectory(file);
        File[] files = file.listFiles((dir, name) -> dir.isDirectory() || name.endsWith("md"));
        List<File> result = Lists.newArrayList();
        Collections.addAll(result, files);
        return result;
    }

    /**
     * not null
     *
     * @param file
     */
    private static void assertNotNull(File file) {
        Assert.notNull(file, () -> "文件为null");
    }

    /**
     * 文件夹断言
     *
     * @param file
     */
    private static void assertDirectory(File file) {
        Assert.isTrue(file.isDirectory(), () -> file.getAbsolutePath() + "is not a directory.");
    }

    /**
     * 普通文件断言
     *
     * @param file
     */
    private static void assertFile(File file) {
        Assert.isTrue(file.isFile(), () -> file.getAbsolutePath() + "is not a File.");
    }

}


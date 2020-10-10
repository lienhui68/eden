package com.eh.eden.java8.demo;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.*;

/**
 * todo
 *
 * @author David Li
 * @create 2020/08/19
 */
public class StreamForkerDemo {
    static List<Dish> menu = Arrays.asList(
            new Dish("pork", false, 800, Dish.Type.MEAT),
            new Dish("beef", false, 700, Dish.Type.MEAT),
            new Dish("chicken", false, 400, Dish.Type.MEAT),
            new Dish("french fries", true, 530, Dish.Type.OTHER),
            new Dish("rice", true, 350, Dish.Type.OTHER),
            new Dish("season fruit", true, 120, Dish.Type.OTHER),
            new Dish("pizza", true, 550, Dish.Type.OTHER),
            new Dish("prawns", false, 300, Dish.Type.FISH),
            new Dish("salmon", false, 450, Dish.Type.FISH));

    public static void main(String[] args) {
        // 测试
        Stream<Dish> menuStream = menu.stream();
        StreamForker.Results results = new StreamForker<>(menuStream)
                // 生成一份由逗号分隔的菜肴名列表
                .fork("shortMenu", s -> s.map(Dish::getName).collect(joining(", ")))
                // 计算菜单的总热量
                .fork("totalCalories", s -> s.mapToInt(Dish::getCalories).sum())
                // 找出热量最高的菜肴
                .fork("mostCaloricDish", s -> s.collect(reducing(
                        (d1, d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2))
                        .get())
                // 按照菜的类型对这些菜进行分类
                .fork("dishesByType", s -> s.collect(groupingBy(Dish::getType))).getResults();

        String shortMenu = results.get("shortMenu");
        int totalCalories = results.get("totalCalories");
        Dish mostCaloricDish = results.get("mostCaloricDish");
        Map<Dish.Type, List<Dish>> dishesByType = results.get("dishesByType");

        System.out.println("Short menu: " + shortMenu);
        System.out.println("Total calories: " + totalCalories);
        System.out.println("Most caloric dish: " + mostCaloricDish);
        System.out.println("Dishes by type: " + dishesByType);
    }
}

@Data
class Dish {

    private String name;
    private boolean vegetarian;
    private int calories;
    private Type type;

    public Dish(String name, boolean vegetarian, int calories, Type type) {
        this.name = name;
        this.vegetarian = vegetarian;
        this.calories = calories;
        this.type = type;
    }

    public enum Type {MEAT, FISH, OTHER}
}

// 定义一个StreamForker，在一个流上执行多个操作
class StreamForker<T> {
    private final Stream<T> stream;
    private final Map<Object, Function<Stream<T>, ?>> forks = new HashMap<>();


    public StreamForker(Stream<T> stream) {
        this.stream = stream;
    }

    public StreamForker<T> fork(Object key, Function<Stream<T>, ?> f) {
        forks.put(key, f); // 使用一个键对流上的函数进行索引
        return this; // 返回this从而保证多次流畅地调用fork方法
    }

    // 使用build方法创建ForkingStreamConsumer
    private ForkingStreamConsumer<T> build() {
        // 创建由队列组成的列表，每一个队列对应一个操作
        List<BlockingQueue<T>> queues = Lists.newArrayList();

        // 建立用于标识操作的键与包含操作结果的 Future 之间的映射关系
        Map<Object, Future<?>> actions = forks.entrySet().stream().reduce(
                new HashMap<>(),
                (map, e) -> {
                    map.put(e.getKey(), getOperationResult(queues, e.getValue()));
                    return map;
                },
                (m1, m2) -> {
                    m1.putAll(m2);
                    return m1;
                }
        );
        return new ForkingStreamConsumer<>(queues, actions);
    }

    // 创建Future
    private Future<?> getOperationResult(List<BlockingQueue<T>> queues, Function<Stream<T>, ?> function) {
        // 为某一任务创建一个队列，并添加到队列的列表中
        BlockingQueue<T> queue = new LinkedBlockingQueue<>();
        queues.add(queue);

        // 创建一个Spliterator，遍历队列中的元素
        Spliterator<T> spliterator = new BlockingQueueSpliterator(queue);
        // 创建一个流，将 Spliterator作为数据源
        Stream<T> source = StreamSupport.stream(spliterator, false);
        // 创建一个Future对象，以异步方式计算在流上执行特定函数的结果
        return CompletableFuture.supplyAsync(() -> function.apply(source));
    }

    public Results getResults() {
        ForkingStreamConsumer<T> consumer = build(); // 创建ForkingStreamConsumer
        try {
            stream.sequential().forEach(consumer); // 处理流中的元素
        } finally {
            consumer.finish(); // 在队列的末尾插入特殊元素表明该 队列已经没有更多需要处理的元素
        }
        return consumer;
    }

    interface Results {
        <R> R get(Object key);
    }

    // 添加处理多个队列的流元素，并根据key获取Future中的结果
    static class ForkingStreamConsumer<T> implements Consumer<T>, StreamForker.Results {

        static final Object END_OF_STREAM = new Object(); // 流遍历结束标志

        private final List<BlockingQueue<T>> queues;
        private final Map<Object, Future<?>> actions;

        public ForkingStreamConsumer(List<BlockingQueue<T>> queues, Map<Object, Future<?>> actions) {
            this.queues = queues;
            this.actions = actions;
        }

        @Override
        public void accept(T t) {
            // 将流中遍历的元素添加到所有的队列中
            queues.forEach(q -> q.add(t));
        }

        // 将最后一个元素添加到队列中，表明该流已经结束
        void finish() {
            accept((T) END_OF_STREAM);
        }

        // 等待Future完成相关的计算， 返回由特定键标识的处理结果
        @Override
        public <R> R get(Object key) {
            try {
                return ((Future<R>) actions.get(key)).get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    class BlockingQueueSpliterator<T> implements Spliterator<T> {
        private final BlockingQueue<T> q;

        public BlockingQueueSpliterator(BlockingQueue<T> q) {
            this.q = q;
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            T t;
            while (true) { // 中断后继续take数据
                try {
                    t = q.take();
                    break;
                } catch (InterruptedException e) {
                    // 中断异常
                }
            }
            if (t != ForkingStreamConsumer.END_OF_STREAM) {
                action.accept(t);
                return true;
            }
            return false;
        }

        @Override
        public Spliterator<T> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return 0;
        }

        @Override
        public int characteristics() {
            return 0;
        }
    }
}
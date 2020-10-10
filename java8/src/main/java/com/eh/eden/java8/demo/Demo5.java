    package com.eh.eden.java8.demo;

    import lombok.Data;

    import java.util.*;
    import java.util.stream.Collectors;

    /**
     * todo
     *
     * @author David Li
     * @create 2020/08/09
     */

    public class Demo5 {

        public static void main(String[] args) {
            Trader raoul = new Trader("Raoul", "Cambridge");
            Trader mario = new Trader("Mario", "Milan");
            Trader alan = new Trader("Alan", "Cambridge");
            Trader brian = new Trader("Brian", "Cambridge");

            List<T> ts = Arrays.asList(
                    new T(brian, 2011, 300),
                    new T(raoul, 2012, 1000),
                    new T(raoul, 2011, 400),
                    new T(mario, 2012, 710),
                    new T(mario, 2012, 700),
                    new T(alan, 2012, 950)
            );

            // 1. 找出2011年发生的所有交易，并按交易额排序（从低到高）。
            List<T> transactions1 = ts.stream()
                    .filter(t -> t.getYear() == 2011)
                    .sorted(Comparator.comparing(T::getValue))
                    .collect(Collectors.toList());
            // 2. 交易员都在哪些不同的城市工作过？
            List<String> cities = ts.stream()
                    .map(t -> t.getTrader().getCity())
                    .distinct()
                    .collect(Collectors.toList());
            //3. 查找所有来自于剑桥的交易员，并按姓名排序。
            List<Trader> traders = ts.stream()
                    .filter(t -> t.getTrader().getCity().equals("Cambridge"))
                    .map(T::getTrader)
                    .distinct()
                    .sorted(Comparator.comparing(Trader::getName))
                    .collect(Collectors.toList());
            //4. 返回所有交易员的姓名字符串，按字母顺序排序。
            String names = ts.stream()
                    .map(t -> t.getTrader().getName())
                    .distinct()
                    .sorted()
                    .collect(Collectors.joining());
            //5. 有没有交易员是在米兰工作的？
            boolean milanBased = ts.stream()
                    .anyMatch(t -> t.getTrader().getCity().equals("Milan"));
            //6. 生活在剑桥的交易员的所有交易额之和
            int sum = ts.stream()
                    .filter(t -> t.getTrader().getCity().equals("Cambridge"))
                    .mapToInt(T::getValue)
                    .reduce(0, Integer::sum);
            int sum1 = ts.stream()
                    .filter(t -> t.getTrader().getCity().equals("Cambridge"))
                    .mapToInt(T::getValue)
                    .sum();
            // 7. 所有交易中，最高的交易额是多少
            int highestValue = ts.stream()
                    .mapToInt(T::getValue)
                    .reduce(Integer.MIN_VALUE, Integer::max);
            Optional<Integer> highestValue1 = ts.stream().map(T::getValue)
                    .reduce(Integer::max);
            OptionalInt highestValue2 = ts.stream().mapToInt(T::getValue)
                    .max();
            // 8. 找到交易额最小的交易
            Optional<T> smallestTransaction = ts.stream()
                    .reduce((t1, t2) -> t1.getValue() < t2.getValue() ? t1 : t2);
            Optional<T> smallestTransaction1 = ts.stream()
                    .min(Comparator.comparing(T::getValue));
        }

    }

    @Data
    class Trader {
        private final String name;
        private final String city;

        public Trader(String n, String c) {
            this.name = n;
            this.city = c;
        }
    }

    @Data
    class T {
        private final Trader trader;
        private final int year;
        private final int value;

        public T(Trader trader, int year, int value) {
            this.trader = trader;
            this.year = year;
            this.value = value;
        }
    }
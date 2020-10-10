package com.eh.eden.java8.demo;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.eh.eden.java8.demo.Demo9.randomDelay;
import static java.math.BigDecimal.ROUND_HALF_EVEN;
import static java.util.stream.Collectors.toList;

/**
 * todo
 *
 * @author David Li
 * @create 2020/08/13
 */
public class Demo9 {

    private static final Random random = new Random();

    public static void randomDelay() {
        int delay = 500 + random.nextInt(2000);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();
        CompletableFuture[] futures = findPricesStream(shops, "myPhone")
                .map(f -> f.thenAccept(s ->
                        System.out.println(s + " (done in " + (System.nanoTime() - start) / 1000_000 + " msecs)")))
                .toArray(size -> new CompletableFuture[size]);
        CompletableFuture.allOf(futures).join();
    }


    static final int SHOP_SIZE = 5;
    static List<Shop> shops = IntStream.rangeClosed(1, SHOP_SIZE).boxed().map(i -> new Shop(String.format("shop-%d", i))).collect(toList());

    // 创建一个线程池， 线程池中线程的数目为100 和商店数目二者中较小 的一个值
    private static final Executor executor = Executors.newFixedThreadPool(
            Math.min(shops.size(), 1200),
            // 使用守护线程——这种方式不会阻止程序的关停
            r -> {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            });

    public static Stream<CompletableFuture<String>> findPricesStream(List<Shop> shops, String product) {
        return shops.stream()
                // 以异步方式取得每个 shop 中指定产品的原始价格
                .map(shop ->
                        CompletableFuture.supplyAsync(() ->
                                shop.getPrice(product), executor))
                // Quote对象存在时，对
                .map(future -> future.thenApply(Quote::parse))
                // 使用另一个异步任务构造期望的 Future ，望的 Future ，其返回的值进行转换
                .map(future ->
                        future.thenCompose(quote ->
                                CompletableFuture.supplyAsync(() ->
                                        Discount.applyDiscount(quote), executor)));

    }

}

class Shop {

    @Getter
    private String name;

    public Shop(String name) {
        this.name = name;
    }

    public String getPrice(String product) {
        Random random = new Random();
        double price = calculatePrice(product);
        Discount.Code code = Discount.Code.values()[random.nextInt(Discount.Code.values().length)];
        return String.format("%s:%.2f:%s", name, price, code);
    }

    private double calculatePrice(String product) {
        randomDelay();
        Random random = new Random();
        return random.nextDouble() * product.charAt(0) + product.charAt(1);
    }
}

class Discount {

    public enum Code {
        NONE(0), SILVER(5), GOLD(10), PLATINUM(15), DIAMOND(20);

        private final int percentage;

        Code(int percentage) {
            this.percentage = percentage;
        }
    }

    // 将折扣代码应 用于商品最初 的原始价格
    public static String applyDiscount(Quote quote) {
        return quote.getShopName() + " price is " + Discount.apply(quote.getPrice(), quote.getDiscountCode());
    }

    // 模 拟 Discount 服务响应的延迟
    private static double apply(double price, Code code) {
        randomDelay();
        return new BigDecimal(price * (100 - code.percentage) / 100).setScale(2, ROUND_HALF_EVEN).doubleValue();
    }

}

class Quote {
    @Getter
    private final String shopName;
    @Getter
    private final double price;
    @Getter
    private final Discount.Code discountCode;

    public Quote(String shopName, double price, Discount.Code code) {
        this.shopName = shopName;
        this.price = price;
        this.discountCode = code;
    }

    public static Quote parse(String s) {
        String[] split = s.split(":");
        String shopName = split[0];
        double price = Double.parseDouble(split[1]);
        Discount.Code discountCode = Discount.Code.valueOf(split[2]);
        return new Quote(shopName, price, discountCode);
    }
}


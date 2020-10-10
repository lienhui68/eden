package com.eh.eden.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * todo
 *
 * @author David Li
 * @create 2020/09/27
 */
public class Demo {
    public static void main(String[] args) throws InterruptedException {
        JedisPool jedisPool = JedisPoolUtil.getJedisPoolInstance();
        JedisPool jedisPool2 = JedisPoolUtil.getJedisPoolInstance();

        System.out.println(jedisPool == jedisPool2);

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set("aa", "bb");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JedisPoolUtil.release(jedisPool, jedis);
        }
    }
}

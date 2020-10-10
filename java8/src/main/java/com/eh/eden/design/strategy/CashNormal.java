package com.eh.eden.design.strategy;

/**
 * 正常收费子类
 *
 * @author David Li
 * @create 2020/08/12
 */
public class CashNormal implements CashSuper {
    @Override
    public double acceptCash(double original) {
        return original;
    }
}

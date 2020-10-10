package com.eh.eden.design.strategy;

/**
 * 打折收费子类
 *
 * @author David Li
 * @create 2020/08/12
 */
public class CashRebate implements CashSuper {

    private double moneyRebate = 1; // 折扣

    public CashRebate(double moneyRebate) {
        this.moneyRebate = moneyRebate;
    }

    @Override
    public double acceptCash(double original) {
        return original * moneyRebate;
    }
}

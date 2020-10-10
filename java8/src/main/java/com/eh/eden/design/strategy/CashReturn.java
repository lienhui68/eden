package com.eh.eden.design.strategy;

/**
 * 返利收费子类
 *
 * @author David Li
 * @create 2020/08/12
 */
public class CashReturn implements CashSuper {

    private double moneyCondition; // 返利条件
    private double moneyReturn; // 返利值

    public CashReturn(double moneyCondition, double moneyReturn) {
        this.moneyCondition = moneyCondition;
        this.moneyReturn = moneyReturn;
    }

    @Override
    public double acceptCash(double original) {
        double result = original;
        if (original >= moneyCondition) {
            result = original - Math.floor(original / moneyCondition) * moneyReturn;
        }
        return result;
    }
}

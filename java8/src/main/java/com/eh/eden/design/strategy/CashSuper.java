package com.eh.eden.design.strategy;

/**
 * 策略角色
 *
 * @author David Li
 * @create 2020/08/12
 */
public interface CashSuper {
    /**
     * 计算使用策略之后的金额
     *
     * @param original 原始金额
     * @return
     */
    double acceptCash(double original);
}

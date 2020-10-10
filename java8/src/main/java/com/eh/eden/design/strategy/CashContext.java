package com.eh.eden.design.strategy;

/**
 * todo
 *
 * @author David Li
 * @create 2020/08/12
 */
public class CashContext {
    private CashSuper cashSuper;

    public CashContext(CashSuper cashSuper) {
        this.cashSuper = cashSuper;
    }

    public double getResult(double money) {
        return cashSuper.acceptCash(money);
    }

}

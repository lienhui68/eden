package com.eh.eden.design.visitor;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * todo
 *
 * @author David Li
 * @create 2020/08/16
 */
public class Client {
    public static void main(String[] args) {
        AccountBook accountBook = new AccountBook();
        //添加两条收入
        accountBook.addBill(new IncomeBill(10000, "卖商品"));
        accountBook.addBill(new IncomeBill(12000, "卖广告位"));
        //添加两条支出
        accountBook.addBill(new ConsumeBill(1000, "工资"));
        accountBook.addBill(new ConsumeBill(2000, "材料费"));

        AccountBookViewer boss = new Boss();
        AccountBookViewer cpa = new CPA();
        AccountBookViewer cfo = new CFO();


        //两个访问者分别访问账本
        accountBook.show(cpa);
        accountBook.show(boss);
        accountBook.show(cfo);


        ((Boss) boss).getTotalConsume();
        ((Boss) boss).getTotalIncome();
    }
}

// 账单，相当于Element
interface Bill {
    void accept(AccountBookViewer viewer);
}

@Getter
@AllArgsConstructor
abstract class AbstractBill implements Bill {
    // 收入金额
    private double amount;

    // 款项
    private String item;
}

// 收入账单，相当于ConcreteElement
@Getter
class IncomeBill extends AbstractBill {

    public IncomeBill(double amount, String item) {
        super(amount, item);
    }

    @Override
    public void accept(AccountBookViewer viewer) {
        if (viewer instanceof AbstractViewer) {
            ((AbstractViewer) viewer).viewIncomeBill(this);
            return;
        }
        viewer.viewAbstractBill(this);
    }
}


// 支出账单，相当于ConcreteElement
@Getter
class ConsumeBill extends AbstractBill {

    public ConsumeBill(double amount, String item) {
        super(amount, item);
    }

    @Override
    public void accept(AccountBookViewer viewer) {
        if (viewer instanceof AbstractViewer) {
            ((AbstractViewer) viewer).viewConsumeBill(this);
            return;
        }
        viewer.viewAbstractBill(this);
    }
}

// 账本访问者，相当于Visitor
interface AccountBookViewer {
    void viewAbstractBill(AbstractBill bill);
}

abstract class AbstractViewer implements AccountBookViewer {
    //查看消费的单子
    abstract void viewConsumeBill(ConsumeBill bill);

    //查看收入的单子
    abstract void viewIncomeBill(IncomeBill bill);

    public final void viewAbstractBill(AbstractBill bill) {
    }
}

// 老板，相当于ConcreteVisitor
@Getter
class Boss extends AbstractViewer {

    // 总收入
    private double totalIncome;
    // 总支出
    private double totalConsume;

    @Override
    public void viewIncomeBill(IncomeBill bill) {
        this.totalIncome += bill.getAmount();
    }

    @Override
    public void viewConsumeBill(ConsumeBill bill) {
        this.totalConsume += bill.getAmount();
    }

    public double getTotalIncome() {
        System.out.println("老板查看一共收入多少，数目是：" + totalIncome);
        return totalIncome;
    }

    public double getTotalConsume() {
        System.out.println("老板查看一共花费多少，数目是：" + totalConsume);
        return totalConsume;
    }
}

// 注会，相当于ConcreteVisitor
class CPA extends AbstractViewer {

    //注会在看账本时，如果是支出，则如果支出款项是工资，则需要看应该交的税交了没否则什么都不做
    @Override
    public void viewConsumeBill(ConsumeBill bill) {
        if (bill.getItem().equals("工资")) {
            System.out.println("注会查看工资是否交个人所得税。");
        }
    }

    //如果是收入，则所有的收入都要交税
    @Override
    public void viewIncomeBill(IncomeBill bill) {
        System.out.println("注会查看收入交税了没。");
    }
}

class AccountBook {
    // 账单列表
    private List<Bill> bills = Lists.newArrayList();

    // 添加单子
    public void addBill(Bill bill) {
        bills.add(bill);
    }

    // 供张本的查看者查看张本
    public void show(AccountBookViewer viewer) {
        bills.forEach(bill -> bill.accept(viewer));
    }

}

//财务主管类，查看账本的类之一，作用于高层的层次结构
class CFO implements AccountBookViewer {

    //财务主管对每一个单子都要核对项目和金额
    public void viewAbstractBill(AbstractBill bill) {
        System.out.println("财务主管查看账本时，每一个都核对项目和金额，金额是" + bill.getAmount() + "，项目是" + bill.getItem());
    }

}

package com.orange.oy.info;

import android.text.TextUtils;

import com.orange.oy.base.Tools;

/**
 * 提现明细
 */
public class BillListDetailInfo {
    /**
     * balance : 1
     * billType : 转账
     * title : 完成任务收入费用
     * accountPk : c2bf00468b7c441b8cf30fddd93fc714
     * orderNo : 20171204120141180001716224854
     * remark : 完成任务收入费用1.0元
     * money : 1
     * state : 交易成功
     * billInfo : 17efd6f00c1740faac5087316551402b
     * type : 收入
     * createDate : 1512360101000
     * createDateStr : 2017-12-04 12:01:41
     */
    private String title;
    private String money;
    private String bill_type;
    private String exchange_time;

    public String getBill_type() {
        return bill_type;
    }

    public void setBill_type(String bill_type) {
        this.bill_type = bill_type;
    }

    public String getExchange_time() {
        return exchange_time;
    }

    public void setExchange_time(String exchange_time) {
        this.exchange_time = exchange_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        if (TextUtils.isEmpty(money)) {
            money = "-";
        } else {
            double d = Tools.StringToDouble(money);
            if (d - (int) d > 0) {
                money = String.valueOf(d);
            } else {
                money = String.valueOf((int) d);
            }
        }
        this.money = money;
    }
}

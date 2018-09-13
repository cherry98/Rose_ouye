package com.orange.oy.info;

import java.io.Serializable;

/**
 * 提现明细item集合
 */
public class WithdrawalMoneyListInfo implements Serializable {
    private String WithdrawaCode;//编号
    private String account;//支付宝帐号
    private String creatDate;//创建日期
    private String type;//状态 //订单状态:0审核中,1已提现,2已失效,3异常
    private String taxMoney;//收税金额
    private String realMoney;//实际金额
    private String money;//总金额
    private String friends;//好友列表
    private String payType;

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public WithdrawalMoneyListInfo() {
    }

    public String getWithdrawaCode() {
        return WithdrawaCode;
    }

    public void setWithdrawaCode(String withdrawaCode) {
        WithdrawaCode = withdrawaCode;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCreatDate() {
        return creatDate;
    }

    public void setCreatDate(String creatDate) {
        this.creatDate = creatDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTaxMoney() {
        return taxMoney;
    }

    public void setTaxMoney(String taxMoney) {
        this.taxMoney = taxMoney;
    }

    public String getRealMoney() {
        return realMoney;
    }

    public void setRealMoney(String realMoney) {
        this.realMoney = realMoney;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getFriends() {
        return friends;
    }

    public void setFriends(String friends) {
        this.friends = friends;
    }
}

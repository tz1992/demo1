package com.tz.demo1.entity;

public class Debt {
	//逾期时间
	public String days;
	
	// 余额
	public double balance;
	//机构简称
	public String org;
	
    public String overTime;
	
	public String getOverTime() {
		return overTime;
	}
	public void setOverTime(String overTime) {
		this.overTime = overTime;
	}
	// 客户代码
	public String clientCode;
	//抢案代码
	public String  code;
	public String getDays() {
		return days;
	}
	public void setDays(String days) {
		this.days = days;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public String getOrg() {
		return org;
	}
	public void setOrg(String org) {
		this.org = org;
	}
	public String getClientCode() {
		return clientCode;
	}
	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	
}

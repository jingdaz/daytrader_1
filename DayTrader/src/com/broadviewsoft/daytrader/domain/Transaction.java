package com.broadviewsoft.daytrader.domain;

import java.util.Date;

import com.broadviewsoft.daytrader.util.Util;

public class Transaction extends Order {

	private Date dealTime;
	private double dealPrice;
	private double commission;

	public Transaction() {

	}

	public Transaction(Order order) {
		this.stock = order.stock;
		this.orderTime = order.orderTime;
		this.txType = order.txType;
		this.orderType = order.orderType;
		this.quantity = order.quantity;
		this.dealPrice = order.costPrice;
		this.limitPrice = order.limitPrice;
		this.stopPrice = order.stopPrice;
		this.status = OrderStatus.EXECUTED;
	}

	public Date getDealTime() {
		return dealTime;
	}

	public void setDealTime(Date dealTime) {
		this.dealTime = dealTime;
	}

	public double getDealPrice() {
		return dealPrice;
	}

	public void setDealPrice(double dealPrice) {
		this.dealPrice = dealPrice;
	}

	public double getCommission() {
		return commission;
	}

	public void setCommission(double commission) {
		this.commission = commission;
	}

	public static String printHeaders(CurrencyType curType) {
		StringBuilder sb = new StringBuilder();
		sb.append("\r\nTransaction Summary\r\n");
		sb.append("Time\t\t");
		sb.append("Tx Type\t\t");
		sb.append("Symbol\t");
		sb.append("Qty\t");
		// sb.append(curType + "\t");
		sb.append("Price\t");
		sb.append("Fee\r\n");
		return sb.toString();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(Util.format(dealTime) + "\t");
		sb.append(txType + " ");
		sb.append(orderType + "\t");
		sb.append(stock.getSymbol() + "\t");
		sb.append(Util.format(quantity) + "\t");
		// FIXME curType
		sb.append(Util.format(dealPrice) + "\t");
		sb.append(Util.format(commission) + "\t");
		return sb.toString();
	}

}

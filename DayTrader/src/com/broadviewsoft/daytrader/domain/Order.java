package com.broadviewsoft.daytrader.domain;

import java.util.Date;

import com.broadviewsoft.daytrader.service.Util;

public class Order {
	protected Stock stock;
	protected Date orderTime;
	protected TransactionType txType;
	protected OrderType orderType;
	protected OrderStatus status;
	protected int quantity;

	protected double limitPrice;
	protected double stopPrice;

	// TODO
	// private Date goodTill;

	public Order() {

	}

	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	public TransactionType getTxType() {
		return txType;
	}

	public void setTxType(TransactionType txType) {
		this.txType = txType;
	}

	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	public OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getLimitPrice() {
		return limitPrice;
	}

	public void setLimitPrice(double limitPrice) {
		this.limitPrice = limitPrice;
	}

	public double getStopPrice() {
		return stopPrice;
	}

	public void setStopPrice(double stopPrice) {
		this.stopPrice = stopPrice;
	}

	public static String printHeaders(CurrencyType curType) {
		StringBuilder sb = new StringBuilder();
		sb.append("\r\nOrder Details\r\n");
		sb.append("Time\t\t");
		sb.append("Tx Type\t\t");
		sb.append("Symbol\t");
		sb.append("Qty\t");
		// sb.append(curType + "\t");
		sb.append("Limit\t");
		sb.append("Stop\t");
		return sb.toString();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(Util.format(orderTime) + "\t");
		sb.append(txType + " " + orderType + "\t");
		sb.append(stock.getSymbol() + "\t");
		sb.append(Util.format(quantity) + "\t");
		// FIXME curType
		if (limitPrice > 0) {
			sb.append(Util.format(limitPrice));
		}
		sb.append("\t");
		if (stopPrice > 0) {
			sb.append(Util.format(stopPrice));
		}
		sb.append("\t");
		return sb.toString();
	}

}

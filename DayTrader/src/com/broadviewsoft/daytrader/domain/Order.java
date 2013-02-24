package com.broadviewsoft.daytrader.domain;

public class Order {
	private Stock stock;
	private OrderType type;
	private double limitPrice;
	private double stopPrice;
	// TODO
	// private Date goodTill;
	
	
	public Order() {
		
	}

	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	public OrderType getType() {
		return type;
	}

	public void setType(OrderType type) {
		this.type = type;
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
	
	

}

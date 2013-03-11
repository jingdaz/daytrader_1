package com.broadviewsoft.daytrader.domain;

import java.util.Date;

public class StockPrice {
	private Stock stock;
	private Date dealTime;
	private double price;

	public StockPrice() {

	}

	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	public Date getDealTime() {
		return dealTime;
	}

	public void setDealTime(Date dealTime) {
		this.dealTime = dealTime;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

}

package com.broadviewsoft.daytrader.domain;

import com.broadviewsoft.daytrader.util.Util;

public class StockHolding {
	private Stock stock;
	private int quantity;
	private double avgPrice;

	public StockHolding() {

	}

	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getAvgPrice() {
		return avgPrice;
	}

	public void setAvgPrice(double avgPrice) {
		this.avgPrice = avgPrice;
	}

	public String toString() {
	  StringBuilder sb = new StringBuilder();
    sb.append(stock.getSymbol() + "\t");
    sb.append(Util.format(quantity) + "\t");
    sb.append("@" + Util.format(avgPrice) + "\r\n");
    return sb.toString();
	}
}

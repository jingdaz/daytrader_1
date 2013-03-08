package com.broadviewsoft.daytrader.domain;

import java.util.Date;

public class StockItem {
	private Stock stock;
	private Date timestamp;
	private Period period;
	private double open;
	private double high;
	private double low;
	private double close;
	private double rsi;
	private double cci;
	private long volume;
	
	public StockItem() {
		stock = new Stock("UVXY");
		period = Period.MIN5;
	}

	public StockItem(String symbol, Period period) {
		stock = new Stock(symbol);
		this.period = period;
	}

	public Stock getStock() {
		return stock;
	}


	public void setStock(Stock stock) {
		this.stock = stock;
	}


	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public double getOpen() {
		return open;
	}

	public void setOpen(double open) {
		this.open = open;
	}

	public double getHigh() {
		return high;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public double getLow() {
		return low;
	}

	public void setLow(double low) {
		this.low = low;
	}

	public double getClose() {
		return close;
	}

	public void setClose(double close) {
		this.close = close;
	}

	public double getRsi() {
		return rsi;
	}

	public void setRsi(double rsi) {
		this.rsi = rsi;
	}

	public double getCci() {
		return cci;
	}

	public void setCci(double cci) {
		this.cci = cci;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	public static String format(double price) {
		return Constants.STOCK_PRICE_FORMATTER.format(price);
	}
	
	public static String format(long price) {
		return Constants.STOCK_VOLUME_FORMATTER.format(price);
	}

	public static String printHeaders(CurrencyType curType, String symbol, Period period) {
		StringBuilder sb = new StringBuilder();
		sb.append(curType + "\t");
		sb.append(symbol + "\t");
		sb.append(period + "\r\t");
		sb.append("Timestamp\t\t");
		sb.append("Open\t");
		sb.append("High\t");
		sb.append("Low\t");
		sb.append("Close\t");
		sb.append("RSI\t");
		sb.append("CCI\t");
		sb.append("Volume\t");
		return sb.toString();
	}


	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(timestamp + "\t");
		sb.append(format(open) + "\t");
		sb.append(format(high) + "\t");
		sb.append(format(low) + "\t");
		sb.append(format(close) + "\t");
		sb.append(format(rsi) + "\t");
		sb.append(format(cci) + "\t");
		sb.append(format(volume) + "\t");
		return sb.toString();
	}

}

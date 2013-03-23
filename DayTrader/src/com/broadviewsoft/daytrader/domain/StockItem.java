package com.broadviewsoft.daytrader.domain;

import java.util.Date;

import com.broadviewsoft.daytrader.util.Util;

public class StockItem implements Comparable<Object> {
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

	public double getTypical() {
	  return (high + low + close)/3;
	}
	
	public static String printHeaders(CurrencyType curType, String symbol,
			Period period) {
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
		sb.append(Util.format(open) + "\t");
		sb.append(Util.format(high) + "\t");
		sb.append(Util.format(low) + "\t");
		sb.append(Util.format(close) + "\t");
		sb.append(Util.format(rsi) + "\t");
		sb.append(Util.format(cci) + "\t");
		sb.append(Util.format(volume) + "\t");
		return sb.toString();
	}

  public int compareTo(Object o)
  {
    if (!(o instanceof StockItem)) {
      throw new ClassCastException("Comparing objects with different types.");
    }
    StockItem other = (StockItem) o;
    if (this.getCci() > other.getCci()) {
      return 1;
    }
    
    if (this.getCci() < other.getCci()) {
      return -1;
    }
    
    return 0;
  }

  public String toString(DataFileType type) {
		StringBuilder sb = new StringBuilder();
		sb.append(Util.format(timestamp) + Constants.CSV_SEPARATOR);
		sb.append(Util.format(open) + Constants.CSV_SEPARATOR);
		sb.append(Util.format(high) + Constants.CSV_SEPARATOR);
		sb.append(Util.format(low) + Constants.CSV_SEPARATOR);
		sb.append(Util.format(close) + Constants.CSV_SEPARATOR);
		if (type == DataFileType.BVS) {
      sb.append(Util.format(rsi) + Constants.CSV_SEPARATOR);
			sb.append(Util.format(cci) + Constants.CSV_SEPARATOR);
		}
		sb.append(Util.format(volume) + "\r\n");
		return sb.toString();
  }

  public String toBVSString() {
		StringBuilder sb = new StringBuilder();
		sb.append(Util.format(timestamp) + Constants.CSV_SEPARATOR);
		sb.append(Util.format(open) + Constants.CSV_SEPARATOR);
		sb.append(Util.format(high) + Constants.CSV_SEPARATOR);
		sb.append(Util.format(low) + Constants.CSV_SEPARATOR);
		sb.append(Util.format(close) + Constants.CSV_SEPARATOR);
		sb.append(Util.format(volume) + Constants.CSV_SEPARATOR);
		sb.append(Util.format(rsi) + Constants.CSV_SEPARATOR);
		sb.append(Util.format(cci) +  "\r\n");
		return sb.toString();
  }

}

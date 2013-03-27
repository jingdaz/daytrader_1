package com.broadviewsoft.daytrader.domain;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;

public class StockStatus {
	private Stock stock = null;
	private int capacity = Constants.STATUS_QUEUE_DEPTH;
	private Date timestamp = null;
	private StockItem ytaItem = null;
	private StockItem preHigh = null;
	private StockItem preLow = null;
	private StockItem curItem = null;
	private LinkedList<StockItem> chartItems = new LinkedList<StockItem>();

	public StockStatus() {
	}

	public StockStatus(String symbol, Date timestamp) {
		this(new Stock(symbol), timestamp);
	}

	public StockStatus(Stock stock, Date timestamp) {
		this.stock = stock;
		this.timestamp = timestamp;
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

	public StockItem getYtaItem() {
		return ytaItem;
	}

	public void setYtaItem(StockItem ytaItem) {
		this.ytaItem = ytaItem;
	}

	public StockItem getPreHigh() {
		return preHigh;
	}

	public void setPreHigh(StockItem preHigh) {
		this.preHigh = preHigh;
	}

	public StockItem getPreLow() {
		return preLow;
	}

	public void setPreLow(StockItem preLow) {
		this.preLow = preLow;
	}

	public StockItem getCurItem() {
		return curItem;
	}

	public void setCurItem(StockItem curItem) {
		this.curItem = curItem;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setChartItems(LinkedList<StockItem> items) {
		if (items.size() > capacity) {
			this.chartItems = new LinkedList<StockItem>(items.subList(
					items.size() - capacity, items.size()));
		} else {
			this.chartItems = items;
		}
	}

	public void addChartItem(StockItem item) {
		if (chartItems.size() >= capacity) {
			chartItems.poll();
			chartItems.add(item);
		}
	}

	public boolean dropsTopDvg() {
		for (int i = 0; i < capacity - 2; i++) {
			if (chartItems.get(i + 1).compareTo(chartItems.get(i)) < 0) {
				return false;
			}
		}

		// drops from CCI >= 100/2
		return ((chartItems.get(capacity - 1).compareTo(
				chartItems.get(capacity - 2)) < 0) && chartItems.get(
				capacity - 2).getCci() > Constants.CCI_TOP_DIVERGENCE / 2);
	}

	public boolean picksBtmDvg() {
		for (int i = 0; i < capacity - 2; i++) {
			if (chartItems.get(i + 1).compareTo(chartItems.get(i)) > 0) {
				return false;
			}
		}

		// picks up from CCI <= -100/2
		return ((chartItems.get(capacity - 1).compareTo(
				chartItems.get(capacity - 2)) > 0) && chartItems.get(
				capacity - 2).getCci() < Constants.CCI_BOTTOM_DIVERGENCE / 2);
	}

	public boolean crossUp() {
		double preClose = ytaItem.getClose();
		// within 60 minutes, preLow drops 6%+ from yesterday close, curItem
		// rises 5% from preLow
		if ((preLow.getLow() / preClose - 1.0) < Constants.PRICE_CROSS_UP_PRELOW_FACTOR
				&& (curItem.getTypical() / preLow.getLow() - 1.0) > Constants.PRICE_CROSS_UP_PRELOW_FACTOR
				&& (curItem.getTimestamp().getTime() - preLow.getTimestamp()
						.getTime()) < Constants.PRICE_CROSS_UP_TIME_INTERVAL) {
			return true;
		}
		return false;
	}

	public boolean crossDown() {
		double preClose = ytaItem.getClose();
		// within 60 minutes, preHigh rises 5%+ from yesterday close, curItem
		// drops 4% from preHigh
		if ((preHigh.getHigh() / preClose - 1.0) > Constants.PRICE_CROSS_DOWN_PREHIGH_FACTOR
				&& (curItem.getTypical() / preHigh.getHigh() - 1.0) < Constants.PRICE_CROSS_DOWN_PREHIGH_FACTOR
				&& (curItem.getTimestamp().getTime() - preHigh.getTimestamp()
						.getTime()) < Constants.PRICE_CROSS_DOWN_TIME_INTERVAL) {
			return true;
		}
		return false;
	}
	
	public boolean isWeakest() {
		Calendar cal = new GregorianCalendar();
	    cal.setTime(timestamp);
	    int hour = cal.get(Calendar.HOUR_OF_DAY);
	    return (hour > 12 && preHigh.getCci() < Constants.CCI_WEAKEST_LIMIT);
	}

	public boolean isStrongest() {
		Calendar cal = new GregorianCalendar();
	    cal.setTime(timestamp);
	    int hour = cal.get(Calendar.HOUR_OF_DAY);
	    return (hour > 12 && preLow.getCci() > Constants.CCI_STRONGEST_LIMIT);
	}

	public boolean isSuperLowOpen() {
	  Calendar cal = new GregorianCalendar();
    cal.setTime(timestamp);
    int hour = cal.get(Calendar.HOUR_OF_DAY);
    int min = cal.get(Calendar.MINUTE);
    int statusInMins = 60 * hour + min;
	  return
	  curItem.getCci() < Constants.CCI_SUPER_LOW_LIMIT
	  && statusInMins >= Constants.CCI_SUPER_OPEN_START_TIME 
	  && statusInMins <= Constants.CCI_SUPER_OPEN_END_TIME;
	}

  public boolean isSuperHighOpen() {
    Calendar cal = new GregorianCalendar();
    cal.setTime(timestamp);
    int hour = cal.get(Calendar.HOUR_OF_DAY);
    int min = cal.get(Calendar.MINUTE);
    int statusInMins = 60 * hour + min;
    return
    curItem.getCci() > Constants.CCI_SUPER_HIGH_LIMIT
    && statusInMins >= Constants.CCI_SUPER_OPEN_START_TIME 
    && statusInMins <= Constants.CCI_SUPER_OPEN_END_TIME;
  }

}

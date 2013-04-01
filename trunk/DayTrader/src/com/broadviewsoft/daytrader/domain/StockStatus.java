package com.broadviewsoft.daytrader.domain;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import com.broadviewsoft.daytrader.util.Util;

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
	  // check with 4 items; first three up
	  // all adjacent items differ < 250
		for (int i = 0; i < capacity - 2; i++) {
			if (chartItems.get(i+1).compareTo(chartItems.get(i)) < 0
			    || Math.abs(chartItems.get(i+1).getCci()-chartItems.get(i).getCci()) > Constants.CCI_DIFF_LIMIT) {
				return false;
			}
		}

		// last item drops from CCI >= 100/2
		// all adjacent items change > 10% and absolute difference < 250
		return chartItems.get(capacity-1).compareTo(chartItems.get(capacity-2)) < 0
        && chartItems.get(capacity-2).getCci() > Constants.CCI_TOP_DIVERGENCE / 2
		    && Math.abs(chartItems.get(capacity-1).getCci()-chartItems.get(capacity-2).getCci()) < Constants.CCI_DIFF_LIMIT
				&& Math.abs(chartItems.get(capacity-2).getCci()/chartItems.get(capacity-1).getCci()) > Constants.CCI_CROSS_UP_DIFF_FACTOR_LIMIT;
	}

	public boolean picksBtmDvg() {
	   // check with 4 items; first three down
    // all adjacent items differ < 250
		for (int i = 0; i < capacity - 2; i++) {
			if (chartItems.get(i+1).compareTo(chartItems.get(i)) > 0
			    || Math.abs(chartItems.get(i+1).getCci()-chartItems.get(i).getCci()) > Constants.CCI_DIFF_LIMIT) {
				return false;
			}
		}

		// picks up from CCI <= -100/2 -- not in use
		// all adjacent items change > 10% and absolute difference < 250
		return chartItems.get(capacity-1).compareTo(chartItems.get(capacity-2)) > 0
//        && chartItems.get(capacity-2).getCci() < Constants.CCI_BOTTOM_DIVERGENCE / 2
		    && Math.abs(chartItems.get(capacity-1).getCci()-chartItems.get(capacity-2).getCci()) < Constants.CCI_DIFF_LIMIT
				&& Math.abs(chartItems.get(capacity-2).getCci()/chartItems.get(capacity-1).getCci()) > Constants.CCI_CROSS_UP_DIFF_FACTOR_LIMIT;
	}
	
	public boolean turningPointBelowFair() {
	  return chartItems.get(capacity-2).getCci() < Constants.CCI_BOTTOM_DIVERGENCE / 2;
	}

  public boolean turningPointAboveFair() {
    return chartItems.get(capacity-2).getCci() > Constants.CCI_TOP_DIVERGENCE / 2;
  }

  public boolean isCciBigSlope() {
    return curItem.getCci() - preLow.getCci() > Constants.CCI_ZERO_AXIS_LIMIT;  
  }
  
	public boolean crossUp() {
		double preClose = ytaItem.getClose();
		// within 60 minutes, preLow drops 6%+ from yesterday close, 
		// curItem rises 5%+ from preLow
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
		// within 60 minutes, preHigh rises 5%+ from yesterday close, 
		// curItem drops 4%+ from preHigh
		if ((preHigh.getHigh() / preClose - 1.0) > Constants.PRICE_CROSS_DOWN_PREHIGH_FACTOR
				&& (curItem.getTypical() / preHigh.getHigh() - 1.0) < Constants.PRICE_CROSS_DOWN_CURITEM_FACTOR
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
	    // until afternoon, CCI never reaches above 25
	    return (hour > 12 && preHigh.getCci() < Constants.CCI_WEAKEST_LIMIT);
	}

	public boolean isStrongest() {
		Calendar cal = new GregorianCalendar();
	    cal.setTime(timestamp);
	    int hour = cal.get(Calendar.HOUR_OF_DAY);
	    // until afternoon, CCI never reaches below -25
	    return (hour > 12 && preLow.getCci() > Constants.CCI_STRONGEST_LIMIT);
	}

	public boolean isSuperLowOpen() {
    // Within first 5 minutes before 10AM, CCI below -300
    int lowMins = Util.marketOpenMins(preLow.getTimestamp());
    int curMins = Util.marketOpenMins(curItem.getTimestamp());
    return
    preLow.getCci() < Constants.CCI_SUPER_LOW_LIMIT
    && lowMins >= Constants.CCI_SUPER_OPEN_START_TIME 
    && lowMins <= Constants.CCI_SUPER_OPEN_END_TIME
    && curMins <= Constants.CCI_SUPER_OPEN_LIMIT_TIME;
	}

  public boolean isSuperHighOpen() {
    // Within first 5 minutes before 10AM, CCI above 300
    int highMins = Util.marketOpenMins(preHigh.getTimestamp());
    int curMins = Util.marketOpenMins(curItem.getTimestamp());
    return
    preHigh.getCci() > Constants.CCI_SUPER_HIGH_LIMIT
    && highMins >= Constants.CCI_SUPER_OPEN_START_TIME 
    && highMins <= Constants.CCI_SUPER_OPEN_END_TIME
    && curMins <= Constants.CCI_SUPER_OPEN_LIMIT_TIME;
  }

  public boolean isSellingRange() {
    // CCI in range 100 ~ 200
    return curItem.getCci() < Constants.CCI_TOP_SELL_LIMIT
    && curItem.getCci() > Constants.CCI_TOP_DIVERGENCE;
  }
  
  public boolean isBuyingRange() {
    // CCI in range -120 ~ -100
    return curItem.getCci() < Constants.CCI_BOTTOM_DIVERGENCE
    && curItem.getCci() > Constants.CCI_BOTTOM_BUY_LIMIT;
  }
  
  public boolean isRsiOverSold() {
    return curItem.getRsi() < Constants.RSI_OVERSOLD_LIMIT;
  }

  public boolean isRsiOverBought() {
    return curItem.getRsi() > Constants.RSI_OVERBOUGHT_LIMIT;
  }

  public boolean isRsiReversed()
  {
    return chartItems.get(capacity-1).getRsi() > chartItems.get(capacity-2).getRsi();
  }

}

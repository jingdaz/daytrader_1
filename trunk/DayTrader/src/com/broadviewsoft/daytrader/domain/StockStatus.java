package com.broadviewsoft.daytrader.domain;

import java.util.Date;
import java.util.LinkedList;

public class StockStatus {
  private int capacity = Constants.STATUS_QUEUE_DEPTH;;
	private Date timestamp = null;
	private StockItem preHigh = null;
	private StockItem preLow = null;
	private StockItem curItem = null;
	private LinkedList<StockItem> chartItems = new LinkedList<StockItem>();

	public StockStatus() {
		this(new Date());
	}

	public StockStatus(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	
  public StockItem getPreHigh()
  {
    return preHigh;
  }

  public void setPreHigh(StockItem preHigh)
  {
    this.preHigh = preHigh;
  }

  public StockItem getPreLow()
  {
    return preLow;
  }

  public void setPreLow(StockItem preLow)
  {
    this.preLow = preLow;
  }

  public StockItem getCurItem()
  {
    return curItem;
  }

  public void setCurItem(StockItem curItem)
  {
    this.curItem = curItem;
  }

  public int getCapacity()
  {
    return capacity;
  }
  
 
public void setChartItems(LinkedList<StockItem> items)
  {
    if (items.size() > capacity) {
      this.chartItems = new LinkedList<StockItem>(items.subList(items.size()-capacity, items.size()));
    }
    else {
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
	  for (int i=0; i<capacity-2; i++) {
	     if (chartItems.get(i+1).compareTo(chartItems.get(i)) < 0) {
	       return false;
	     }
	  }

	  // drops from CCI >= 100/2
	  return ((chartItems.get(capacity-1).compareTo(chartItems.get(capacity-2)) < 0)
	  && chartItems.get(capacity-2).getCci() > Constants.CCI_TOP_DIVERGENCE/2);
	}
	
	public boolean picksBtmDvg() {
    for (int i=0; i<capacity-2; i++) {
      if (chartItems.get(i+1).compareTo(chartItems.get(i)) > 0) {
        return false;
      }
   }

   // picks up from CCI <= -100/2
   return ((chartItems.get(capacity-1).compareTo(chartItems.get(capacity-2)) > 0)
   && chartItems.get(capacity-2).getCci() < Constants.CCI_BOTTOM_DIVERGENCE/2);
	}
}

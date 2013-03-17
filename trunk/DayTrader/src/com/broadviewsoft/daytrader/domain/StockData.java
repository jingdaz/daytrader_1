package com.broadviewsoft.daytrader.domain;

import java.util.ArrayList;
import java.util.List;

public class StockData
{
  private Stock stock;
  
  List<StockItem> mins = new ArrayList<StockItem>();
  List<StockItem> min5s = new ArrayList<StockItem>();
  List<StockItem> min15s = new ArrayList<StockItem>();
  List<StockItem> hours = new ArrayList<StockItem>();
  List<StockItem> days = new ArrayList<StockItem>();
  List<StockItem> weeks = new ArrayList<StockItem>();
  
  public StockData() {
    
  }

  public Stock getStock()
  {
    return stock;
  }

  public void setStock(Stock stock)
  {
    this.stock = stock;
  }

  public List<StockItem> getMins()
  {
    return mins;
  }

  public void setMins(List<StockItem> mins)
  {
    this.mins = mins;
  }

  public List<StockItem> getMin5s()
  {
    return min5s;
  }

  public void setMin5s(List<StockItem> min5s)
  {
    this.min5s = min5s;
  }

  public List<StockItem> getMin15s()
  {
    return min15s;
  }

  public void setMin15s(List<StockItem> min15s)
  {
    this.min15s = min15s;
  }

  public List<StockItem> getHours()
  {
    return hours;
  }

  public void setHours(List<StockItem> hours)
  {
    this.hours = hours;
  }

  public List<StockItem> getDays()
  {
    return days;
  }

  public void setDays(List<StockItem> days)
  {
    this.days = days;
  }

  public List<StockItem> getWeeks()
  {
    return weeks;
  }

  public void setWeeks(List<StockItem> weeks)
  {
    this.weeks = weeks;
  }
  
  
  
}
 

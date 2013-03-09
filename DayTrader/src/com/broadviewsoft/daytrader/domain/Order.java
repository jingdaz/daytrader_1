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

	public Date getOrderTime()
  {
    return orderTime;
  }

  public void setOrderTime(Date orderTime)
  {
    this.orderTime = orderTime;
  }

  public TransactionType getTxType()
  {
    return txType;
  }

  public void setTxType(TransactionType txType)
  {
    this.txType = txType;
  }

  public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}


	public OrderType getOrderType()
  {
    return orderType;
  }

  public void setOrderType(OrderType orderType)
  {
    this.orderType = orderType;
  }

  public OrderStatus getStatus()
  {
    return status;
  }

  public void setStatus(OrderStatus status)
  {
    this.status = status;
  }

  public int getQuantity()
  {
    return quantity;
  }

  public void setQuantity(int quantity)
  {
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
	
	public String toString() {
	  StringBuilder sb = new StringBuilder();
    sb.append(orderTime + "\t");
    sb.append(txType + "\t");
    sb.append(orderType + "\t");
    sb.append(stock.getSymbol() + "\t");
    sb.append(Util.format(quantity) + "\t");
    sb.append(status + "\t");
    // FIXME curType
    if (limitPrice != 0) {
      sb.append("Limit " + Util.format(limitPrice) + "\t");      
    }
    if (stopPrice != 0) {
      sb.append("Stop " + Util.format(stopPrice) + "\t");      
    }
    return sb.toString();
	}

}

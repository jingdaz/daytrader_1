package com.broadviewsoft.daytrader.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.util.Util;

public class Account
{
  private static Log logger = LogFactory.getLog(Account.class);

  private long acctNbr = 0;

  private CurrencyType currencyType = CurrencyType.USD;

  private double cashAmount = 0;

  private double initialAmount = 0;

  private List<StockHolding> holdings = new ArrayList<StockHolding>();

  private List<Order> orders = new ArrayList<Order>();

  private List<Transaction> transactions = new ArrayList<Transaction>();
  // sorted by keys
  private Map<Date, Double> dailyProfits = new TreeMap<Date, Double>();

  public Account()
  {
	  acctNbr = Constants.DEFAULT_ACCOUNT_NUMBER;
  }

  public void init(double preClose)
  {
    cashAmount = Constants.INIT_CASH_AMOUNT;
    for (int i = 0; i < Constants.INIT_STOCK_SYMBOLS.length; i++)
    {
      if (Constants.INIT_STOCK_VOLUMES[i] > 0)
      {
        Stock stock = new Stock(Constants.INIT_STOCK_SYMBOLS[i]);
        StockHolding sh = new StockHolding();
        sh.setStock(stock);
        sh.setQuantity(Constants.INIT_STOCK_VOLUMES[i]);
        sh.setAvgPrice(preClose);
        holdings.add(sh);
      }
    }
    // record initial amount in total
    initialAmount = getTotal(preClose);
  }

  public void flush()
  {
//    this.cashAmount = Constants.INIT_CASH_AMOUNT;
    initialAmount = 0;
    for (Order order : orders) {
      if (order.getStatus() == OrderStatus.OPEN) {
        order.setStatus(OrderStatus.EXPIRED);
      }
    }
//    this.holdings.clear();
  }

  /**
   * Place an Sell/Buy order
   * 
   * @param now order time
   * @param order order details
   * @return result if order has been placed successfully
   */
  public boolean placeOrder(Date now, Order order)
  {
    // check available fund before placing buy orders
    // TODO compare available fund to order amount
    if (cashAmount <= 0) {
      logger.info("[" + Util.format(now) + "] Insufficient fund available: " + cashAmount + ", order rejected.");
      order.setStatus(OrderStatus.REJECTED);
      return false;
    }

    Iterator<Order> it = orders.listIterator();
    while (it.hasNext())
    {
      Order o = it.next();
      if (o.getTxType() == order.getTxType() && o.getStatus() == OrderStatus.OPEN)
      {
        // it.remove();
        o.setStatus(OrderStatus.CANCELLED);
        logger.info("[" + Util.format(now) + "] Cancelling order of same type " + o);
      }
    }
    orders.add(order);
    logger.info("Placing order " + order);
    return true;
  }

  public long getAcctNbr()
  {
    return acctNbr;
  }

  public void setAcctNbr(long acctNbr)
  {
    this.acctNbr = acctNbr;
  }

  public CurrencyType getCurrencyType()
  {
    return currencyType;
  }

  public void setCurrencyType(CurrencyType currencyType)
  {
    this.currencyType = currencyType;
  }

  public double getCashAmount()
  {
    return cashAmount;
  }

  public void setCashAmount(double cashAmount)
  {
    this.cashAmount = cashAmount;
  }

  public List<StockHolding> getHoldings()
  {
    return holdings;
  }

  public void setHoldings(List<StockHolding> holdings)
  {
    this.holdings = holdings;
  }

  public List<Order> getOrders()
  {
    return orders;
  }

  public void setOrders(List<Order> orders)
  {
    this.orders = orders;
  }

  public List<Transaction> getTransactions()
  {
    return transactions;
  }

  public void setTransactions(List<Transaction> transactions)
  {
    this.transactions = transactions;
  }

  public StockHolding getHolding(Stock stock)
  {
    for (StockHolding sh : holdings)
    {
      if (sh.getStock().getSymbol().equalsIgnoreCase(stock.getSymbol()))
      {
        return sh;
      }
    }
    return null;
  }

  public void updateHoldings(Transaction tx)
  {
    Stock stock = tx.getStock();
    TransactionType type = tx.getTxType();
    double dealPrice = tx.getDealPrice();
    int qty = tx.getQuantity();

    // in hand already
    if (isInHolding(tx.getStock()))
    {
      Iterator<StockHolding> it = holdings.listIterator();
      // empty first?
      while (it.hasNext())
      {
        StockHolding sh = it.next();
        if (sh.getStock().getSymbol().equalsIgnoreCase(stock.getSymbol()))
        {
          switch (type)
          {
            case BUY:
              cashAmount -= qty * dealPrice;
              double avgPrice = (sh.getAvgPrice() * sh.getQuantity() + dealPrice * qty) / (sh.getQuantity() + qty);
              sh.setAvgPrice(avgPrice);
              sh.setQuantity(sh.getQuantity() + qty);
              break;

            case SELL:
              if (qty > sh.getQuantity())
              {
                logger.info("Selling quantity exceeds holding in account: " + qty);
              }
              else
              {
                cashAmount += qty * dealPrice;
                int remaining = sh.getQuantity() - qty;
                if (remaining == 0)
                {
                  it.remove();
                }
                else
                {
                  sh.setQuantity(remaining);
                }

              }
              break;
          }
        }
      }
    }

    // not in hand yet
    else
    {
      switch (type)
      {
        case BUY:
          cashAmount -= qty * dealPrice;
          StockHolding sh = new StockHolding();
          sh.setStock(tx.getStock());
          sh.setAvgPrice(tx.getDealPrice());
          sh.setQuantity(tx.getQuantity());
          holdings.add(sh);
          break;

        case SELL:
          logger.error("Error occurred when attempting to sell non-existing stock.");
          break;
      }
    }

  }

  private boolean isInHolding(Stock stock)
  {
    for (StockHolding sh : holdings)
    {
      if (sh.getStock().getSymbol().equalsIgnoreCase(stock.getSymbol()))
      {
        return true;
      }
    }

    return false;
  }

  public double getTotal(double price)
  {
    double total = cashAmount;
    for (StockHolding sh : holdings)
    {
      total += sh.getQuantity() * price;
    }
    return total;
  }

  public void showHoldings(Date date, double price)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("Holdings");
    
    for (StockHolding sh : holdings)
    {
      sb.append("\r\nSymbol\tQty\tPrice\t\t");
    }
    sb.append("\r\n");
    for (StockHolding sh : holdings)
    {
      sb.append(sh.getStock().getSymbol() + "\t" + sh.getQuantity() + "\t" + Util.format(sh.getAvgPrice()) + "\t\t");
    }
    if (!holdings.isEmpty()) {
      sb.append("\r\n");
    }
    
    sb.append("Cash\t\t");
    sb.append("Total\t\t");
    sb.append("Fee\t\t");
    sb.append("Profit");
    sb.append("\r\n");

    sb.append(Util.format(cashAmount) + "\t");
    if (Math.abs(cashAmount) < 10000 
        && Math.abs(cashAmount) > 1000
        && cashAmount > 0)
    {
      sb.append("\t");
    }

    double totalAmount = getTotal(price);
    sb.append(Util.format(totalAmount) + "\t");
    if (totalAmount < 10000)
    {
      sb.append("\t");
    }
    
    double fees = 0;
    for (Transaction tx : transactions) {
      if (Util.roundup(tx.getDealTime()).compareTo(date) == 0) {
        fees += Constants.COMMISSION_FEE;
      }
    }
    if (fees > 0)
    {
      sb.append(Util.format(fees));
    }
    sb.append("\t\t");
    double profit = totalAmount - initialAmount - fees;
    if (Math.abs(profit) > 1)
    {
      sb.append(Util.format(profit));
    }
    sb.append("\r\n");
    logger.info(sb.toString());
  }

  public void showAllOrders()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(Order.printHeaders(CurrencyType.USD));

    // print out transaction details
    for (Order order : orders)
    {
      sb.append(order + "\r\n");
    }
    logger.info(sb.toString());
  }

  public void showOrders(Date date)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(Order.printHeaders(CurrencyType.USD));

    // print out transaction details
    for (Order order : orders)
    {
      if (Util.roundup(order.getOrderTime()).compareTo(date) == 0) {
        sb.append(order + "\r\n");
      }
    }
    logger.info(sb.toString());
  }

  public void showAllTransactions()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(Transaction.printHeaders(CurrencyType.USD));

    // print out transaction details
    for (Transaction tx : transactions)
    {
      sb.append(tx + "\r\n");
    }
    logger.info(sb.toString());
  }

  
  public void showTransactions(Date date)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(Transaction.printHeaders(CurrencyType.USD));

    // print out transaction details
    for (Transaction tx : transactions)
    {
        if (Util.roundup(tx.getDealTime()).compareTo(date) == 0) {
        sb.append(tx + "\r\n");
      }
    }
    logger.info(sb.toString());
  }

  
  public void showProfits()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("Daily Profit\r\n");
    // print out transaction details
    double total = 0;
    for (Entry<Date, Double> entry : dailyProfits.entrySet())
    {
      sb.append(Util.format(entry.getKey()) + " | " + Util.format(entry.getValue()) + "\r\n");
      total += Util.trim(entry.getValue().doubleValue());
    }
    sb.append("Total Profit: " + Util.format(total));
    logger.info(sb.toString());
  }

  public void updateProfit(Date date, double price) {
    double totalAmount = getTotal(price);
    double fees = 0;
    for (Transaction tx : transactions) {
      if (Util.roundup(tx.getDealTime()).compareTo(date) == 0) {
        fees += Constants.COMMISSION_FEE;
      }
    }
    double profit = totalAmount - initialAmount - fees;
    dailyProfits.put(date, Double.valueOf(profit));
  }
}

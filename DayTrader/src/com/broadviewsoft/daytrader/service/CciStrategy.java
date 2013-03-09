package com.broadviewsoft.daytrader.service;

import java.util.Random;

import com.broadviewsoft.daytrader.domain.Account;
import com.broadviewsoft.daytrader.domain.Order;
import com.broadviewsoft.daytrader.domain.OrderStatus;
import com.broadviewsoft.daytrader.domain.OrderType;
import com.broadviewsoft.daytrader.domain.Stock;
import com.broadviewsoft.daytrader.domain.StockStatus;
import com.broadviewsoft.daytrader.domain.TransactionType;

public class CciStrategy extends TradeStrategy
{

  public void execute(StockStatus status, Account account)
  {
    // status one, account one
    Random random = new Random(System.currentTimeMillis());
    int n = random.nextInt(100);
    if (n < 70) {
      if (account.getHoldings()!=null && account.getHoldings().isEmpty()) {
        System.out.println("Placing market buy order @ " + status.getTimestamp());
        Order order = new Order();
        order.setOrderTime(status.getTimestamp());
        order.setStock(new Stock("UVXY"));
        order.setStatus(OrderStatus.OPEN);
        order.setTxType(TransactionType.BUY);
        order.setOrderType(OrderType.MARKET);
        order.setQuantity(1000);
        account.placeOrder(order);
      }
    }
    
    else if (n > 50) {
      if (account.getHoldings()!=null && !account.getHoldings().isEmpty()) {
        System.out.println("Placing limit sell order... @ " + status.getTimestamp());
        Order order = new Order();
        order.setOrderTime(status.getTimestamp());
        order.setStock(new Stock("UVXY"));
        order.setTxType(TransactionType.SELL);
        order.setStatus(OrderStatus.OPEN);
        order.setOrderType(OrderType.LIMIT);
        double targetPrice = 10.0 + random.nextInt(10)/50.0;
        order.setLimitPrice(targetPrice);
        order.setQuantity(1000);
        account.placeOrder(order);
      }
    }

    // status two, account two
    

  }

}
 

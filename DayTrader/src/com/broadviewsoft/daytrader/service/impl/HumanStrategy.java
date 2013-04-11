package com.broadviewsoft.daytrader.service.impl;

import java.util.Date;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.domain.Account;
import com.broadviewsoft.daytrader.domain.Order;
import com.broadviewsoft.daytrader.domain.OrderType;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.StockHolding;
import com.broadviewsoft.daytrader.domain.StockStatus;
import com.broadviewsoft.daytrader.domain.TransactionType;
import com.broadviewsoft.daytrader.service.DataFeederFactory;
import com.broadviewsoft.daytrader.service.TradeStrategy;
import com.broadviewsoft.daytrader.util.Util;

public class HumanStrategy extends TradeStrategy {
	private static Log logger = LogFactory.getLog(HumanStrategy.class);

	public HumanStrategy() {
		period = Period.MIN5;
		dataFeeder = DataFeederFactory.newInstance();
	}

	public void execute(StockStatus stockStatus, Account account) {
		logger.info("Executing on " + Util.format(stockStatus.getTimestamp()));
		StockHolding holding = null;
		for (StockHolding sh : account.getHoldings()) {
		  if (stockStatus.getStock().eqauls(sh.getStock())) {
		    System.out.println("Holding " + sh);
		  }
		}
		
		System.out.println(stockStatus);
		
		if (stockStatus.isWeakest()) {
      logger.info("Weakest market; cautious to jump in!");
    }
		
    if (stockStatus.isStrongest()) {
      logger.info("Strongest market; hold your chips tight!");
    }

		if (stockStatus.isSuperLowOpen()) {
      logger.info("Super low open; buy from CCI -120 ~ -100!");
    }
		
		if (stockStatus.isSuperHighOpen()) {
      logger.info("Super high open; sell from -2% of highest!");
    }
		
		if (stockStatus.crossUp()) {
			logger.info("Price cross up; notice yesterday close/high, 10% of lowest!");
		}

		if (stockStatus.crossDown()) {
			logger.info("Price cross down; wait until -10% of highest!");
		}
		
		if (stockStatus.dropsTopDvg()) {
			logger.info("Price drops from top divergance!");
		}

		if (stockStatus.picksBtmDvg()) {
			logger.info("Price picks up from bottom divergance!");
		}

    Scanner scanIn = new Scanner(System.in);
		TransactionType txType = null; 
		OrderType orType = null;
		double stopPrice = -1;
		double limitPrice = -1;
		int qty = -1;
		Order order = null;
		String input = null;

		do
    {
      System.out.println("Buy or Sell or Pass?");
      input = scanIn.next().trim();
      if (input == null || "".equals(input) || input.startsWith("P") || input.startsWith("p"))
      {
        return;
      }
      if (input.startsWith("B") || input.startsWith("b"))
      {
        txType = TransactionType.BUY;
      }
      else if (input.startsWith("S") || input.startsWith("s"))
      {
        txType = TransactionType.SELL;
      }
    } while (txType == null);
		
		// reset input
		input = null;
		do
    {
		  System.out.println("Market, Stop or Limit?");
      input = scanIn.next().trim();
      if (input == null || "".equals(input))
      {
        System.out.println("Invaid order type!");
      }
      if (input.startsWith("M") || input.startsWith("m"))
      {
        orType = OrderType.MARKET;
      }
      else if (input.length() > 1 && (input.startsWith("S") || input.startsWith("s")))
      {
        orType = OrderType.STOP;
        stopPrice = Double.parseDouble(input.substring(1));
      }
      else if (input.length() > 1 && (input.startsWith("L") || input.startsWith("l")))
      {
        orType = OrderType.LIMIT;
        limitPrice = Double.parseDouble(input.substring(1));
      }
    } while (orType == null);
		
		
	// reset input
    input = null;
    do
    {
      System.out.println("Quantity(1000)?");
      input = scanIn.next().trim();
      if (input == null || "".equals(input)) {
        qty = 1000;
      } else {
        qty = Integer.parseInt(input);
      }
    } while (qty == -1);
		
    scanIn.close();
    
    switch (txType) {
      case SELL:
        if (orType == OrderType.MARKET) {
        order = Order.createOrder(stockStatus.getTimestamp(),
            TransactionType.SELL, OrderType.MARKET, qty);
        } else if (orType == OrderType.STOP){
          order = Order.createOrder(stockStatus.getTimestamp(),
              TransactionType.SELL, OrderType.STOP, qty, 0, stopPrice);
        } else if (orType == OrderType.LIMIT){
          order = Order.createOrder(stockStatus.getTimestamp(),
              TransactionType.SELL, OrderType.LIMIT, qty, limitPrice, 0);
        }
        account.placeOrder(stockStatus.getTimestamp(), order);

        break;
      case BUY:
        if (orType == OrderType.MARKET) {
          order = Order.createOrder(stockStatus.getTimestamp(),
              TransactionType.BUY, OrderType.MARKET, qty);
          } else if (orType == OrderType.STOP) {
            order = Order.createOrder(stockStatus.getTimestamp(),
                TransactionType.BUY, OrderType.STOP, qty, 0, stopPrice);;
          } else if (orType == OrderType.LIMIT) {
            order = Order.createOrder(stockStatus.getTimestamp(),
                TransactionType.BUY, OrderType.LIMIT, qty, limitPrice, 0);;
          }
        account.placeOrder(stockStatus.getTimestamp(), order);
        break;
		}
	}

	public void handleOverNight(Account account, String symbol, Date timestamp,
			double preClose, double curOpen) {
	}

}

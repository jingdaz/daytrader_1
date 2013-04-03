package com.broadviewsoft.daytrader.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.domain.Account;
import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.domain.Order;
import com.broadviewsoft.daytrader.domain.OrderStatus;
import com.broadviewsoft.daytrader.domain.OrderType;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.StockHolding;
import com.broadviewsoft.daytrader.domain.StockStatus;
import com.broadviewsoft.daytrader.domain.TransactionType;
import com.broadviewsoft.daytrader.service.DataFeederFactory;
import com.broadviewsoft.daytrader.service.TradeStrategy;
import com.broadviewsoft.daytrader.util.Util;

public class CciStrategy extends TradeStrategy {
	private static Log logger = LogFactory.getLog(CciStrategy.class);

	public CciStrategy() {
		period = Period.MIN5;
		dataFeeder = DataFeederFactory.newInstance();
	}

	public void execute(StockStatus stockStatus, Account account) {
		StockHolding targetHolding = null;
		for (StockHolding sh : account.getHoldings()) {
			if (sh.getStock().eqauls(stockStatus.getStock())) {
				targetHolding = sh;
				break;
			}
		}
		
		logger.info("Executing on " + Util.format(stockStatus.getTimestamp()));
		
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

		// Top divergence: drops from CCI >= 100
		// Sell at CCI drops to 200 ~ 100
		if ((targetHolding != null 
				&& !stockStatus.isStrongest() 
				&& stockStatus.dropsTopDvg())
				&& !stockStatus.isSuperHighOpen()
				&& stockStatus.isSellingRange()) {
			int qty = 0;
			if (targetHolding.getQuantity() > Constants.DEFAULT_QUANTITY) {
				qty = targetHolding.getQuantity() / 2;
			} else {
				qty = targetHolding.getQuantity();
			}
			Order sell = Order.createOrder(stockStatus.getTimestamp(),
					TransactionType.SELL, OrderType.MARKET, qty);
			account.placeOrder(stockStatus.getTimestamp(), sell);
		}
		// Super high open; use trailing stop -2% of highest
		else if (targetHolding != null && stockStatus.isSuperHighOpen()) {
		  double limitWin = stockStatus.getPreHigh().getHigh() * Constants.LOCKWIN_PRE_HIGH_FACTOR;
		  Order stop = Order.createOrder(stockStatus.getTimestamp(),
          TransactionType.SELL, OrderType.STOP,
          targetHolding.getQuantity(), 0, limitWin);
      account.placeOrder(stockStatus.getTimestamp(), stop);
		}

		// Bottom divergence: picks up from CCI <= -100
		if (!stockStatus.isWeakest() 
		    && !stockStatus.isSuperLowOpen()
 				&& stockStatus.picksBtmDvg()
 				&& stockStatus.isBuyingRange()
 				&& (stockStatus.turningPointBelowFair()
 				    || stockStatus.isCciBigSlope())) {
			Order buy = Order.createOrder(stockStatus.getTimestamp(),
					TransactionType.BUY, OrderType.MARKET,
					Constants.DEFAULT_QUANTITY);
			account.placeOrder(stockStatus.getTimestamp(), buy);
		} 
		
		// Super low open; buy at CCI -120 ~ -100 and RSI < 30
		else if (stockStatus.isSuperLowOpen() 
		    && stockStatus.isBuyingRange()
		    && (stockStatus.isRsiOverSold() || stockStatus.isRsiReversed())) {
		  Order low = Order.createOrder(stockStatus.getTimestamp(),
          TransactionType.BUY, OrderType.MARKET,
          Constants.DEFAULT_QUANTITY);
      account.placeOrder(stockStatus.getTimestamp(), low);
		}
		
		// adjust existing orders
		List<Order> orders = account.getOrders();
		for (Order order : orders) {
		  if (order.getTxType()==TransactionType.SELL 
		      && order.getStatus()==OrderStatus.OPEN
		      && (order.getOrderType()==OrderType.STOP || order.getOrderType()==OrderType.STOPLIMIT)) {
		    double curFactor = stockStatus.getCurItem().getTypical() / order.getCostPrice();
		    
		    // tight up stop price to lock profit
		    if ( curFactor > Constants.STOP_ORDER_LOCKWIN_FACTOR) {
		      double newStop = order.getStopPrice()*(1.0+(curFactor-Constants.STOP_ORDER_TRAILING_FACTOR)/2);
		      if (newStop > order.getStopPrice()) {
		        order.setStopPrice(newStop);
		        logger.info("[" + Util.format(stockStatus.getTimestamp()) + "] Raising Stop price to lock profit on " + order);
		      }
		    }
		    
		    // set trailing stop price
		    else if ( curFactor > Constants.STOP_ORDER_TRAILING_FACTOR) {
		      double newStop = order.getStopPrice()*(1.0+curFactor-Constants.STOP_ORDER_TRAILING_FACTOR);
		      if (newStop > order.getStopPrice()) {
		        order.setStopPrice(newStop);
	          logger.info("[" + Util.format(stockStatus.getTimestamp()) + "] Moved Trailing Stop price up on " + order);
		      }
		    }
		  }
		}
	}

	public void handleOverNight(Account account, String symbol, Date timestamp,
			double preClose, double curOpen) {
		List<StockHolding> holdings = account.getHoldings();
		for (StockHolding sh : holdings) {
			// holding over night
			if (sh.getStock().getSymbol().equalsIgnoreCase(symbol)) {
				int result = Util.compare(curOpen, preClose);
				// Open high, set lockwin order min(5% profit, 2%+ open)
				// or Stop at -2% of highest so far
				if (result > 0) {
					double lockWinLimit = Math.min(
							Constants.LOCKWIN_PRE_CLOSE_FACTOR * preClose,
							Constants.LOCKWIN_CUR_OPEN_FACTOR * curOpen);
					Order newOrder = Order.createOrder(timestamp,
							TransactionType.SELL, OrderType.LIMIT,
							sh.getQuantity(), lockWinLimit, 0);
					account.placeOrder(timestamp, newOrder);
				}
				// Open low, set stop order
				else if (result < 0) {
					double stopLoss = Constants.STOPLOSS_CUR_OPEN_FACTOR
							* curOpen;
					Order newOrder = Order.createOrder(timestamp,
							TransactionType.SELL, OrderType.STOP,
							sh.getQuantity(), 0, stopLoss);
					account.placeOrder(timestamp, newOrder);
				}
				// Open flat, wait for chances
			}
		}

	}

}

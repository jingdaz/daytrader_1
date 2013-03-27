package com.broadviewsoft.daytrader.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.domain.Account;
import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.domain.Order;
import com.broadviewsoft.daytrader.domain.OrderType;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.StockHolding;
import com.broadviewsoft.daytrader.domain.StockStatus;
import com.broadviewsoft.daytrader.domain.TransactionType;
import com.broadviewsoft.daytrader.service.DataFeeder;
import com.broadviewsoft.daytrader.service.TradeStrategy;
import com.broadviewsoft.daytrader.util.Util;

public class CciStrategy extends TradeStrategy {
	private static Log logger = LogFactory.getLog(CciStrategy.class);

	private DataFeeder dataFeeder = null;

	public CciStrategy() {
		period = Period.MIN5;
		dataFeeder = new DataFeeder();
		dataFeeder.init(Constants.STOCKS_WITH_DATA);
	}

	public void execute(StockStatus stockStatus, Account account) {
		StockHolding targetHolding = null;
		for (StockHolding sh : account.getHoldings()) {
			if (sh.getStock().eqauls(stockStatus.getStock())) {
				targetHolding = sh;
				break;
			}
		}
//		double cciSlope = (stockStatus.getCurItem().getCci() - stockStatus
//				.getPreHigh().getCci()) / stockStatus.getPreHigh().getCci();
//		double priceSlope = (stockStatus.getCurItem().getTypical() - stockStatus
//				.getPreHigh().getTypical())
//				/ stockStatus.getPreHigh().getTypical();
		logger.info("Executing on " + Util.format(stockStatus.getTimestamp()));
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
		if ((targetHolding != null 
				&& !stockStatus.isStrongest() 
				&& stockStatus.dropsTopDvg())
				&& !stockStatus.isSuperHighOpen()
				&& stockStatus.getCurItem().getCci() < Constants.CCI_TOP_SELL_LIMIT
				&& stockStatus.getCurItem().getCci() > Constants.CCI_TOP_DIVERGENCE) {
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
		else if (stockStatus.isSuperHighOpen()) {
		  double limitWin = stockStatus.getPreHigh().getHigh() * Constants.LOCKWIN_PRE_HIGH_FACTOR;
		  Order stop = Order.createOrder(stockStatus.getTimestamp(),
          TransactionType.SELL, OrderType.STOP,
          targetHolding.getQuantity(), 0, limitWin);
      account.placeOrder(stockStatus.getTimestamp(), stop);
		}

		// Bottom divergence: picks up from CCI <= -100
		if (!stockStatus.isWeakest() 
        && stockStatus.getCurItem().getCci() > Constants.CCI_BOTTOM_BUY_LIMIT
 				&& (stockStatus.picksBtmDvg()
 				    || stockStatus.isSuperLowOpen()
 				    || stockStatus.getCurItem().getCci() < Constants.CCI_BOTTOM_DIVERGENCE
 				    /*|| (stockStatus.getCurItem().getCci() - stockStatus.getPreLow().getCci() > 150)*/)) {
			Order buy = Order.createOrder(stockStatus.getTimestamp(),
					TransactionType.BUY, OrderType.MARKET,
					Constants.DEFAULT_QUANTITY);
			account.placeOrder(stockStatus.getTimestamp(), buy);
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

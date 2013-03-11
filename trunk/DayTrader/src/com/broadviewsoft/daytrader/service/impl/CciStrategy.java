package com.broadviewsoft.daytrader.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Random;

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
		dataFeeder = new DataFeeder(Constants.PROD_MODE);
	}

	public void execute(StockStatus status, Account account) {
		// status one, account one
		Random random = new Random(System.currentTimeMillis());
		int n = random.nextInt(100);
		if (n < 75) {
			if (account.getHoldings() != null
					&& account.getHoldings().isEmpty()) {
				logger.info("\tPlacing Market Buy order at "
						+ status.getTimestamp());
				Order newOrder = Order.createOrder(status.getTimestamp(),
						TransactionType.BUY, OrderType.MARKET, 1000);
				account.placeOrder(newOrder);
			}
		}

		if (n > 25) {
			if (account.getHoldings() != null
					&& !account.getHoldings().isEmpty()) {
				logger.info("\tPlacing Limit Sell order at "
						+ status.getTimestamp());
				double targetPrice = 10.0 + random.nextInt(10) / 50.0;
				Order newOrder = Order.createOrder(status.getTimestamp(),
						TransactionType.SELL, OrderType.LIMIT, 1000,
						targetPrice);
				account.placeOrder(newOrder);
			}
		}

		// status two, account two

	}

	public void handleOverNight(Account account, String symbol, Date timestamp, double curOpen) {
		double preClose = dataFeeder.getPreClose(symbol, timestamp);





		List<StockHolding> holdings = account.getHoldings();
		for (StockHolding sh : holdings) {
			// holding over night
			if (sh.getStock().getSymbol().equalsIgnoreCase(symbol)) {
				int result = Util.compare(curOpen, preClose);
				// Open high, set lockwin order min(5% profit, 2%+ open)
				if (result > 0) {
					double lockWinLimit = Math.min(Constants.LOCKWIN_PRE_CLOSE_FACTOR
							* preClose, Constants.LOCKWIN_CUR_OPEN_FACTOR * curOpen);
					Order newOrder = Order.createOrder(timestamp,
							TransactionType.SELL, OrderType.LIMIT, sh.getQuantity(), lockWinLimit);
					account.placeOrder(newOrder);
				}
				// Open low, set stop order
				else if (result < 0) {
					double stopLoss = Constants.STOPLOSS_CUR_OPEN_FACTOR * curOpen;
					Order newOrder = Order.createOrder(timestamp,
							TransactionType.SELL, OrderType.STOP, sh.getQuantity(), stopLoss);
					account.placeOrder(newOrder);
				}
				// Open flat, wait for chances
			}
		}

	}

}

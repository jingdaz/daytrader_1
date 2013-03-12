package com.broadviewsoft.daytrader.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.domain.Account;
import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.domain.DailyStatus;
import com.broadviewsoft.daytrader.domain.Order;
import com.broadviewsoft.daytrader.domain.OrderType;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.PriceType;
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
		dailyStatus = new DailyStatus();
	}

	public void execute(StockStatus stockStatus, Account account)
  {
    // Top divergence: drops from CCI >= 100
    if (stockStatus.dropsTopDvg() && !account.getHoldings().isEmpty())
    {
      Order sell = Order.createOrder(stockStatus.getTimestamp(), TransactionType.SELL, OrderType.MARKET, Constants.DEFAULT_QUANTITY);
      account.placeOrder(sell);
      logger.debug("\tPlacing Market Sell order at " + stockStatus.getTimestamp());
    }
    // Bottom divergence: picks up from CCI <= -100
    if (!dailyStatus.isWeakest() && stockStatus.picksBtmDvg() && account.getHoldings().isEmpty() 
    		&& stockStatus.getCurItem().compareTo(stockStatus.getPreLow())>0)
    {
      Order buy = Order.createOrder(stockStatus.getTimestamp(), TransactionType.BUY, OrderType.MARKET, Constants.DEFAULT_QUANTITY);
      account.placeOrder(buy);
      logger.debug("\tPlacing Market Buy order at " + stockStatus.getTimestamp());
    }

  }

	public void handleOverNight(Account account, String symbol, Date timestamp) {
		Date yesterday = new Date(timestamp.getTime()
				- Constants.DAY_IN_MILLI_SECONDS);
		double preClose = dataFeeder.getPrice(symbol, yesterday,
				PriceType.Close);
		double curOpen = dataFeeder.getPrice(symbol, timestamp, PriceType.Open);

		List<StockHolding> holdings = account.getHoldings();
		for (StockHolding sh : holdings) {
			// holding over night
			if (sh.getStock().getSymbol().equalsIgnoreCase(symbol)) {
				int result = Util.compare(curOpen, preClose);
				// Open high, set lockwin order min(5% profit, 2%+ open)
				if (result > 0) {
					double lockWinLimit = Math.min(
							Constants.LOCKWIN_PRE_CLOSE_FACTOR * preClose,
							Constants.LOCKWIN_CUR_OPEN_FACTOR * curOpen);
					Order newOrder = Order.createOrder(timestamp,
							TransactionType.SELL, OrderType.LIMIT,
							sh.getQuantity(), lockWinLimit);
					account.placeOrder(newOrder);
					logger.debug("\tPlacing Limit Sell order at " + timestamp);
				}
				// Open low, set stop order
				else if (result < 0) {
					double stopLoss = Constants.STOPLOSS_CUR_OPEN_FACTOR
							* curOpen;
					Order newOrder = Order.createOrder(timestamp,
							TransactionType.SELL, OrderType.STOP,
							sh.getQuantity(), stopLoss);
					account.placeOrder(newOrder);
					logger.debug("\tPlacing Stop Sell order at " + timestamp);
				}
				// Open flat, wait for chances
			}
		}

	}

}

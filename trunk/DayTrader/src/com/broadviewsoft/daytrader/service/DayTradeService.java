package com.broadviewsoft.daytrader.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.domain.Account;
import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.StockItem;
import com.broadviewsoft.daytrader.domain.StockStatus;
import com.broadviewsoft.daytrader.service.BrokerService;
import com.broadviewsoft.daytrader.service.ITradeStrategy;

/**
 * 
 * Test driver to simulate Day Trade using this system
 * <P>
 * Test driver to run backtest using this system to find an optimal strategy
 * </P>
 * <P>
 * <B>Creation date:</B> Mar 8, 2013 3:43:24 PM
 * </P>
 * 
 * @author Jason Zhang
 */
public class DayTradeService {
	private static Log logger = LogFactory.getLog(DayTradeService.class);

	private BrokerService broker = null;
	private Account account = null;

	public DayTradeService() {
		broker = new BrokerService();
		account = new Account();
	}

	public void tradeDaily(ITradeStrategy strategy, String symbol, Date tradeDate,
			double curOpen) {
		Period period = strategy.getPeriod();

		account.init();
		broker.addAccount(account);

		account.showHoldings();

//		strategy.handleOverNight(account, symbol, tradeDate, curOpen);

		Date start = new Date(tradeDate.getTime() + Constants.MARKET_OPEN_TIME);
		Date end = new Date(tradeDate.getTime() + Constants.MARKET_CLOSE_TIME);

		Date now = start;
		while (now.before(end)) {
			for (int i = 0; i < period.minutes(); i++) {
				if (i == 0) {
					StockStatus status = analyze(symbol, period, now);
					strategy.execute(status, account);
				}
				// check order execution after 1 minute - simulate slow order
				// entry on mobile phone
				else {
					broker.checkOrder(now);
				}
				// advance 1 minute on clock
				now = new Date(now.getTime()
						+ Constants.MINUTE_IN_MILLI_SECONDS);
			}
		}

		account.showOrders();
		account.showTransactions();
		account.showHoldings();
	}

	public StockStatus analyze(String symbol, Period period, Date date) {
		StockStatus curStatus = new StockStatus(date);
		List<StockItem> data = broker.collectData(symbol, period, date);
		// TODO logic to figure out current status based on data
		return curStatus;
	}

}

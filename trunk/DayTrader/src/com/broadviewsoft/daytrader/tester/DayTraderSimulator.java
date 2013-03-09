package com.broadviewsoft.daytrader.tester;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.broadviewsoft.daytrader.domain.Account;
import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.domain.Broker;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.StockItem;
import com.broadviewsoft.daytrader.domain.StockStatus;
import com.broadviewsoft.daytrader.service.CciStrategy;
import com.broadviewsoft.daytrader.service.ITradeStrategy;

/**
 * 
 * TODO
 * <P>
 * Insert the description here.
 * </P>
 * <P>
 * <B>Creation date:</B> Mar 8, 2013 3:43:24 PM
 * </P>
 * 
 * @author 538601600
 */
public class DayTraderSimulator {
	private Broker broker;
	private Account account;
	private List<ITradeStrategy> strategies = new ArrayList<ITradeStrategy>();

	public DayTraderSimulator() {
		broker = new Broker();
		account = new Account();
		strategies.add(new CciStrategy());
	}

	public void tradeDaily(ITradeStrategy strategy, Date tradeDate,
			double preClose, double curOpen) {
		account.init();
		broker.addAccount(account);

		account.showHoldings();
		
		// account.handleOverNight(preClose, curOpen);

		String symbol = "UVXY";
		Period period = Period.MIN5;

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

		account.showTransactions();
		account.showHoldings();
	}

	public StockStatus analyze(String symbol, Period period, Date date) {
		StockStatus curStatus = new StockStatus(date);
		List<StockItem> data = broker.collectData(symbol, period, date);
		// TODO logic to figure out current status based on data
		return curStatus;
	}

	public void startup(Date tradeDate, double preClose, double curOpen) {
		for (ITradeStrategy strategy : strategies) {
			tradeDaily(strategy, tradeDate, preClose, curOpen);
		}
	}

	public static void main(String[] args) throws ParseException {
		DayTraderSimulator simulator = new DayTraderSimulator();
		double preClose = 9.91;
		double curOpen = 9.98;
		Date tradeDate = Constants.TRADE_DATE_FORMATTER.parse("02/28/2013");

		simulator.startup(tradeDate, preClose, curOpen);
	}
}

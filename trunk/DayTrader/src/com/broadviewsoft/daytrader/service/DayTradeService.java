package com.broadviewsoft.daytrader.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.domain.Account;
import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.domain.Period;
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

	public void tradeDaily(ITradeStrategy strategy, String symbol, Date tradeDate) {
		Period period = strategy.getPeriod();

		strategy.resetDailyStatus();
		account.init();
		broker.addAccount(account);
		account.showHoldings();

//		strategy.handleOverNight(account, symbol, tradeDate);

		Date start = new Date(tradeDate.getTime() + Constants.MARKET_OPEN_TIME);
		Date end = new Date(tradeDate.getTime() + Constants.MARKET_CLOSE_TIME);

		Date now = start;
		while (now.before(end)) {
			for (int i = 0; i < period.minutes(); i++) {
				if (i == 0) {
					StockStatus status = strategy.analyze(broker, symbol, period, now);
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

//		account.showOrders();
		account.showTransactions();
		account.showHoldings();
		// reset account for future
		account.reset();
	}

  /**
	 * 
	 * @param strategy
	 * @param symbol
	 * @param startDate
	 * @param endDate
	 */
  public void trade(ITradeStrategy strategy, String symbol, Date startDate, Date endDate)
  {
    Date today = startDate;
    while (!today.after(endDate)) {
      logger.info("Simulating " + today);
      Calendar cal = new GregorianCalendar();
      cal.setTime(today);
      int day = cal.get(Calendar.DAY_OF_WEEK);
      if (day == Calendar.SUNDAY || day == Calendar.SATURDAY
          || Constants.MARKET_CLOSE_DAYS.contains(today)) {
        logger.info("Market is closed today; Skipped.");
      }
      else {
        tradeDaily(strategy, symbol, today);
      }
      today = new Date(today.getTime() + Constants.DAY_IN_MILLI_SECONDS);
    }
   
  }

}

package com.broadviewsoft.daytrader.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.domain.Account;
import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.PriceType;
import com.broadviewsoft.daytrader.domain.StockStatus;
import com.broadviewsoft.daytrader.service.BrokerService;
import com.broadviewsoft.daytrader.service.ITradeStrategy;
import com.broadviewsoft.daytrader.util.Util;

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
public class TradePlatform {
	private static Log logger = LogFactory.getLog(TradePlatform.class);

	private BrokerService broker = null;
	private Account account = null;

	public TradePlatform() {
		broker = new BrokerService();
		account = new Account();
	}

	public void tradeDaily(ITradeStrategy strategy, String symbol, Date tradeDate) {
		Period period = strategy.getPeriod();

		strategy.resetDailyStatus();
		// FIXME Monday's preClose?
    
    int todayItemIndex = broker.getDataFeeder().getCurItemIndex(symbol, tradeDate, Period.DAY);
    int yesterdayItemIndex = todayItemIndex - 1;
    logger.info("Index for today and yesterday " + todayItemIndex + "/" + yesterdayItemIndex);
    
    if (todayItemIndex < 0 || yesterdayItemIndex < 0) {
    	logger.error("No open/close price found on " + tradeDate);
    	return;
    }
    
    double curOpen = broker.getDataFeeder().getPriceByIndex(symbol, Period.DAY, todayItemIndex, PriceType.Open);
    double preClose = broker.getDataFeeder().getPriceByIndex(symbol, Period.DAY, yesterdayItemIndex, PriceType.Close);
    
		account.init(preClose, tradeDate);
		broker.registerAccount(account);
		account.showHoldings();

		strategy.handleOverNight(account, symbol, tradeDate, preClose, curOpen);

		Date start = new Date(tradeDate.getTime() + Constants.MARKET_OPEN_TIME);
		Date end = new Date(tradeDate.getTime() + Constants.MARKET_CLOSE_TIME);

		Date today = start;
		while (today.before(end)) {
			for (int i = 0; i < period.minutes(); i++) {
			  broker.checkOrder(today);
				if (i == 0 && !Constants.OVERNIGHT_ONLY) {
					StockStatus status = strategy.analyze(broker, symbol, period, today);
		       // check order execution after 1 minute - simulate slow order
	        // entry on mobile phone
					strategy.execute(status, account);
				}
				// advance 1 minute on clock
				today = new Date(today.getTime()
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
    Date nextDay = null;
    while (!today.after(endDate)) {
      logger.info("Simulating " + Constants.TRADE_DATE_FORMATTER.format(today));
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
      nextDay = new Date(today.getTime() + Constants.DAY_IN_MILLI_SECONDS);
		if (TimeZone.getDefault().inDaylightTime(today) && !TimeZone.getDefault().inDaylightTime(nextDay)) {
			nextDay = Util.backwardOneHour(nextDay);
		}
		else if (!TimeZone.getDefault().inDaylightTime(today) && TimeZone.getDefault().inDaylightTime(nextDay)) {
			nextDay = Util.forwardOneHour(nextDay);
		}
      today = nextDay;
    }
   
  }

}

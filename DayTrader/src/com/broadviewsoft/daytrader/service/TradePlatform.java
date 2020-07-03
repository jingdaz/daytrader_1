package com.broadviewsoft.daytrader.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.domain.Account;
import com.broadviewsoft.daytrader.domain.Client;
import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.PriceType;
import com.broadviewsoft.daytrader.domain.StockStatus;
import com.broadviewsoft.daytrader.service.BrokerService;
import com.broadviewsoft.daytrader.service.ITradeStrategy;
import com.broadviewsoft.daytrader.util.Util;

/**
 * 
 * Abstraction of Trade Platform for Day-Trade
 * <P>
 * Contains a Broker and Data Feeder
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
	private List<Client> clients = null;

	public TradePlatform() {
		broker = new BrokerService();
		clients = new ArrayList<Client>();
	}

	public void tradeDaily(Client client, ITradeStrategy strategy,
			String symbol, Date tradeDate) {
		Period period = strategy.getPeriod();
		Account account = client.getAccounts().get(0);

		int tdaItemIndex = broker.getItemIndex(symbol, tradeDate,
				Period.DAY);
		int ytaItemIndex = tdaItemIndex - 1;
		logger.info("Index for today and yesterday " + tdaItemIndex + "/"
				+ ytaItemIndex);

		if (tdaItemIndex < 0 || ytaItemIndex < 0) {
			logger.error("No open/close price found on " + tradeDate);
			return;
		}

		double curOpen = broker.getPrice(symbol, Period.DAY,
				tdaItemIndex, PriceType.Open);
		double curClose = broker.getPrice(symbol, Period.DAY,
        tdaItemIndex, PriceType.Close);
		double preClose = broker.getPrice(symbol, Period.DAY,
				ytaItemIndex, PriceType.Close);

		account.showHoldings(tradeDate, preClose);

		// strategy.handleOverNight(account, symbol, tradeDate, preClose, curOpen);

		Date start = new Date(tradeDate.getTime() + Constants.MARKET_OPEN_TIME);
		Date end = new Date(tradeDate.getTime() + Constants.MARKET_CLOSE_TIME);

		Date now = start;
		StockStatus status = null;
		while (now.before(end)) {
			for (int i = 0; i < period.minutes(); i++) {
				broker.checkOrder(account, now);
				if (i == 0 && !Constants.OVERNIGHT_ONLY) {
					status = strategy.analyze(broker, symbol, period, now,
							ytaItemIndex);
					// check order execution after 1 minute - simulate slow order
					// entry on mobile phone
					strategy.execute(status, account);
				}
				// advance 1 minute on clock
				now = new Date(now.getTime()
						+ Constants.MINUTE_IN_MILLI_SECONDS);
			}
		}

		if (now.equals(end)) {
			logger.info("Market closes at " + Util.format(now));
		}
		
		account.showOrders(tradeDate);
		account.showTransactions(tradeDate);
		account.showHoldings(tradeDate, curClose);
		account.updateProfit(tradeDate, curClose);
		// refresh account for future
//		account.flush();
	}

	/**
	 * 
	 * @param strategy
	 * @param symbol
	 * @param startDate
	 * @param endDate
	 */
	public void trade(Client client, ITradeStrategy strategy, String symbol,
			Date startDate, Date endDate) {
		Account account = client.getAccounts().get(0);
		// start trade
		Date today = startDate;
		Date nextDay = null;
		while (!today.after(endDate)) {
			logger.info("Simulating "
					+ Constants.TRADE_DATE_FORMATTER.format(today));
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			int day = cal.get(Calendar.DAY_OF_WEEK);
			if (day == Calendar.SUNDAY || day == Calendar.SATURDAY) {
				logger.info("It's a weekend today; Market is closed; Skipped.");
			} else if (Constants.MARKET_CLOSE_DAYS.contains(today)) {
				logger.info("It's a holiday today; Market is closed; Skipped.");
			} else if (Constants.MARKET_CLOSE_EARLY_DAYS.contains(today)) {
				logger.info("Market opens an half day today; Closed at 1:00 PM");
				// FIXME tradeHalfDay(strategy, symbol, today);
			} else {
				tradeDaily(client, strategy, symbol, today);
			}
			nextDay = new Date(today.getTime() + Constants.DAY_IN_MILLI_SECONDS);
			if (TimeZone.getDefault().inDaylightTime(today)
					&& !TimeZone.getDefault().inDaylightTime(nextDay)) {
				nextDay = Util.backwardOneHour(nextDay);
			} else if (!TimeZone.getDefault().inDaylightTime(today)
					&& TimeZone.getDefault().inDaylightTime(nextDay)) {
				nextDay = Util.forwardOneHour(nextDay);
			}
			today = nextDay;
		}

		account.showProfits();
	}

}

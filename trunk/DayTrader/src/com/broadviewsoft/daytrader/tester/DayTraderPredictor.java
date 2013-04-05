package com.broadviewsoft.daytrader.tester;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.domain.Account;
import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.PriceType;
import com.broadviewsoft.daytrader.service.BrokerService;
import com.broadviewsoft.daytrader.service.DataFeederFactory;
import com.broadviewsoft.daytrader.service.IDataFeeder;
import com.broadviewsoft.daytrader.service.ITradeStrategy;
import com.broadviewsoft.daytrader.service.impl.CciStrategy;

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
public class DayTraderPredictor {
	private static Log logger = LogFactory.getLog(DayTraderPredictor.class);

	private BrokerService broker = null;
	private Account account = null;
	private ITradeStrategy strategy = null;
	private IDataFeeder dataFeeder = DataFeederFactory.newInstance();

	public DayTraderPredictor() {
		broker = new BrokerService();
		account = new Account();
		strategy = new CciStrategy();
		broker.registerAccount(account);
	}
	
	public BrokerService getBroker() {
		return broker;
	}

	public void setBroker(BrokerService broker) {
		this.broker = broker;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public ITradeStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(ITradeStrategy strategy) {
		this.strategy = strategy;
	}

	public void predict(String symbol) {
		Calendar cal = Calendar.getInstance();
		Calendar calToday = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		Date today = calToday.getTime();
		
	    int todayItemIndex = dataFeeder.getCurItemIndex(symbol, today, Period.DAY);
	    int yesterdayItemIndex = todayItemIndex - 1;
	    
	    if (todayItemIndex < 0 || yesterdayItemIndex < 0) {
	    	logger.error("No open/close price found on " + today);
	    	return;
	    }
	    
	    double curOpen = dataFeeder.getPriceByIndex(symbol, Period.DAY, todayItemIndex, PriceType.Open);
	    double preClose = dataFeeder.getPriceByIndex(symbol, Period.DAY, yesterdayItemIndex, PriceType.Close);
		
		account.init(preClose);

		double[] curOpens = new double[8];
		for (int i = 0; i < Constants.PREDICT_OPEN_FACTORS.length; i++) {
			curOpens[i] = Constants.PREDICT_OPEN_FACTORS[i];
		}
	}

	public static void main(String[] args) throws ParseException {
		DayTraderPredictor predictor = new DayTraderPredictor();

		String[] symbols = Constants.INIT_STOCK_SYMBOLS;

		for (String symbol : symbols) {
			predictor.predict(symbol);
		}
	}
}

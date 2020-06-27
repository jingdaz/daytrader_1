package com.broadviewsoft.daytrader.tester;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.domain.Account;
import com.broadviewsoft.daytrader.domain.Client;
import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.service.TradePlatform;
import com.broadviewsoft.daytrader.service.ITradeStrategy;
import com.broadviewsoft.daytrader.service.impl.CciStrategy;
import com.broadviewsoft.daytrader.service.impl.RsiStrategy;

/**
 * 
 * Test driver to simulate Day-Trade using this system
 * <P>
 * Test driver to run backtest using this system to find an optimal strategy
 * </P>
 * <P>
 * <B>Creation date:</B> Mar 8, 2013 3:43:24 PM
 * </P>
 * 
 * @author Jason Zhang
 */
public class DayTraderSimulator {
	private static Log logger = LogFactory.getLog(DayTraderSimulator.class);

	private TradePlatform tradePlatform = null;
	private List<ITradeStrategy> strategies = null;
	
	public DayTraderSimulator() {
		tradePlatform = new TradePlatform();
		strategies = new ArrayList<ITradeStrategy>();
		init();
	}
	
	public List<ITradeStrategy> getStrategies() {
		return strategies;
	}

	public void addStrategy(ITradeStrategy strategy) {
		strategies.add(strategy);
	}
	
	public void init() {
		logger.info("Initializing simulator...");
		addStrategy(new CciStrategy());
//		simulator.addStrategy(new RsiStrategy());
	}
	
	public void simulate(Client client, String symbol, Date startDate, Date endDate) {
		logger.info("Starting simulation by client ...");
		for (ITradeStrategy strategy : strategies) {
			logger.info("Applying strategy (" + strategy.getDescription() + ") for " + symbol);
			tradePlatform.trade(client, strategy, symbol, startDate, endDate);
		}
		logger.info("Done simulation.");
	}

	public static void main(String[] args) throws ParseException {
		DayTraderSimulator simulator = new DayTraderSimulator();

		Client c = new Client("Jason", "Chang", "05/24/1980");
		Account a = new Account();
		c.addAccount(a);

		String[] symbols = Constants.INIT_STOCK_SYMBOLS;
		Date startDate = Constants.TRADE_DATE_FORMATTER.parse("06/22/2020");
		Date endDate = Constants.TRADE_DATE_FORMATTER.parse("06/24/2020");

		for (String symbol : symbols) {
			simulator.simulate(c, symbol, startDate, endDate);
		}
	}
}

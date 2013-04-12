package com.broadviewsoft.daytrader.tester;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.domain.Account;
import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.PriceType;
import com.broadviewsoft.daytrader.service.DataFeederFactory;
import com.broadviewsoft.daytrader.service.IDataFeeder;
import com.broadviewsoft.daytrader.service.ITradeStrategy;
import com.broadviewsoft.daytrader.service.TradePlatform;
import com.broadviewsoft.daytrader.service.impl.CciStrategy;

/**
 * 
 * Day Trader predictor to recommend handling of over-night holdings
 * <P>
 * 
 * </P>
 * <P>
 * <B>Creation date:</B> Mar 8, 2013 3:43:24 PM
 * </P>
 * 
 * @author Jason Zhang
 */
public class DayTraderPredictor {
	private static Log logger = LogFactory.getLog(DayTraderPredictor.class);

	private TradePlatform tradePlatform = null;
	private Account account = null;
	private ITradeStrategy strategy = null;
	private IDataFeeder dataFeeder = DataFeederFactory.newInstance();

	public DayTraderPredictor() {
	  tradePlatform = new TradePlatform();
	  account = new Account();
		strategy = new CciStrategy();
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

	public void predict(String symbol, Date yesterday) {
	  int ytdItemIndex = dataFeeder.getCurItemIndex(symbol, yesterday, Period.DAY);
    if (ytdItemIndex < 0) {
      logger.error("No open/close price found on " + yesterday);
      return;
    }
	  
	  double preClose = dataFeeder.getPriceByIndex(symbol, Period.DAY, ytdItemIndex, PriceType.Close);
    
		double[] curOpens = new double[Constants.PREDICT_OPEN_FACTORS.length];
    for (int i = 0; i < Constants.PREDICT_OPEN_FACTORS.length; i++) {
      curOpens[i] = Constants.PREDICT_OPEN_FACTORS[i];
    }

    
	}

	public static void main(String[] args) throws ParseException {
		DayTraderPredictor predictor = new DayTraderPredictor();
		
		Date yesterday = Constants.TRADE_DATE_FORMATTER.parse("04/08/2013");
		String[] symbols = Constants.INIT_STOCK_SYMBOLS;

		for (String symbol : symbols) {
			predictor.predict(symbol, yesterday);
		}
	}
}

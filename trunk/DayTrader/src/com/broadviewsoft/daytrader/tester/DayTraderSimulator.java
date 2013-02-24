package com.broadviewsoft.daytrader.tester;

import java.text.ParseException;
import java.util.Date;

import com.broadviewsoft.daytrader.domain.Account;
import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.domain.MarketMaker;
import com.broadviewsoft.daytrader.domain.Period;

public class DayTraderSimulator {
	private MarketMaker mm;
	private Account account;
	
	public DayTraderSimulator() {
		mm = new MarketMaker();
		account = new Account();
	}
	
	public void tradeDaily(Date tradeDate, double preClose, double curOpen) {
		account.init();
		account.handleOverNight(preClose, curOpen);
		
		Period period = Period.MIN15;
		int interval = period.minutes();
		
		Date start = new Date(tradeDate.getTime() + Constants.MARKET_OPEN_TIME);
		Date end = new Date(tradeDate.getTime() + Constants.MARKET_CLOSE_TIME);
		
		Date now = start;
		while (now.before(end)) {
			
		}
		
		account.showTransactions();
	}
	
	
	
	
	public static void main(String[] args) throws ParseException {
		DayTraderSimulator simulator = new DayTraderSimulator();
		double preClose = 9.91;
		double curOpen = 9.98;
		Date tradeDate = Constants.TRADE_DATE_FORMATTER.parse("01/29/2013");
		
		simulator.tradeDaily(tradeDate, preClose, curOpen);
	
	}
}

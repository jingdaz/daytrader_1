package com.broadviewsoft.daytrader.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MarketMaker extends Thread {
	private boolean useRealData = false;
	private List<StockPrice> priceTrace = new ArrayList<StockPrice>(); 
	private List<Account> accounts = new ArrayList<Account>();
	
	public MarketMaker() {
		
	}

	public boolean isUseRealData() {
		return useRealData;
	}

	public void setUseRealData(boolean useRealData) {
		this.useRealData = useRealData;
	}

	public List<StockPrice> getPriceTrace() {
		return priceTrace;
	}

	public void setPriceTrace(List<StockPrice> priceTrace) {
		this.priceTrace = priceTrace;
	}
	
	// TODO Real-time data feed
	/**
	 * Retrieve stock real-time price
	 * Use data feed in future
	 * 
	 * @param timestamp
	 * @return
	 */
	public StockPrice getRealtimePrice(Date timestamp) {
		if (useRealData) {
			return null;
		}

		// retrieve from history data
		
		return null;
	}
	
	
	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

	public void addAccount(Account account) {
		accounts.add(account);
	}

	public void run() {
		
	}

	
}

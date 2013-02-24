package com.broadviewsoft.daytrader.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MarketMaker {
	private boolean useRealData = false;
	private List<StockPrice> priceTrace = new ArrayList<StockPrice>(); 
	
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
}

package com.broadviewsoft.daytrader.service;

import com.broadviewsoft.daytrader.domain.Period;

public abstract class TradeStrategy  implements ITradeStrategy {
	protected Period period;
	
	protected String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}

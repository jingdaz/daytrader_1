package com.broadviewsoft.daytrader.service;

import com.broadviewsoft.daytrader.domain.Period;

/**
 * possible to mix strategies?
 * 
 * @author Jingda
 *
 */
public abstract class TradeStrategy implements ITradeStrategy {
	protected Period period = null;

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" ");
		sb.append(period);
		return sb.toString();
	}

}

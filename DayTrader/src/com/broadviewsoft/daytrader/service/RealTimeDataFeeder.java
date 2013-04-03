package com.broadviewsoft.daytrader.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.domain.Constants;

public class RealTimeDataFeeder extends AbstractDataFeeder {
	private static Log logger = LogFactory.getLog(RealTimeDataFeeder.class);

	public RealTimeDataFeeder() {
		this(false);
	}

	public RealTimeDataFeeder(boolean prodMode) {
		this.prodMode = prodMode;
		if (!initialized) {
		  init(Constants.STOCKS_WITH_DATA);
		  initialized = true;
		}
	}

	public void init(String[] symbols) {
	}

}

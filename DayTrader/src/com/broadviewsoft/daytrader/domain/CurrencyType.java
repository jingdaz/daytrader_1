package com.broadviewsoft.daytrader.domain;

public enum CurrencyType {
	USD(0),
	CAD(1);
	
	private CurrencyType (int id) {
		this.id = id;
	}
	
	public int id() {
		return id;
	}

	private int id;
}

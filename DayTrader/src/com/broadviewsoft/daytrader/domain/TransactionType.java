package com.broadviewsoft.daytrader.domain;

public enum TransactionType {
	BUY(0),
	SELL(1);
	
	private TransactionType (int id) {
		this.id = id;
	}
	
	
	public int id() {
		return id;
	}


	private int id;
}

package com.broadviewsoft.daytrader.domain;

public class Stock {
	private CurrencyType currencyType;
	private String symbol;
	private String companyName;
	
	public Stock() {
		currencyType = CurrencyType.USD;
	}

	public Stock(String symbol) {
		currencyType = CurrencyType.USD;
		this.symbol = symbol;
	}

	public CurrencyType getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(CurrencyType currencyType) {
		this.currencyType = currencyType;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	
}

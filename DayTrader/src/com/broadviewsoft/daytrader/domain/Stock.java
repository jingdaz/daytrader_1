package com.broadviewsoft.daytrader.domain;

public class Stock implements Cloneable {
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

	public boolean eqauls(Object obj) {
		if (!(obj instanceof Stock)) {
			return false;
		}
		
		Stock s = (Stock) obj;
		return symbol.equalsIgnoreCase(s.getSymbol());
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.currencyType);
		sb.append(" ");
		sb.append(this.symbol);
		sb.append(" ");
		sb.append(this.companyName);
		
		return sb.toString();
	}
	
	public static void main(String[] args) throws CloneNotSupportedException {
		Stock a = new Stock("IBM");
		Stock b = new Stock("IBM");
		Object c = a.clone();
		System.out.println("a==b? " + (a.eqauls(b)));
		System.out.println("a==c? " + (a.eqauls(c)));
	}
}

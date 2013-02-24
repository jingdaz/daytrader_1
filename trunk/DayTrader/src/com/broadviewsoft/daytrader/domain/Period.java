package com.broadviewsoft.daytrader.domain;

public enum Period {
	MIN(1), 
	MIN5(5), 
	MIN15(15), 
	HOUR(60), 
	DAY(24*60), 
	WEEK(7*24*60);
	
	private Period(int minutes) {
		this.minutes = minutes;
	}
	
	public int minutes() {
		return minutes;
	}

	private int minutes;
}

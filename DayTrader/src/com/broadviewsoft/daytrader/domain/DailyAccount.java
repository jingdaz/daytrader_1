package com.broadviewsoft.daytrader.domain;

import java.util.Date;
@Deprecated
public class DailyAccount {
	private Date dateTime;
	private Account account;

	public DailyAccount() {

	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	/**
	 * Settlement now for next period
	 */
	public void nextPeriod(Period period) {

	}
}

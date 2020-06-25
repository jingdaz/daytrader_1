package com.broadviewsoft.daytrader.domain;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Abstraction of clients who conduct investment on TradePlatform
 * 
 * @author Jason
 *
 */
public class Client implements Serializable {
	private static final long serialVersionUID = 8654846504009131628L;
	private DateFormat df = new SimpleDateFormat(Constants.DOB_PATTERN);

	private String firstName;
	private String lastName;
	private Date dob;
	private List<Account> accounts = new ArrayList<Account>();
	
	public Client(String firstName, String lastName, Date dob) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.dob = dob;
	}

	public Client(String firstName, String lastName, String dob) {
		Date d = null;
		try {
			d = df.parse(dob);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.firstName = firstName;
		this.lastName = lastName;
		this.dob = d;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public Date getDob() {
		return dob;
	}

	public List<Account> getAccounts() {
		return accounts;
	}
	
	public void addAccount(Account account) {
		accounts.add(account);
	}
	
	public void removeAccount(Account account) {
		accounts.remove(account);
	}
}

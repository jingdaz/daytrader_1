package com.broadviewsoft.daytrader.domain;

import java.io.Serializable;
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
	
	private String firstName;
	private String lastName;
	private Date dob;
	private List<Account> accounts = new ArrayList<Account>();
	
	public Client(String firstName, String lastName, Date dob) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.dob = dob;
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

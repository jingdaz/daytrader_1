package com.broadviewsoft.daytrader.domain;

import java.util.ArrayList;
import java.util.List;

public class DailyStatus {
	// indicator of weakest day
	private boolean weakest =true;
	// indicator of strongest day
	private boolean strongest = true;

	private List<StockStatus> statuses = new ArrayList<StockStatus>();

	public DailyStatus() {

	}

	public boolean isWeakest() {
		return weakest;
	}

	public void setWeakest(boolean weakest) {
		this.weakest = weakest;
	}

	public boolean isStrongest() {
		return strongest;
	}

	public void setStrongest(boolean strongest) {
		this.strongest = strongest;
	}

	public List<StockStatus> getStatuses() {
		return statuses;
	}

	public void setStatuses(List<StockStatus> statuses) {
		this.statuses = statuses;
	}
	
	public void reset() {
		weakest = true;
		strongest = true;
		statuses.clear();
	}

}

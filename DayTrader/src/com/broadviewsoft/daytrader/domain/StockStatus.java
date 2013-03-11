package com.broadviewsoft.daytrader.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StockStatus {
	private Date timestamp = null;
	private List<StockItem> chartItems = new ArrayList<StockItem>();

	public StockStatus() {
		this(new Date());
	}

	public StockStatus(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public List<StockItem> getChartItems() {
		return chartItems;
	}

	public void setChartItems(List<StockItem> chartItems) {
		this.chartItems = chartItems;
	}

}

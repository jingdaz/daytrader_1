package com.broadviewsoft.daytrader.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.domain.DataException;
import com.broadviewsoft.daytrader.domain.DataFileType;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.PriceType;
import com.broadviewsoft.daytrader.domain.Stock;
import com.broadviewsoft.daytrader.domain.StockData;
import com.broadviewsoft.daytrader.domain.StockItem;
import com.broadviewsoft.daytrader.service.impl.HistoryDataFileService;
import com.broadviewsoft.daytrader.util.Util;

public abstract class AbstractDataFeeder {
	private static Log logger = LogFactory.getLog(AbstractDataFeeder.class);

	protected boolean prodMode = false;
  protected boolean initialized = false;
  
	protected List<StockData> allData = new ArrayList<StockData>();

	public abstract void init(String[] symbols);

	public boolean isProdMode() {
		return prodMode;
	}

	public void setProdMode(boolean prodMode) {
		this.prodMode = prodMode;
	}

  public StockItem getYesdayItem(String symbol, int index) {
    return getItemByIndex(symbol, Period.DAY, index);
  }


	protected List<StockItem> getHistoryData(String symbol, Period period,
			Date cutTime) {
		List<StockItem> result = new ArrayList<StockItem>();
		for (StockData sd : allData) {
			if (sd.getStock().getSymbol().equalsIgnoreCase(symbol)) {
				// TODO check NPE
				switch (period) {
				case MIN:
					return findSubList(sd.getMins(), cutTime);
				case MIN5:
					return findSubList(sd.getMin5s(), cutTime);
				case DAY:
					return findSubList(sd.getDays(), cutTime);
				}
				break;
			}
		}
		return result;
	}

	protected StockItem getItemByIndex(String symbol, Period period, int index) {
    List<StockItem> targetItems = null;
    StockItem targetItem = null;
    
    for (StockData sd : allData) {
      if (sd.getStock().getSymbol().equalsIgnoreCase(symbol)) {
        switch (period) {
        case MIN:
          targetItems = sd.getMins();
          break;

        default:
          targetItems = sd.getDays();
        }
      }
    }
    if (targetItems != null) {
       targetItem = targetItems.get(index);
    }
    
    return targetItem;
  }

	public double getPriceByIndex(String symbol, Period period, int index,
			PriceType type) {
		List<StockItem> targetItems = null;

		for (StockData sd : allData) {
			if (sd.getStock().getSymbol().equalsIgnoreCase(symbol)) {
				switch (type) {
				case Typical:
					targetItems = sd.getMins();
					break;

				default:
					targetItems = sd.getDays();
				}

			}
		}

		StockItem targetItem = targetItems.get(index);
		
		double result = getPriceByType(type, targetItem);
		
		return Util.trim(result);
	}

	// FIXME StockItem timestamp cannot be null and mins with size > 1
		public double getPrice(String symbol, Date timestamp, Period period, PriceType type) {
			List<StockItem> targetItems = null;

			for (StockData sd : allData) {
				if (sd.getStock().getSymbol().equalsIgnoreCase(symbol)) {
					switch (period) {
					case DAY:
            targetItems = sd.getDays();
						break;

          case MIN5:
            targetItems = sd.getMin5s();
            break;

          default:
	           targetItems = sd.getMins();
					}

				}
			}

			if (timestamp == null || targetItems.isEmpty()
					|| timestamp.before(targetItems.get(0).getTimestamp())) {
				return 0;
			}

			if (timestamp.after(targetItems.get(targetItems.size() - 1)
					.getTimestamp())) {
				return 0;
			}

			double result = 0;
			for (int i = 0; i < targetItems.size() - 1; i++) {
				if (timestamp.equals(targetItems.get(i).getTimestamp())) {
					result = getPriceByType(type, targetItems.get(i));
				}

				if (timestamp.after(targetItems.get(i).getTimestamp())
						&& timestamp.before(targetItems.get(i + 1).getTimestamp())) {
					result = getPriceByType(type, targetItems.get(i),
							targetItems.get(i + 1));
				}
			}
			return Util.trim(result);
		}
	
	// FIXME StockItem timestamp cannot be null and list with size > 1
	private List<StockItem> findSubList(List<StockItem> list, Date cutTime) {
		List<StockItem> result = new ArrayList<StockItem>();
		if (cutTime == null || list == null || list.isEmpty()
				|| list.get(0) == null
				|| cutTime.before(list.get(0).getTimestamp())) {
			return result;
		}

		if (list.get(list.size() - 1) == null
				|| cutTime.after(list.get(list.size() - 1).getTimestamp())) {
			return result;
		}

		for (int i = 0; i < list.size() - 1; i++) {
			if (!cutTime.before(list.get(i).getTimestamp())
					&& cutTime.before(list.get(i + 1).getTimestamp())) {
				return new ArrayList<StockItem>(list.subList(0, i + 1));
			}
		}

		return result;
	}

	private double getPriceByType(PriceType type, StockItem... items) {
		double result = 0;
		switch (type) {
		case Typical:
			result = averagePrice(items);
			break;

		default:
			try {
				result = ((Double) StockItem.class.getMethod(
						("get" + type.name())).invoke(items[0])).doubleValue();
			} catch (Exception e) {
				logger.error("Error when retrieving stock " + items[0] + " "
						+ type.name() + " price.\r\n " + e.getMessage());
				result = 0;
			}
			break;
		}

		return result;
	}

	private double averagePrice(StockItem... items) {
		int counter = 0;
		double sum = 0;
		for (StockItem si : items) {
			sum += si.getTypical();
			counter++;
		}
		return Util.trim(sum / counter);
	}

	public int getCurItemIndex(String symbol, Date curTime, Period period) {
		List<StockItem> targetItems = null;

		for (StockData sd : allData) {
			if (sd.getStock().getSymbol().equalsIgnoreCase(symbol)) {
				switch (period) {
				case MIN:
					targetItems = sd.getMins();
					break;

				default:
					targetItems = sd.getDays();
				}
			}
		}

		if (curTime == null || targetItems.isEmpty()
				|| curTime.before(targetItems.get(0).getTimestamp())) {
			return -1;
		}

		if (curTime.after(targetItems.get(targetItems.size() - 1)
				.getTimestamp())) {
			return -1;
		}

		for (int i = 0; i < targetItems.size(); i++) {
			if (curTime.equals(targetItems.get(i).getTimestamp())) {
				return i;
			}
		}

		// not found
		return -1;
	}
}

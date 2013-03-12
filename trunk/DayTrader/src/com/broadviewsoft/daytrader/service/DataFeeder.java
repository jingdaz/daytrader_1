package com.broadviewsoft.daytrader.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.domain.DataException;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.PriceType;
import com.broadviewsoft.daytrader.domain.StockItem;
import com.broadviewsoft.daytrader.service.impl.HistoryDataFileService;
import com.broadviewsoft.daytrader.util.Util;

public class DataFeeder
{
  private static Log logger = LogFactory.getLog(DataFeeder.class);

  private boolean prodMode = false;

  private IHistoryDataService service = new HistoryDataFileService();

  List<StockItem> mins = new ArrayList<StockItem>();

  List<StockItem> min5s = new ArrayList<StockItem>();

  List<StockItem> days = new ArrayList<StockItem>();

  public DataFeeder()
  {
    this(false);
  }

  public DataFeeder(boolean prodMode)
  {
    try
    {
      mins = service.loadData("UVXY", Period.MIN);
      min5s = service.loadData("UVXY", Period.MIN5);
      days = service.loadData("UVXY", Period.DAY);
      logger.info("Finished loading historical data.");
    }
    catch (DataException e)
    {
      logger.error("Error when loading historical data.", e);
    }

    this.prodMode = prodMode;
  }

  public boolean isProdMode()
  {
    return prodMode;
  }

  public void setProdMode(boolean prodMode)
  {
    this.prodMode = prodMode;
  }

  public List<StockItem> getHistoryData(String symbol, Period period, Date cutTime)
  {
    List<StockItem> result = new ArrayList<StockItem>();
    // TODO check NPE
    switch (period)
    {
      case MIN:
        return findSubList(mins, cutTime);
      case MIN5:
        return findSubList(min5s, cutTime);
    }
    return result;
  }

  // FIXME StockItem timestamp cannot be null and mins with size > 1
  public double getPrice(String symbol, Date timestamp, PriceType type)
  {
    List<StockItem> targetItems = null;
    switch (type)
    {
      case Typical:
        targetItems = mins;
        break;

      default:
        targetItems = days;
    }

    if (timestamp == null || targetItems.isEmpty() || timestamp.before(targetItems.get(0).getTimestamp()))
    {
      return 0;
    }

    if (timestamp.after(targetItems.get(targetItems.size() - 1).getTimestamp()))
    {
      return 0;
    }

    double result = 0;
    for (int i = 0; i < targetItems.size() - 1; i++)
    {
      if (timestamp.equals(targetItems.get(i).getTimestamp()))
      {
        result = getPriceByType(type, targetItems.get(i));
      }

      if (timestamp.after(targetItems.get(i).getTimestamp()) && timestamp.before(targetItems.get(i + 1).getTimestamp()))
      {
        result = getPriceByType(type, targetItems.get(i), targetItems.get(i + 1));
      }
    }
    return Util.trim(result);
  }

  // FIXME StockItem timestamp cannot be null and list with size > 1
  private List<StockItem> findSubList(List<StockItem> list, Date cutTime)
  {
    List<StockItem> result = new ArrayList<StockItem>();
    if (cutTime == null || list == null || list.isEmpty() || list.get(0) == null || cutTime.before(list.get(0).getTimestamp()))
    {
      return result;
    }

    if (list.get(list.size() - 1) == null || cutTime.after(list.get(list.size() - 1).getTimestamp()))
    {
      return result;
    }

    for (int i = 0; i < list.size() - 1; i++)
    {
      if (!cutTime.before(list.get(i).getTimestamp()) && cutTime.before(list.get(i + 1).getTimestamp()))
      {
        return new ArrayList<StockItem>(list.subList(0, i+1));
      }
    }

    return result;
  }

  private double getPriceByType(PriceType type, StockItem... items)
  {
    double result = 0;
    switch (type)
    {
      case Typical:
        result = averagePrice(items);
        break;

      default:
        try
        {
          result = ((Double) StockItem.class.getMethod(("get" + type.name())).invoke(items[0])).doubleValue();
        }
        catch (Exception e)
        {
          logger.error("Error when retrieving stock " + items[0] + " " + type.name() + " price.\r\n " + e.getMessage());
          result = 0;
        }
        break;
    }

    return result;
  }

  private double averagePrice(StockItem... items)
  {
    int counter = 0;
    double sum = 0;
    for (StockItem si : items)
    {
      double avg = (si.getHigh() + si.getLow() + si.getClose()) / 3.0;
      sum += avg;
      counter++;
    }
    return Util.trim(sum / counter);
  }

  // FIXME ratio?
  private double averagePrice(StockItem item1, StockItem item2)
  {
    double avg1 = averagePrice(item1);
    double avg2 = averagePrice(item2);
    return Util.trim((avg1 + avg2) / 2.0);
  }
}

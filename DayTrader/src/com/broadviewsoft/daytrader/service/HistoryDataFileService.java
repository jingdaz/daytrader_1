package com.broadviewsoft.daytrader.service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrRegEx;
import org.supercsv.cellprocessor.constraint.LMinMax;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.domain.CurrencyType;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.StockItem;

public class HistoryDataFileService implements HistoryDataService
{

  public List<StockItem> loadData(String symbol, Period period)
  {
    List<StockItem> result = new ArrayList<StockItem>();
    String location = Constants.HISTORY_DATA_PATH;
    String filename = symbol + Constants.FILENAME_CONNECTOR + period.name() + Constants.STOCK_DATA_FILE_EXTENSION;

    String csvFilename = location + filename;
    ICsvBeanReader beanReader = null;
    try
    {
      beanReader = new CsvBeanReader(new FileReader(csvFilename), CsvPreference.STANDARD_PREFERENCE);

      // the header elements are used to map the values to the bean (names
      // must match)
      final String[] header = beanReader.getHeader(true);
      final CellProcessor[] processors = getProcessors();

      StockItem item;
      while ((item = beanReader.read(StockItem.class, header, processors)) != null)
      {
        result.add(item);
      }

    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
    }
    finally
    {
      if (beanReader != null)
      {
        try
        {
          beanReader.close();
        }
        catch (IOException e)
        {
        }
      }
    }
    return result;
  }

  /**
   * Sets up the processors used for the examples. There are 10 CSV columns, so
   * 10 processors are defined. Empty columns are read as null (hence the
   * NotNull() for mandatory columns).
   * 
   * @return the cell processors
   */
  private static CellProcessor[] getProcessors()
  {

    final String emailRegex = "[a-z0-9\\._]+@[a-z0-9\\.]+"; // just an
    // example, not
    // very robust!
    StrRegEx.registerMessage(emailRegex, "must be a valid email address");

    final CellProcessor[] processors = new CellProcessor[]
      {
          new ParseDate(Constants.STOCK_PRICE_TIMESTAMP_PATTERN), // timestamp
          new NotNull(new ParseDouble()), // open
          new NotNull(new ParseDouble()), // high
          new NotNull(new ParseDouble()), // low
          new NotNull(new ParseDouble()), // close
          new Optional(new ParseDouble()), // rsi
          new NotNull(new ParseDouble()), // cci
          new LMinMax(0L, LMinMax.MAX_LONG) // volume
      };

    return processors;
  }

  public static void main(String[] args)
  {
    HistoryDataFileService service = new HistoryDataFileService();
    // print out stock headers
    CurrencyType curType = CurrencyType.USD;
    String symbol = "UVXY";
    Period period = Period.MIN5;
    System.out.println(StockItem.printHeaders(curType, symbol, period));

    // print out stock data
    List<StockItem> result = service.loadData(symbol, period);
    for (StockItem si : result)
    {
      System.out.println(si);
    }
  }
}

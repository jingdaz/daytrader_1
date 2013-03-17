package com.broadviewsoft.daytrader.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestDate
{

  /**
   * @param args
   * @throws ParseException 
   */
  public static void main(String[] args) throws ParseException
  {
    DateFormat df = new SimpleDateFormat("m/d/yyyy");
    Date first = df.parse("1/1/1970");
    long sec = first.getTime();
    System.out.println("first in milli-seconds " + sec);
    Date d = new Date((long)1363354200*1000);
    System.out.println("date is " + d);

  }

}
 

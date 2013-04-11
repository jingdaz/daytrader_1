package com.broadviewsoft.daytrader.service;

import org.faceless.graph2.*;
import java.awt.Color;
import java.awt.Paint;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.text.NumberFormat;

public class CandleGraphExample
{
    public static void main (String args[]) throws IOException
    {
        // Create the graph and set up the Axes
        //
        AxesGraph graph = new AxesGraph();
        graph.setAxis(Axis.LEFT, new NumericAxis(NumberFormat.getCurrencyInstance(), Axis.DENSITY_NORMAL));
        graph.setAxis(Axis.BOTTOM, new DateAxis());
        TextStyle ts = new TextStyle("Default", 10, Color.BLACK);
        ts.setRotate(-20);
        ts.setAlign(Align.RIGHT);
        ts.setPaddingRight(-6);
        ts.setPaddingTop(-1);
        graph.getAxis(Axis.BOTTOM).setToothTextStyle(ts);
        
        // Create candle series
        //
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        CandleSeries candle = new CandleSeries("YHOO");
        try {
            // Add the data
            //
            candle.set(df.parse("02-JAN-2002"),18.14,18.63,17.68,18.69);
            candle.set(df.parse("03-JAN-2002"),18.70,19.13,18.54,19.29);
            candle.set(df.parse("04-JAN-2002"),19.00,18.90,18.53,19.81);
            candle.set(df.parse("07-JAN-2002"),18.70,19.73,18.65,19.94);
            candle.set(df.parse("08-JAN-2002"),19.40,19.53,19.25,19.73); 
            candle.set(df.parse("09-JAN-2002"),19.80,20.25,19.77,21.35);
            candle.set(df.parse("10-JAN-2002"),20.06,20.49,19.95,20.71);
            candle.set(df.parse("11-JAN-2002"),20.52,20.16,20.02,20.90);
            candle.set(df.parse("14-JAN-2002"),19.71,19.01,18.87,19.92);
            candle.set(df.parse("15-JAN-2002"),19.22,19.47,18.82,19.48);
            candle.set(df.parse("16-JAN-2002"),18.92,17.87,17.80,19.08);
            candle.set(df.parse("17-JAN-2002"),19.37,20.12,19.25,20.38);
            candle.set(df.parse("18-JAN-2002"),19.66,19.20,18.95,19.98);
            candle.set(df.parse("22-JAN-2002"),19.90,18.42,18.40,20.05);
            candle.set(df.parse("23-JAN-2002"),18.80,18.44,17.97,18.85);
            candle.set(df.parse("24-JAN-2002"),18.93,18.19,18.02,19.40);
            candle.set(df.parse("25-JAN-2002"),18.32,18.68,18.06,18.85);
            candle.set(df.parse("28-JAN-2002"),18.83,18.70,18.40,18.91);
            candle.set(df.parse("29-JAN-2002"),18.81,18.18,17.71,18.81);
            candle.set(df.parse("30-JAN-2002"),18.19,17.19,16.18,18.20);
            candle.set(df.parse("31-JAN-2002"),17.70,17.24,16.79,17.80);
            
            // Set some candle options
            //
            candle.setBarWidth(0.8);
            candle.setStyle(new Style(Color.BLACK));
            candle.setStyle(df.parse("23-JAN-2002"), new Style(Color.RED));
            
            // Create and add markers to the candle series
            //
            Marker m = new Marker("star", 14);
            m.setStyle(new Style(Color.RED));
            candle.addMarker(m, DateAxis.toDouble(df.parse("23-JAN-2002")), 17.85);
            candle.addMarker(new Text("My Birthday", new TextStyle("Default", 10, Color.RED)),DateAxis.toDouble(df.parse("23-JAN-2002")), 17.65);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        
        // Add the series to the graph and set the back wall paint
        //
	graph.addSeries(candle);
        graph.setBackWallPaint(null, new Color(204,204,204), Axis.LEFT, Axis.BOTTOM, null); 

        // Add a title
        //
	graph.addText("January 2002 Yahoo! Inc Stock Prices", new TextStyle("Times", 24, Color.BLUE, Align.CENTER));
        
        // Write the file
        //
	ImageOutput image = new ImageOutput(500, 500);
	graph.draw(image);
	FileOutputStream out = new FileOutputStream("CandleGraphExample.png");
	image.writePNG(out, 0);
        out.close();
    }
}
package com.broadviewsoft.daytrader.service;

// $Id: XMLToGraph.java 13826 2011-09-15 15:28:02Z mike $

import org.faceless.graph2.FlashOutput;
import org.faceless.graph2.Graph;
import org.faceless.graph2.ImageOutput;
import org.faceless.graph2.SVGOutput;
import org.faceless.graph2.XMLGraph;
import org.xml.sax.*;
import java.io.*;
import java.awt.*;
import java.net.URL;

/**
 * This simple example takes a list of filenames or URLs containing XML as
 * arguments, and converts each one in turn to a PNG image containing the
 * equivalent graph.
 * 
 * Example Usage:
 * 
 * java XMLToGraph xml/*.xml java XMLToGraph
 * http://big.faceless.org/products/graph/text.xml java XMLToGraph --svg
 * test.xml
 * 
 */
public class XMLToGraph
{
  private static String format = "png";

  public static void main(String[] args)
      throws Exception
  {
    if (args.length == 0)
    {
      System.out.println("Usage: java XMLToGraph [ --svg | --png | --swf | --pdf | <filename> ] ...\n");
      System.exit(0);
    }
    for (int i = 0; i < args.length; i++)
    {
      if (args[i].equals("--svg"))
      {
        format = "svg";
      }
      else if (args[i].equals("--png"))
      {
        format = "png";
      }
      else if (args[i].equals("--swf"))
      {
        format = "swf";
      }
      else if (args[i].equals("--pdf"))
      {
        format = "pdf";
      }
      else
      {
        URL url = getInput(args[i]);
        InputSource input = new InputSource(url.openStream());
        input.setSystemId(url.toString());
        String outfile = getOutput(url);

        System.out.println("--------------------------------\nParsing \"" + url + "\"... ");
        try
        {
          XMLGraph xml = new XMLGraph();
          xml.setResourceProvider(XMLGraph.getDefaultResourceProvider(format));
          xml.parse(input);
          Graph graph = xml.getGraph();

          OutputStream out = new FileOutputStream(outfile);
          if (format == "svg")
          {
            SVGOutput image = new SVGOutput(xml.getWidth(), xml.getHeight());
            graph.draw(image);
            image.writeSVG(new OutputStreamWriter(out, "UTF-8"), true);
          }
          else if (format == "png")
          {
            ImageOutput image = new ImageOutput(xml.getWidth(), xml.getHeight());
            graph.draw(image);
            image.writePNG(out, 0);
          }
          else if (format == "swf")
          {
            FlashOutput image = new FlashOutput(xml.getWidth(), xml.getHeight());
            graph.draw(image);
            image.writeFlash(out);
            /*
             * If you have our PDF library in your classpath, uncomment this
             * block to generate PDFs as well
             * 
             * } else if (format=="pdf") { org.faceless.pdf2.PDF pdf = new
             * org.faceless.pdf2.PDF(); org.faceless.pdf2.PDFPage page =
             * pdf.newPage("A4"); org.faceless.pdf2.PDFCanvas canvas = new
             * org.faceless.pdf2.PDFCanvas(xml.getWidth(), xml.getHeight());
             * PDFOutput image = new PDFOutput(canvas); graph.draw(image);
             * page.drawCanvas(canvas, (page.getWidth()-canvas.getWidth())/2,
             * (page.getHeight()-canvas.getHeight())/2,
             * (page.getWidth()+canvas.getWidth())/2,
             * (page.getHeight()+canvas.getHeight())/2); pdf.render(out);
             */
          }
          else
          {
            throw new IllegalArgumentException("Unknown format " + format);
          }
          out.close();
          System.out.println("Created \"" + outfile + "\"");
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    }
    System.exit(0); // Otherwise Java 1.3 won't exit
  }

  // Given a URL which may be relative to the current working directory,
  // return a fully qualified URL.
  //
  private static final URL getInput(String arg)
      throws IOException
  {
    URL url = new URL("file:" + System.getProperty("user.dir") + System.getProperty("file.separator"));
    url = new URL(url, arg);
    return url;
  }

  // Given a URL, return a filename to write the graph for that URL to.
  //
  private static final String getOutput(URL url)
  {
    String file;
    if (url.getProtocol().equals("file"))
    {
      file = url.getFile();
      if (file.endsWith(".xml"))
        file = file.substring(0, file.length() - 4);
      file += "." + format;
    }
    else
    {
      file = "XMLGraph." + format;
    }
    return file;
  }
}

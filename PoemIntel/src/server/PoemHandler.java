package server;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import poem.Poem;
import poem.PubInfo;

public class PoemHandler extends DefaultHandler
{
  
  private boolean foundResponse;
  private String title, author, year, body, waitingFor;
  
  public PoemHandler(String title)
  {
    this.title = title;
    this.author = "";
    this.year = "";
    this.body = "";
  }
  
  public Poem getPoem()
  {
    PubInfo pubInfo = new PubInfo(title, author, year);
    return new Poem(pubInfo, body);
  }
  
  @Override
  public void startDocument()
  {
    foundResponse = false;
    waitingFor = "";
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes)
  {
    if (foundResponse)
    {
      waitingFor = qName + "_characters";
    }
    else if (qName.equals("poem"))
    {
      waitingFor = "title_characters";
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName)
  {
    if (qName.equals("poem"))
      foundResponse = false;
    if (foundResponse)
      waitingFor = "";
  }

  @Override
  public void characters(char[] ch, int start, int length)
  {
    if (waitingFor.equals("author_characters"))
    {
      author = new String(ch, start, length).trim();
    } 
    else if (waitingFor.equals("year_characters"))
    {
      year = new String(ch, start, length).trim();
    }
    else if (waitingFor.equals("body_characters"))
    {
      body += new String(ch, start, length).trim();
    }
    else if (waitingFor.equals("title_characters"))
    {
      String poemTitle = new String(ch, start, length).trim();
      if (poemTitle.equals(this.title))
      {
        foundResponse = true;
      }
    }
    
  }

}

package server;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import poem.Poem;
import poem.PubInfo;

public class PoemHandler extends DefaultHandler
{
  
  private boolean foundResponse;
  private String title, author, year, body, root, waitingFor;
  
  public PoemHandler(String root)
  {
    this.root = root;
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
  }

  @Override
  public void endElement(String uri, String localName, String qName)
  {
  }

  @Override
  public void characters(char[] ch, int start, int length)
  {
  }

}

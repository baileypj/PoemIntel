package server;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import poem.Poem;
import poem.PubInfo;

public class ListHandler extends DefaultHandler
{
  private boolean foundResponse, authorSearch;
  private String title, author, year, body, searchYear, searchAuthor, waitingFor;
  private ArrayList<Poem> list;
  
  /**
   * Constructor.
   * 
   * @param searchTerm
   *          specific year or author to be searched for
   * @param authorSearch
   *          true if searching by author, false for year
   */
  public ListHandler(String searchTerm, boolean authorSearch)
  {
    if (authorSearch)
    {
      this.searchAuthor = searchTerm;
    }
    else
    {
      this.searchYear = searchTerm;
    }
    this.authorSearch = authorSearch;
    this.year = "";
    this.author = "";
    this.title = "";
    this.body = "";
    list = new ArrayList<Poem>();
  }
  
  public ArrayList<Poem> getList()
  {
    return list;
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
    boolean add = false;
    if (qName.equals("poem")) // end of poem, check requirements of search
    {
      foundResponse = false;

      if (authorSearch)
      {
        if (this.author.equals(this.searchAuthor))
        {
          add = true;
        }
      }
      else
      {
        if (this.year.equals(this.searchYear))
        {
          add = true;
        }
      }
    }

    if (add)
    {
      list.add(new Poem(new PubInfo(title, author, year), body));
      body = "";
    }

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
      title = new String(ch, start, length).trim();
      foundResponse = true;
    }
    
  }

}

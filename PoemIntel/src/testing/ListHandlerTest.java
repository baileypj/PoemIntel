package testing;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;

import poem.Poem;
import poem.PubInfo;
import server.HttpServer;
import server.ListHandler;
import server.PoemHandler;

public class ListHandlerTest
{
  
  public static void main(String[] args) throws Exception
  {
    
    // Get poems from file
    FileInputStream fis = new FileInputStream("public_html/exampleDatabase.xml");
    byte[] file_content = new byte[fis.available()];
    fis.read(file_content);
    ByteArrayInputStream bais = new ByteArrayInputStream(file_content);

    // Parse the poems file
    InputSource inputSource = new InputSource(bais);
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser parser = factory.newSAXParser();
    ListHandler listHandler = new ListHandler("John Smith", true);
    parser.parse(inputSource, listHandler);

    // Get the poem
    ArrayList<Poem> poemList = listHandler.getList();
//    String poemString = poem.getPoemAsXML();
   
    
    for (Poem poem : poemList)
    {
      System.out.println(poem.getPubInfo().getAuthor());
      System.out.println(poem.getPubInfo().getTitle());
      System.out.println(poem.getPubInfo().getYear());
      System.out.println(poem.getBody());
      System.out.println();
    }
  }

}

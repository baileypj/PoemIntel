package testing;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;

import poem.Poem;
import server.HttpServer;
import server.PoemHandler;

public class PoemHandlerTest
{
  
  public static void main(String[] args) throws Exception
  {
    
    // Get poems from file
    FileInputStream fis = new FileInputStream("public_html/examplePoem1.xml");
    byte[] file_content = new byte[fis.available()];
    fis.read(file_content);
    ByteArrayInputStream bais = new ByteArrayInputStream(file_content);

    // Parse the poems file
    InputSource inputSource = new InputSource(bais);
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser parser = factory.newSAXParser();
    PoemHandler poemHandler = new PoemHandler("Secrets Under Trees");
    parser.parse(inputSource, poemHandler);

    // Get the poem
    Poem poem = poemHandler.getPoem();
    String poemString = poem.getPoemAsXML();
    
    System.out.println(poem.getPubInfo().getAuthor());
    System.out.println(poem.getPubInfo().getTitle());
    System.out.println(poem.getPubInfo().getYear());
    System.out.println(poem.getBody());
  }

}

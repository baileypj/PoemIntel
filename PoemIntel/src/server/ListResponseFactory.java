package server;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import poem.Poem;

public class ListResponseFactory
{
  private static final int header_length = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>".length();

  public static String createGETResponse(NameValueMap queryParameters)
      throws IOException, ParserConfigurationException, SAXException
  {
    // Read the file
    FileInputStream fis = new FileInputStream(HttpServer.poemDatabase);
    int file_length = fis.available() - header_length;
    byte[] file_content = new byte[file_length];

    // removes header (HEADER WILL CONFLICT WITH HEADER ADDED DURING INSERT)
    fis.read(file_content, 0, header_length);
    fis.read(file_content, 0, file_length);
    fis.close();

    // Filter for every valid query
    for (Iterator<String> qi = queryParameters.getNames(); qi.hasNext();)
    {
      // Gets the Name of the query parameter
      String param = qi.next();

      // Sets searchType to the appropriate type
      // If param not either 'author' or 'year', skip
      boolean searchType;
      if (param.equals("author"))
        searchType = true;
      else if (param.equals("year"))
        searchType = false;
      else
        continue;

      // Sets the searchTerm to the Value associated with the Name
      String searchTerm = queryParameters.getValue(param);

      // Parse the poems file
      ByteArrayInputStream bais = new ByteArrayInputStream(file_content);
      InputSource inputSource = new InputSource(bais);
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser parser = factory.newSAXParser();
      ListHandler handler = new ListHandler(searchTerm, searchType);
      parser.parse(inputSource, handler);

      // Gets the (filtered) list of poems from the handler
      ArrayList<Poem> poems = handler.getList();

      // Creates the new filtered list as XML
      String filteredList = "<poems>";
      for (Poem poem : poems) filteredList += poem.getPoemAsXML();
      filteredList += "</poems>";

      // Sets the file_content to the filtered list
      byte[] new_file_content = filteredList.getBytes();
      file_content = new_file_content;
      file_length = file_content.length;
    }

    // Get the insert reference type
    String type = queryParameters.getValue("type");

    // Get insert content
    byte[] insert_content;
    int insert_length;
    if (type != null && type.equals("text"))
    {
      // Insert reference to TYPE-converting xsl file if specified
      // NOTE: we will only be doing text and html, but this leaves it open to more types.
      insert_content =
          ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<?xml-stylesheet type=\"text/xsl\" href=\""
              + "http://" + HttpServer.serverAddress + ":" + HttpServer.serverPort + "/list" + type
              + ".xsl\"?>\r\n\r\n").getBytes();
      insert_length = insert_content.length;
    }
    else
    {
      // Insert reference to html-converting xsl file if specified
      insert_content =
          ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<?xml-stylesheet type=\"text/xsl\" href=\""
              + "http://" + HttpServer.serverAddress + ":" + HttpServer.serverPort
              + "/listhtml.xsl\"?>\r\n\r\n").getBytes();
      insert_length = insert_content.length;
    }

    // Set content array to length of both file and insert content
    byte[] content = new byte[insert_length + file_length];
    ByteBuffer bb = ByteBuffer.wrap(content);

    // load content (first XSL insert, then XML)
    bb.put(insert_content);
    bb.put(file_content);

    // Return content
    return new String(content, 0, content.length);
  }
}

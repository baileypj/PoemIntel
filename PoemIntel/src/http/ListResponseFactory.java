package http;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ListResponseFactory
{
  private static final String FILENAME = "public_html/poems.xml";

  public static String createGETResponse(NameValueMap queryParameters) throws IOException
  {
    // Create a stream for the file
    // and determine its length
    FileInputStream fis = new FileInputStream(FILENAME);
    int file_length = fis.available();

    // Read the file
    byte[] file_content = new byte[file_length];
    fis.read(file_content);
    fis.close();

    // Get the insert reference type
    String type = queryParameters.getValue("type");
    String title = queryParameters.getValue("title");
    String author = queryParameters.getValue("author");
    String year = queryParameters.getValue("year");

    // Get insert content
    byte[] insert_content;
    int insert_length;
    if (type != null && type.equals("text"))
    {
      // Insert reference to TYPE-converting xsl file if specified
      // NOTE: we will only be doing text and html, but this leaves it open to more types.
      insert_content = ("<?xml version=\"1.0\"?>\r\n<?xml-stylesheet type=\"" + type
          + "/xsl\" href=\"http://127.0.0.1:8080/list" + type + ".xsl\"?>\r\n\r\n").getBytes();
      insert_length = insert_content.length;
    }
    else
    {
      // Insert reference to html-converting xsl file if specified
      // NOTE: i'm not sure if xml-stylesheet type="html/xsl" is a valid type
      insert_content = ("<?xml version=\"1.0\"?>\r\n<?xml-stylesheet type=\"html/xsl\" href=\""
          + "http://127.0.0.1:8080/listhtml.xsl\"?>\r\n\r\n").getBytes();
      insert_length = insert_content.length;
    }

    // TODO TODO TODO TODO TODO
    // APPLY OTHER QUERY PARAMETERS

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

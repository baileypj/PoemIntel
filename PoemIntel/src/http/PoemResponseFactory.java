package http;

import java.nio.ByteBuffer;

public class PoemResponseFactory
{
  public static String createGETResponse(String poemName, NameValueMap queryParameters)
  {
    // GET XML POEM FROM FILE
    // TODO TODO PLACEHOLDER TODO TODO
    // Something like String poem = GetPoemFromFile(POEM NAME OR ID, poems.xml)

    // FOR NOW MADE UP EXAMPLE POEM RETRIEVED FROM FILE
    String poem =
        "<poem><title>test</title><author>test</author><year>test</year><body>test</body></poem>";

    // Get file content
    byte[] file_content = poem.getBytes();
    int file_length = poem.length();

    // Get the insert reference type
    String type = queryParameters.getValue("type");

    // Get insert content
    byte[] insert_content;
    int insert_length;
    if (type != null && type.equals("text"))
    {
      // Insert reference to TYPE-converting xsl file if specified
      // NOTE: we will only be doing text and html, but this leaves it open to more types.
      insert_content = ("<?xml version=\"1.0\"?>\r\n<?xml-stylesheet type=\"" + type
          + "/xsl\" href=\"http://127.0.0.1:8080/poem" + type + ".xsl\"?>\r\n\r\n").getBytes();
      insert_length = insert_content.length;
    }
    else
    {
      // Insert reference to html-converting xsl file if specified
      // NOTE: i'm not sure if xml-stylesheet type="html/xsl" is a valid type
      insert_content = ("<?xml version=\"1.0\"?>\r\n<?xml-stylesheet type=\"html/xsl\" href=\""
          + "http://127.0.0.1:8080/poemhtml.xsl\"?>\r\n\r\n").getBytes();
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

  public static String createPOSTResponse(String type, String uri)
  {
    String response;

    // Get response content
    if (type != null && type.equals("text"))
    {
      // Return text response if specified
      response = String.format("%s has been updated");
    }
    else
    {
      // Return html response if specified
      response = String.format("<html><body><p>%s has been updated</p></body></html>", uri);
    }
    return response;
  }
}

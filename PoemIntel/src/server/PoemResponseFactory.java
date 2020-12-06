package server;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;

public class PoemResponseFactory
{
  public static String createGETResponse(String poemName, NameValueMap queryParameters) throws FileNotFoundException
  {
//    // Get poems from file
//    FileInputStream fis = new FileInputStream(HttpServer.poemDatabase);
//    byte[] file_content = new byte[fis.available()];
//    fis.read(file_content);
//    ByteArrayInputStream bais = new ByteArrayInputStream(file_content);
//
//    // Parse the poems file
//    InputSource inputSource = new InputSource(bais);
//    SAXParserFactory factory = SAXParserFactory.newInstance();
//    SAXParser parser = factory.newSAXParser();
//    PoemHandler handler = new PoemHandler(poemName);
//    parser.parse(inputSource, handler);
//
//    // Get the poem
//    Poem poem = poemHandler.getPoem();
//    String poemString = poem.getPoemAsXML();

    String poemString = "<poem\r\n" +
        "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" +
        "xsi:noNamespaceSchemaLocation=\"poem.xsd\">\r\n" +
        "<pubInfo>\r\n" +
        "    <title>Secrets Under Trees</title>\r\n" +
        "    <author>Jessica Knight</author>\r\n" +
        "    <year>2016</year>\r\n" +
        "  </pubInfo>\r\n" +
        "  <body>Tiny little secrets\r\n" +
        "Get buried in the dirt,\r\n" +
        "And if they were dug up,\r\n" +
        "Someone would probably get hurt,\r\n" +
        "So leave them safely there,\r\n" +
        "To rot amongst the leaves,\r\n" +
        "Admiring instead,\r\n" +
        "The truth in summer's green trees.</body>\r\n" +
        "</poem>\r\n";

    // Get the poem content
    byte[] poem_content = poemString.getBytes();
    int poem_length = poemString.length();

    // Get the insert reference type
    String type = queryParameters.getValue("type");

    // Get insert content
    byte[] insert_content;
    int insert_length;
    if (type != null && type.equals("text"))
    {
      // Insert reference to TYPE-converting xsl file if specified
      // NOTE: we will only be doing text and html, but this leaves it open to more types.
      insert_content = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<?xml-stylesheet type=\"" + type
          + "/xsl\" href=\"http://127.0.0.1:8080/poem" + type + ".xsl\"?>\r\n\r\n").getBytes();
      insert_length = insert_content.length;
    }
    else
    {
      // Insert reference to html-converting xsl file if specified
      // NOTE: i'm not sure if xml-stylesheet type="html/xsl" is a valid type
      insert_content = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<?xml-stylesheet type=\"text/xsl\" href=\""
          + "http://127.0.0.1:8080/poemhtml.xsl\"?>\r\n\r\n").getBytes();
      insert_length = insert_content.length;
    }

    // Set content array to length of both file and insert content
    byte[] content = new byte[insert_length + poem_length];
    ByteBuffer bb = ByteBuffer.wrap(content);

    // load content (first XSL insert, then XML)
    bb.put(insert_content);
    bb.put(poem_content);

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

package server;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.InvalidPropertiesFormatException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import poem.Poem;

public class PoemResponseFactory
{
  public static String createGETResponse(NameValueMap queryParameters, String poemName)
      throws IOException, ParserConfigurationException, SAXException
  {
    // Read the file
    FileInputStream fis = new FileInputStream(HttpServer.poemDatabase);
    byte[] file_content = new byte[fis.available()];
    fis.read(file_content);
    fis.close();

    // Parse the poems file
    ByteArrayInputStream bais = new ByteArrayInputStream(file_content);
    InputSource inputSource = new InputSource(bais);
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser parser = factory.newSAXParser();
    PoemHandler handler = new PoemHandler(poemName);
    parser.parse(inputSource, handler);

    // Get the poem, throw fnfexception if no poems match name
    Poem poem = handler.getPoem();
    if(poem == null) throw new FileNotFoundException();
    String poemString = poem.getPoemAsXML();

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
              + "/poemhtml.xsl\"?>\r\n\r\n").getBytes();
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

  public static String createPOSTResponse(NameValueMap queryParameters, String poemName, byte[] content) throws ParserConfigurationException, SAXException, IOException
  {
    // CHECK XML AGAINST SCHEMA
    // TODO TODO TODO TODO TODO

    // Read the file
    ByteArrayInputStream bais = new ByteArrayInputStream(content);
    InputSource inputSource = new InputSource(bais);
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser parser = factory.newSAXParser();
    PoemHandler handler = new PoemHandler(poemName);
    parser.parse(inputSource, handler);

    // Get the poem
    Poem poem = handler.getPoem();
    if(poem == null) throw new InvalidPropertiesFormatException("Poem is missing essential information");
    String poemString = "\r\n\r\n" + poem.getPoemAsXML();

    // Write the poem to the database
    FileOutputStream fos = new FileOutputStream(HttpServer.poemDatabase, true);
    fos.write(poemString.getBytes());
    fos.close();

    // Get the insert reference type
    String type = queryParameters.getValue("type");

    // Get response content
    String response;
    if (type != null && type.equals("text"))
    {
      // Return text response if specified
      response = String.format("\"%s\" has been posted");
    }
    else
    {
      // Return html response if specified
      response = String.format("<html><body><p>\"%s\" has been posted</p></body></html>", poemName);
    }
    return response;
  }
}

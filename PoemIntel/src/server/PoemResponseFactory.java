package server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.InvalidPropertiesFormatException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

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
    if (poem == null)
    {
      throw new FileNotFoundException();
    }

    // Get the poem content
    byte[] poem_content = poem.getPoemAsXML().getBytes();
    int poem_length = poem_content.length;

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

  public static String createPOSTResponse(NameValueMap queryParameters, String poemName,
      byte[] content) throws ParserConfigurationException, SAXException, IOException
  {
    // Throw error if poem is larger than specified max poem size
    if (content.length > HttpServer.maxPoemSize)
    {
      throw new InvalidPropertiesFormatException(
          "Poem is larger than " + HttpServer.maxPoemSize + " bytes");
    }

    // Create input stream for both factories
    ByteArrayInputStream bais = new ByteArrayInputStream(content);

    // Check poem against the schema
    try
    {
      StreamSource streamSource = new StreamSource(bais);
      SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      Schema schema = schemaFactory.newSchema(new File(HttpServer.poemSchema));
      Validator validator = schema.newValidator();
      validator.validate(streamSource);
    }
    catch (IOException | SAXException e)
    {
      throw new InvalidPropertiesFormatException("Poem structure is invalid");
    }

    // Parse the poem
    bais.reset();
    InputSource inputSource = new InputSource(bais);
    SAXParserFactory saxFactory = SAXParserFactory.newInstance();
    SAXParser parser = saxFactory.newSAXParser();
    PoemHandler handler = new PoemHandler(poemName);
    parser.parse(inputSource, handler);

    // Get the poem
    Poem poem = handler.getPoem();
    if (poem == null)
    {
      throw new InvalidPropertiesFormatException("Poem is missing essential information");
    }

    // Write the poem to the database

    // Get database (WITHOUT </poems>) bytes
    int endtag_length = "</poems>".length();
    FileInputStream fis = new FileInputStream(HttpServer.poemDatabase);
    int database_length = fis.available() - endtag_length;
    byte[] database_content = new byte[database_length];
    fis.read(database_content, 0, database_length);
    fis.close();

    // Get new poem bytes
    String poemString = "\r\n\r\n" + poem.getPoemAsXML() + "\r\n</poems>";
    byte[] poem_content = poemString.getBytes();
    int poem_length = poem_content.length;

    // Set content array to length of both file and insert content
    byte[] new_database = new byte[database_length + poem_length];
    ByteBuffer bb = ByteBuffer.wrap(new_database);

    // load into new_database (first database (without end tag), then new XML poem)
    bb.put(database_content);
    bb.put(poem_content);

    // Write to file
    FileOutputStream fos = new FileOutputStream(HttpServer.poemDatabase);
    fos.write(new_database);
    fos.close();

    // Get the insert reference type
    String type = queryParameters.getValue("type");

    // Get response content
    String response;
    if (type != null && type.equals("text"))
    {
      // Return text response if specified
      response = String.format("\"%s\" has been posted", poemName);
    }
    else
    {
      // Return html response if specified
      response = String.format("<html><body><p>\"%s\" has been posted</p></body></html>", poemName);
    }
    return response;
  }
}

package testing;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

public class XMLSchemaTest
{

  public static void main(String[] args)
  {

    String xmlPath = "clientPoems/poem1.xml";
    String xsdPath = "clientPoems/poem.xsd";

    if (validateXMLSchema(xsdPath, xmlPath))
    {
      System.out.println("XML is valid!");
    }
    else
    {
      System.out.println("XML is NOT valid!");
    }

  }

  public static boolean validateXMLSchema(String xsdPath, String xmlPath)
  {

    try
    {
      SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      Schema schema = factory.newSchema(new File(xsdPath));
      Validator validator = schema.newValidator();
      validator.validate(new StreamSource(new File(xmlPath)));
    }
    catch (IOException | SAXException e)
    {
      return false;
    }
    return true;
  }

}

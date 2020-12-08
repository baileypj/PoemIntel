package server;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class PoemServlet extends AbstractHttpServlet
{
  public PoemServlet()
  {
    super();
  }

  public PoemServlet(HttpInputStream in, HttpOutputStream out)
  {
    super(in, out);
  }

  @Override
  public void doGet(HttpRequest req, HttpResponse res)
  {
    byte[] content;
    MIMETyper mimeTyper;
    NameValueMap queryParameters;
    SecurityManager security;
    String uri;

    // Initialization
    mimeTyper = MIMETyper.createInstance();
    uri = "public_html" + req.getRequestURI();

    // Get the SecurityManager
    security = System.getSecurityManager();

    try
    {
      // Check for read permission before doing anything
      if (security != null)
        security.checkRead(uri);

      // Get poem name (convert underscores to spaces)
      String poemName = FileTyper.getFileName(uri);

      // Get query parameters
      queryParameters = req.getQueryParameters();

      // Set the content
      content = PoemResponseFactory.createGETResponse(queryParameters, poemName).getBytes();

      // Set the status
      res.setStatus(HttpResponse.SC_OK);

      // Set the content type
      res.setContentType(mimeTyper.getContentTypeFor(uri));

      // Put the data in the response
      res.setContent(content);

      // Transmit the response
      res.write(out);
    }
    catch (SecurityException se)
    {
      res.sendError(HttpResponse.SC_FORBIDDEN, out);
    }
    catch (IOException ioe)
    {
      res.sendError(HttpResponse.SC_NOT_FOUND, out);
    }
    catch (SAXException | ParserConfigurationException e)
    {
      res.sendError(HttpResponse.SC_INTERNAL_ERROR, out);
    }
  }

  @Override
  public void doPost(HttpRequest req, HttpResponse res)
  {
    byte[] content;
    MIMETyper mimeTyper;
    NameValueMap queryParameters;
    SecurityManager security;
    String uri;

    // Initialization
    mimeTyper = MIMETyper.createInstance();
    uri = "public_html/" + req.getRequestURI();

    // Get the SecurityManager
    security = System.getSecurityManager();

    try
    {
      // Check for read permission before doing anything
      if (security != null)
        security.checkRead(uri);

      // Get additional content
      req.readContent(in);

      // Post / Update poems.xml
      // TODO TODO TODO TODO
      // MUST UPDATE POEMS HERE

      // Get query parameters
      queryParameters = req.getQueryParameters();

      // Set the content
      content = PoemResponseFactory.createPOSTResponse(queryParameters, uri).getBytes();

      // Set the status
      res.setStatus(HttpResponse.SC_OK);

      // Set the content type
      res.setContentType(mimeTyper.getContentTypeFor(uri));

      // Put the data in the response
      res.setContent(content);

      // Transmit the response
      res.write(out);
    }
    catch (SecurityException se)
    {
      res.sendError(HttpResponse.SC_FORBIDDEN, out);
    }
    catch (IOException ioe)
    {
      res.sendError(HttpResponse.SC_NOT_FOUND, out);
    }

  }
}

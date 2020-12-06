package server;

import java.io.IOException;

public class ListServlet extends AbstractHttpServlet
{
  public ListServlet()
  {
    super();
  }

  public ListServlet(HttpInputStream in, HttpOutputStream out)
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
    System.out.println(uri);

    // Get the SecurityManager
    security = System.getSecurityManager();

    try
    {
      // Check for read permission before doing anything
      if (security != null)
        security.checkRead(uri);

      // Get query parameters
      queryParameters = req.getQueryParameters();

      // Set the content
      content = ListResponseFactory.createGETResponse(queryParameters).getBytes();

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

package http;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * The "default" servlet.
 * This servlet handles "normal" GET and POST requests.
 *
 * @author Prof. David Bernstein, James Madison University
 * @version 0.3
 */
public class DefaultHttpServlet extends AbstractHttpServlet
{
  public DefaultHttpServlet()
  {
    super();
  }

  public DefaultHttpServlet(HttpInputStream in, HttpOutputStream out)
  {
    super(in, out);
  }

  @Override
  public void doGet(HttpRequest req, HttpResponse res)
  {
    byte[] content;
    FileInputStream fis;
    int length;
    MIMETyper mimeTyper;
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

      // Create a stream for the file
      // and determine its length
      fis = new FileInputStream(uri);
      length = fis.available();

      // Read the file
      content = new byte[length];
      fis.read(content);
      fis.close();

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

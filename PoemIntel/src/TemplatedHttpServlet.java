import java.io.*;
import java.net.*;


/**
 * A servlet that inserts content into a template.
 *
 * @author  Prof. David Bernstein, James Madison University
 * @version 0.3
 */
public class TemplatedHttpServlet extends AbstractHttpServlet
{

    /**
     * Default Constructor
     */
    public TemplatedHttpServlet()
    {
       super();
    }

    /**
     * Explicit Value Constructor
     *
     * @param in    The HttpInputStream to read from
     * @param out   The HttpOutputStream to write to
     */
    public TemplatedHttpServlet(HttpInputStream in, HttpOutputStream out)
    {
       super(in, out);
    }
    

    /**
     * Handle a GET request
     *
     * @param req    The request to read from
     * @param res    The response to write to
     */
    protected void doGet(HttpRequest req, HttpResponse res)
    {
       byte[]             content;
       FileInputStream    fisBottom, fisContent, fisTop;
       int                length, lengthBottom, lengthContent, lengthTop;
       MIMETyper          mimeTyper;
       SecurityManager    security;       
       String             uri;


       // Initialization
       mimeTyper = MIMETyper.createInstance();
       uri = "../public_html"+req.getRequestURI();

       // Get the SecurityManager
       security = System.getSecurityManager();

       try 
       {
          // Check for read permission before doing anything
          if (security != null) security.checkRead(uri);

          // Create streams for the content
          // and the templates
          fisTop     = new FileInputStream("../public_html/top.tmpl");
          fisContent = new FileInputStream(uri);
          fisBottom  = new FileInputStream("../public_html/bottom.tmpl");
          lengthTop     = fisTop.available(); 
          lengthContent = fisContent.available();
          lengthBottom  = fisBottom.available();
          length = lengthTop + lengthContent + lengthBottom;
          
          // Set the status
          res.setStatus(HttpResponse.SC_OK);
          
          // Set the content type
          res.setContentType("text/html");
          
          // Read the files
          content = new byte[length];
          fisTop.read(content, 0, lengthTop);
          fisContent.read(content, lengthTop, lengthContent);
          fisBottom.read(content, lengthTop+lengthContent, lengthBottom);
          fisTop.close();
          fisContent.close();
          fisBottom.close();
          
          // Put the content in the response
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

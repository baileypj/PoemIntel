package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handle an HTTP 1.0 connection in a new thread of execution
 * This version:
 * Handles all requests using servlets
 *
 * @author Prof. David Bernstein, James Madison University
 * @version 0.3
 */
public class HttpConnectionHandler implements Runnable
{
  private HttpInputStream in;
  private HttpOutputStream out;
  private Logger logger;
  private Socket socket;

  /**
   * Explicit Value Constructor
   * (Starts the thread of execution)
   *
   * @param s The TCP socket for the connection
   */
  public HttpConnectionHandler(Socket s)
  {
    // Setup the logging system
    logger = Logger.getLogger("PoemIntelServer");

    socket = s;
  }

  /**
   * The entry point for the thread
   */
  @Override
  public void run()
  {
    HttpServlet servlet;
    HttpServletFactory factory;
    HttpRequest request;
    HttpResponse response;
    InputStream is;
    OutputStream os;

    try
    {
      // Get the I/O streams for the socket
      is = socket.getInputStream();
      os = socket.getOutputStream();

      in = new HttpInputStream(is);
      out = new HttpOutputStream(os);

      // Create an empty request and response
      request = new HttpRequest();
      response = new HttpResponse();

      try
      {
        // Read and parse the request information
        request.read(in);

        // Log the request for debugging purposes
        logger.log(Level.CONFIG, request.toString());

        // Process the request
        factory = HttpServletFactory.createFactory();
        servlet = factory.createServlet(request, in, out);
        servlet.service(request, response);
      }
      catch (Exception e)
      {
        e.printStackTrace();
        response.sendError(HttpResponse.SC_BAD_REQUEST, out);
      }
    }
    catch (IOException ioe)
    {
      // I/O problem so terminate the thread.
      // The server should close the socket.
    }
  }
}

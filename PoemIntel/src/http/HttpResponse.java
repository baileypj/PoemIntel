package http;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Iterator;

/**
 * An encapsulation of an HTTP response
 * This version adds:
 * Extends the abstract class HttpMessage
 *
 * @author Prof. David Bernstein, James Madison University
 * @version 0.2
 */
public class HttpResponse extends HttpMessage
{
  private int status;
  private NumberFormat nf;

  public static final int SC_ACCEPTED = 202;
  public static final int SC_BAD_GATEWAY = 502;
  public static final int SC_BAD_REQUEST = 400;
  public static final int SC_CREATED = 201;
  public static final int SC_FORBIDDEN = 403;
  public static final int SC_INTERNAL_ERROR = 500;
  public static final int SC_MOVED = 301;
  public static final int SC_NO_RESPONSE = 204;
  public static final int SC_NOT_FOUND = 404;
  public static final int SC_NOT_IMPLEMENTED = 501;
  public static final int SC_OK = 200;
  public static final int SC_PARTIAL_INFORMATION = 203;
  public static final int SC_PAYMENT_REQUIRED = 402;
  public static final int SC_SERVICE_OVERLOADED = 503;
  public static final int SC_UNAUTHORIZED = 401;

  /**
   * Default Constructor
   */
  public HttpResponse()
  {
    super();
    nf = NumberFormat.getIntegerInstance();
    status = SC_OK;
  }

  /**
   * Get the status associated with this HttpResponse
   *
   * @return The status
   */
  public int getStatus()
  {
    return status;
  }

  /**
   * Get the default message associated with a status code
   *
   * @param sc The status code
   * @return The associated default message
   */
  public static String getStatusMessage(int sc)
  {
    switch (sc)
    {
      case SC_ACCEPTED:
        return "Accepted";
      case SC_BAD_GATEWAY:
        return "Bad Gateway";
      case SC_BAD_REQUEST:
        return "Bad Request";
      case SC_CREATED:
        return "Created";
      case SC_FORBIDDEN:
        return "Forbidden";
      case SC_INTERNAL_ERROR:
        return "Internal Error";
      case SC_MOVED:
        return "Moved";
      case SC_NO_RESPONSE:
        return "No Response";
      case SC_NOT_FOUND:
        return "Not Found";
      case SC_NOT_IMPLEMENTED:
        return "Not Implemented";
      case SC_OK:
        return "OK";
      case SC_PARTIAL_INFORMATION:
        return "Partial Information";
      case SC_PAYMENT_REQUIRED:
        return "Payment Required";
      case SC_SERVICE_OVERLOADED:
        return "Service Overloaded";
      case SC_UNAUTHORIZED:
        return "Unauthorized";
      default:
        return "Unknown Status Code " + sc;
    }
  }

  /**
   * Read this HttpResponse (up to, but not including, the
   * content).
   * Note: The content is not read so that the content may
   * be read in a specialized way (e.g., as formatted binary data)
   * or in case the content is large (and should be "streamed").
   *
   * @param in The HttpInputStream to read from
   */
  @Override
  public void read(HttpInputStream in)
  {
    String line;
    String[] tokens;

    try
    {
      line = in.readHttpLine();
      tokens = line.split("\\s");
      try
      {
        setStatus(Integer.parseInt(tokens[1]));
      }
      catch (NumberFormatException nfe)
      {
        setStatus(-1);
      }
      headers.putPairs(in, ":");
    }
    catch (IOException ioe)
    {
      setStatus(SC_NO_RESPONSE);
    }
  }

  /**
   * Send an error response to the client.
   * After using this method, the response should be considered to
   * be committed and should not be written to.
   *
   * @param sc The status code
   * @param out The HttpOutputStream to write to
   */
  public void sendError(int sc, HttpOutputStream out)
  {
    String errorHTML;

    errorHTML =
        "<HTML><BODY><P>HTTP Error " + sc + " - " + getStatusMessage(sc) + "</P></BODY></HTML>\r\n";

    setStatus(sc);
    setContent(errorHTML.getBytes());

    try
    {
      write(out);
    }
    catch (IOException ioe)
    {
      // Nothing can be done
    }
  }

  /**
   * Sets the status code for this response
   *
   * @param sc The status code
   */
  public void setStatus(int sc)
  {
    status = sc;
  }

  /**
   * Returns a String representation of this Object
   *
   * @return The String representation
   */
  @Override
  public String toString()
  {
    Iterator<String> i;
    String name, s, value;

    s = "Status: \n\t" + status + "\n";
    s += "Headers:\n";
    i = getHeaderNames();
    while (i.hasNext())
    {
      name = i.next();
      value = headers.getValue(name);
      s += "\t" + name + "\t" + value + "\n";
    }

    return s;
  }

  /**
   * Write this HttpResponse
   *
   * @param out The HttpOutputStream to write to
   */
  public void write(HttpOutputStream out) throws IOException
  {
    if (content != null)
      setContentLength(content.length);
    else
      setContentLength(0);

    writeStatusLine(out);
    writeHeaders(out);

    if (content != null)
      out.write(content);
    out.flush();
    out.close();
  }

  /**
   * Write the headers to an output stream
   *
   * @param out The HttpOutputStream to write to
   */
  private void writeHeaders(HttpOutputStream out)
  {
    Iterator<String> i;
    String name, value;

    i = headers.getNames();
    while (i.hasNext())
    {
      name = i.next();
      value = headers.getValue(name);

      if ((value != null) && (!value.equals("")))
        out.printHeaderLine(name, value);
    }
    out.printEOL();
    out.flush();
  }

  /**
   * Write the status line to an output stream
   *
   * @param out The HttpOutputStream to write to
   */
  private void writeStatusLine(HttpOutputStream out)
  {
    out.print("HTTP/1.0");
    out.print(" ");

    nf.setMaximumIntegerDigits(3);
    nf.setMinimumIntegerDigits(3);
    out.print(nf.format(status));
    out.print(" ");

    out.print(getStatusMessage(status));
    out.printEOL();

    out.flush();
  }
}

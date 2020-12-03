package http;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

/**
 * An encapsulation of an HTTP request
 * This version:
 * Extends the abstract class HttpMessage
 *
 * @author Prof. David Bernstein, James Madison University
 * @version 0.2
 */
public class HttpRequest extends HttpMessage
{
  private NameValueMap queryParameters;
  private String method, queryString, version;
  private URI uri;

  /**
   * Default Constructor
   */
  public HttpRequest()
  {
    super();
    method = null;
    queryString = null;
    version = null;
  }

  /**
   * Returns the name of the HTTP method with which this request was made,
   * (for example, GET, POST, or PUT)
   *
   * @return The method
   */
  public String getMethod()
  {
    return method;
  }

  /**
   * Returns the part of this request's URI from the protocol name
   * up to the query string in the first line of the HTTP request
   *
   * @return The URI
   */
  public String getRequestURI()
  {
    String path;

    if (uri == null)
      path = null;
    else
      path = uri.getPath();
    if ((path == null) || path.equals(""))
      return "index.html";
    else
      return path;
  }

  /**
   * Read this HttpRequest (up to, but not including, the
   * content).
   * Note: The content is not read so that the content may
   * be read in a specialized way (e.g., as formatted binary data)
   * or in case the content is large (and should be "streamed").
   *
   * @param in The HttpInputStream to read from
   */
  @Override
  public void read(HttpInputStream in)
      throws IndexOutOfBoundsException, IOException, URISyntaxException
  {
    String line, request, token;
    String[] tokens;

    line = in.readHttpLine();

    tokens = line.split("\\s");
    method = tokens[0];
    request = tokens[1];
    if (tokens.length > 2)
      version = tokens[2];
    else
      version = "HTTP/0.9";

    // Parse the URI
    uri = new URI(request);

    // Get the decoded query string
    queryString = uri.getQuery();

    // Process the query string
    queryParameters = NameValueMap.createNameValueMap();
    if (queryString != null)
      queryParameters.putPairs(queryString, "&", "=");

    // Process the headers
    headers = NameValueMap.createNameValueMap();
    headers.putPairs(in, ":");

    // Get the content length
    token = headers.getValue("Content-Length");
    try
    {
      setContentLength(Integer.parseInt(token.trim()));
    }
    catch (Exception e)
    {
      setContentLength(-1);
    }
  }

  /**
   * Set the method
   */
  public void setMethod(String method)
  {
    this.method = method;
  }

  /**
   * Set the URI
   */
  public void setURI(URI uri)
  {
    this.uri = uri;
  }

  /**
   * Set the version
   */
  public void setVersion(String version)
  {
    this.version = version;
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

    s = "Method: \n\t" + getMethod() + "\n";
    s += "URI: \n\t" + getRequestURI() + "\n";

    s += "Parameters:\n" + queryString + "\n";

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
   * Write this HttpRequest
   *
   * @param out The HttpOutputStream to write to
   */
  public void write(HttpOutputStream out)
  {
    Iterator<String> i;
    String name, value;

    out.printHttpLine(method + " " + getRequestURI() + " HTTP/" + version);
    i = getHeaderNames();
    while (i.hasNext())
    {
      name = i.next();
      value = headers.getValue(name);
      out.printHeaderLine(name, value);
    }
    out.printEOL();
    try
    {
      if (content != null)
        out.write(content);
    }
    catch (IOException ioe)
    {
      // Couldn't write the content
    }
    out.flush();
  }

}

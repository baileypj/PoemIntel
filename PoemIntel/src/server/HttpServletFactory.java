package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;

/**
 * Constructs servlets appropriate based on
 * the file-type in the request.
 * Note that the Singleton pattern is used to construct
 * the factory itself.
 *
 * @version 0.3
 * @author Prof. David Bernstein, James Madison University
 */
public class HttpServletFactory
{
  // For the Singleton pattern
  private static HttpServletFactory instance = new HttpServletFactory();

  // Actual attributes
  private File file;
  private long lastModified;
  private NameValueMap associations;

  private static final String FILE_NAME = "associations.dat";
  private static final NameValueMap DEFAULT_ASSOCIATIONS = NameValueMap.createNameValueMap();

  /**
   * Default Constructor
   */
  private HttpServletFactory()
  {
    file = new File(FILE_NAME);
    lastModified = -1;
    associations = NameValueMap.createNameValueMap();
    loadAssociations();
  }

  /**
   * Create an HttpServletFactory
   */
  public static HttpServletFactory createFactory()
  {
    return instance;
  }

  /**
   * Construct an HttpServlet
   *
   * @param req The HTTP request
   * @param in The HttpInputStream to read from
   * @param out The HttpOutputStream to write to
   */
  public HttpServlet createServlet(HttpRequest req, HttpInputStream in, HttpOutputStream out)
      throws Exception
  {
    HttpServlet servlet;
    String className, ext, fname;

    loadAssociations();
    fname = req.getRequestURI();
    ext = FileTyper.getExtension(fname);
    className = associations.getValue(ext);

    servlet = null;

    // Get class of servlet from the associations.dat
    Class<?> servlet_class =
        Class.forName("server." + className);

    // Get constructor for class of servlet
    Constructor<?> servlet_constructor =
        servlet_class.getConstructor(new Class[] {HttpInputStream.class, HttpOutputStream.class});

    // Create Servlet
    servlet = (HttpServlet) servlet_constructor.newInstance(in, out);
    return servlet;
  }

  /**
   * Load the associations between file types and
   * servlets (if they have changed on disk)
   */
  private void loadAssociations()
  {
    BufferedReader in;
    long modified;

    modified = file.lastModified();
    if (modified > lastModified)
    {
      try
      {
        in = new BufferedReader(new FileReader(new File(FILE_NAME)));
        associations.clear();
        associations.putPairs(in, "\\s");
        lastModified = modified;
      }
      catch (Exception e)
      {
        associations = DEFAULT_ASSOCIATIONS;
      }
    }
  }
}

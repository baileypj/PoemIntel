import java.io.*;


/**
 * The requirements of HTTP servlets.
 *
 * Most servlets will extend the AbstractHttpServlet class rather
 * than implement this interface directly.  This interface exists
 * to allow for the creation of servlets that cannot take advantage
 * of the functionality in AbstractHttpServlet.
 *
 * @author  Prof. David Bernstein, James Madison University
 * @version 0.3
 */
public interface HttpServlet
{
    /**
     * Service an HTTP request.
     *
     * @param req    The request to read from
     * @param res    The response to write to
     */
    public abstract void service(HttpRequest req, HttpResponse res);
    


    /**
     * Set the streams for this HttpServlet
     *
     * @param in  The HttpInputStream to read from
     * @param out The HttpOutputStream to write to
     */
    public abstract void setStreams(HttpInputStream in, HttpOutputStream out);
}

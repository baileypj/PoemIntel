import java.io.*;


/**
 * An abstract class that should be subclassed to create 
 * an HTTP servlet.
 *
 * A subclass of HttpServlet must override at least one 
 * of the following: doGet(), doPut()
 *
 * @author  Prof. David Bernstein, James Madison University
 * @version 0.3
 */
public abstract class AbstractHttpServlet implements HttpServlet
{
    protected HttpInputStream         in;
    protected HttpOutputStream        out;

    /**
     * Default Constructor
     */
    public AbstractHttpServlet()
    {
       in = null;
       out = null;
    }

    /**
     * Explicit Value Constructor
     */
    public AbstractHttpServlet(HttpInputStream in, HttpOutputStream out)
    {
       setStreams(in, out);
    }

    /**
     * Called by the server (via the service method) to 
     * allow a servlet to handle a GET request
     *
     * @param req    The request to read from
     * @param res    The response to write to
     */
    protected void doGet(HttpRequest req, HttpResponse res)
    {
	res.sendError(HttpResponse.SC_FORBIDDEN, out);
    }



    /**
     * Called by the server (via the service method) to 
     * allow a servlet to handle a POST request
     *
     * @param req    The request to read from
     * @param res    The response to write to
     */
    protected void doPost(HttpRequest req, HttpResponse res)
    {
	res.sendError(HttpResponse.SC_FORBIDDEN, out);
    }


    /**
     * Dispatches HTTP requests to the doXXX methods. There's no need to 
     * override this method.
     *
     * @param req    The request to read from
     * @param res    The response to write to
     */
    public void service(HttpRequest req, HttpResponse res)
    {
	String      method;

	method = null;
	if (req != null) method   = req.getMethod().toUpperCase();


	if      ((req == null) || (method == null))
                            res.sendError(HttpResponse.SC_BAD_REQUEST, out);
	else if (method.equals("GET"))  doGet(req, res);
	else if (method.equals("POST")) doPost(req, res);
	else res.sendError(HttpResponse.SC_NOT_IMPLEMENTED, out);
    }

    /**
     * Set the streams for this HttpServlet
     *
     * @param in  The HttpInputStream to read from
     * @param out The HttpOutputStream to write to
     */
    public void setStreams(HttpInputStream in, HttpOutputStream out)
    {
       this.in  = in;
       this.out = out;       
    }
}

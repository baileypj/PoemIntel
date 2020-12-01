import java.io.*;

/**
 * A PrintStream that can be used for writing HTTP requests
 * and responses
 *
 * @author  Prof. David Bernstein, James Madison University
 * @version 0.0
 */
public class HttpOutputStream extends PrintStream
{
    /**
     * Explicit Value Constructor
     *
     * @param os  The underlying OutputStream to use
     */
    public HttpOutputStream(OutputStream os)
    {
       super(os, false);
    }

    /**
     * Print an End-Of-Line marker
     */
    public void printEOL()
    {
       print("\r\n");
    }

    /**
     * Print a line (using the HTTP End-Of-Line marker)
     *
     * @param line  The line to print
     */
    public void printHttpLine(String line)
    {
       print(line);       
       print("\r\n");
    }

    /**
     * Print an appropriately formatted and terminated
     * header line
     *
     * @param name   The name
     * @param value  The corresponding value
     */
    public void printHeaderLine(String name, String value)
    {
       print(name);
       print(":");
       print(value);
       printEOL();
    }

}

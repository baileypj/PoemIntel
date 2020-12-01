import java.io.*;
import java.net.FileNameMap;



/**
 * A utility class for working with MIME types
 *
 * Note: This class makes use of the Singleton Pattern since there
 * is never need for more than one MIMETyper.  The MIMETyper class is
 * thread-safe,
 *
 * @author  Prof. David Bernstein, James Madison University
 * @version 1.0
 */
public class MIMETyper implements FileNameMap
{
    private NameValueMap         types;


    private static MIMETyper     instance = new MIMETyper();
    private static final String  DEFAULT = "application/octet-stream";

    /**
     * Default Constructor
     */
    private MIMETyper()
    {
       types = NameValueMap.createConcurrentNameValueMap();
       initializeTypes();
    }



    /**
     * Create an instance of a MIMETyper if necessary.
     * Otherwise, return the existing instance.
     *
     * @return   The instance
     */
    public static MIMETyper createInstance()
    {
       return instance;
    }



    /**
     * Guess the MIME type from a file extension
     *
     * @param  ext   The extension (e.g., ".gif")
     * @return       The MIME type (e.g., "image/gif")
     */
    public String getContentTypeForExtension(String ext)
    {
       String    type;

       type = types.getValue(ext.toLowerCase());
       if (type == null) type = DEFAULT;

       return type;
    }




    /**
     * Guess the MIME type from a file name
     * (possibly including a path)
     *
     * @param  name  The name (e.g., "/pictures/dome.gif")
     * @return       The MIME type (e.g., "image/gif")
     */
    public String getContentTypeFor(String name)
    {
       String    ext;

       ext = FileTyper.getExtension(name);

       return getContentTypeForExtension(ext);
    }




    /**
     * Initialize the types table
     */
    private void initializeTypes()
    {
       BufferedReader       in;
       String               line;       

       try
       {
          in = new BufferedReader(new FileReader("mimetypes.dat"));
          types.putPairs(in, "\t");
       }
       catch (IOException ioe)
       {
          types.put(".htm","text/html");
          types.put(".html","text/html");
          types.put(".text","text/plain");
       }
    }
}

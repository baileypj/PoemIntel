import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * A mapping of name=value pairs.
 *
 * @author  Prof. David Bernstein, James Madison University
 * @version 1.0
 */
public class NameValueMap
{
    private Map<String,String>       delegate;
    

    /**
     * Explicit Value Constructor
     *
     * @param delegate    The Map to delegate to
     */
    private NameValueMap(Map<String,String> delegate)
    {
       this.delegate = delegate;
    }

    /**
     * Construct a NameValueMap (that is not thread safe)
     */
    public static NameValueMap createNameValueMap()
    {
       return new NameValueMap(new HashMap<String,String>());
    }


    /**
     * Construct a thread-safe NameValueMap
     */
    public static NameValueMap createConcurrentNameValueMap()
    {
       return new NameValueMap(new ConcurrentHashMap<String,String>());
    }

    /**
     * Remove all name=value pairs from this map.
     */
    public void clear()
    {
       try
       {
          delegate.clear();
       }
       catch (UnsupportedOperationException uoe)
       {
          // Couldn't clear
          //
          // Note: We could iterate through each element and remove()
          // it but this could cause a concurrent modification problem.
          // So, this would only work if the iterator supported the
          // remove() operation.
       }
    }


    /**
     * Get all of the names
     *
     * @return All of the names in this Map
     */
    public Iterator<String> getNames()
    {
       return delegate.keySet().iterator();       
    }

    /**
     * Get the value for a particular name
     *
     * @param  name   The name of interest
     * @return        The corresponding value (or null)
     */
    public String getValue(String name)
    {
       return delegate.get(name);       
    }


    /**
     * Add a name=value pair to this map.
     *
     * @param name   The name
     * @param value  The corresponding value
     */
    public void put(String name, String value)
    {
       delegate.put(name, value);
    }
    


    /**
     * Add a name=value pair to this map.
     *
     * Note: Only the first occurrence of the delimiter is significant.
     * The delimiter may appear in the value.
     *
     * @param pair   The String containing the name=value pair
     * @param regex  The delimiter between the name and value
     */
    public void putPair(String pair, String regex)
    {
       String   value;       
       String[] components;
       
       components = pair.split(regex);
       if (components.length == 2) delegate.put(components[0], components[1]);
       if (components.length >  2)
       {
          value = "";          
          for (int i=1; i<components.length; i++) value += components[i];
          delegate.put(components[0], value);
       }
    }
    

    /**
     * Add a name=value pair to this map (assuming the delimiter is
     * the '=' character).
     *
     * @param pair   The String containing the name=value pair
     */
    public void putPair(String pair)
    {
       putPair(pair, "=");       
    }
    

    /**
     * Add one or more name=value pairs to this map.
     *
     * @param pairs      The BufferedReader containing the lines of pairs
     * @param regexLine  The delimiter between the different pairs
     * @param regexPair  The delimiter between the name and value in each pair
     */
    public void putPairs(String pairs, String regexLine, String regexPair)
    {
       String[] components, lines;
       
       lines = pairs.split(regexLine);
       for (int i=0; i<lines.length; i++)
       {
          putPair(lines[i], regexPair);
       }
    }
    
    

    /**
     * Add one or more name=value pairs to this map.
     *
     * This method assumes that the pairs are delimited by "&" and the
     * names and values are delimited by "=".
     *
     * @param pairs      The BufferedReader containing the lines of pairs
     */
    public void putPairs(String pairs)
    {
       putPairs(pairs, "&", "=");       
    }
    

    /**
     * Add one or more name=value pairs to this map.
     *
     * This method reads from the BufferedReader until either an
     * end-of-stream is encountered or a line contains the String "".
     *
     * In the event of an IOException, this method will return (but
     * will not remove any pairs that might have been added).
     *
     * @param in     The BufferedReader containing the lines of pairs
     * @param regex  The delimiter between name and value in each pair
     */
    public void putPairs(BufferedReader in, String regex)
    {
       String      line;
       
       try
       {
          while (((line=in.readLine()) != null) && !line.equals(""))
          {
             putPair(line, regex);
          }
       }
       catch (IOException ioe)
       {
       }
    }
    

    /**
     * Add one or more name=value pairs to this map.
     *
     * This method reads from the BufferedReader until either an
     * end-of-stream is encountered or a line contains the String "".
     *
     * In the event of an IOException, this method will return (but
     * will not remove any pairs that might have been added).
     *
     * @param in     The HttpInputStream containing the lines of pairs
     * @param regex  The delimiter between name and value in each pair
     */
    public void putPairs(HttpInputStream in, String regex)
    {
       String      line;
       
       try
       {
          while (((line=in.readHttpLine()) != null) && !line.equals(""))
          {
             putPair(line, regex);
          }
       }
       catch (IOException ioe)
       {
       }
    }
    
}

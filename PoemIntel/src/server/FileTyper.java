package server;

/**
 * A utility class for working with file types (i.e., extensions)
 *
 * @author Prof. David Bernstein, James Madison University
 * @version 1.0
 */
public class FileTyper
{

  /**
   * Returns the extension/file-type
   * Note: This method returns the name if it does
   * not contain a '.' character
   *
   * @param name The name (e.g., "/pictures/dome.gif")
   * @return The extension (e.g., ".gif")
   */
  public static String getExtension(String name)
  {
    int lastSlash, firstPeriod, queryStart;
    String ext;

    lastSlash = name.lastIndexOf('/');
    lastSlash = Math.max(lastSlash, name.lastIndexOf('\\'));
    if (lastSlash < 0)
      lastSlash = 0;

    firstPeriod = name.indexOf('.', lastSlash);
    if (firstPeriod < 0)
      firstPeriod = 0;

    queryStart = name.indexOf('?', firstPeriod);
    if (queryStart < 0)
      queryStart = name.length();

    ext = name.substring(firstPeriod, queryStart);

    return ext;
  }

  /**
   * Return the file name (no extension)
   * @param name The name
   * @return The file name (no extension)
   */
  public static String getFileName(String name)
  {
    return getFileName(name, false);
  }

  /**
   * Returns the file name
   *
   * @param name The name
   * @param ext Include extension?
   * @return The filename (and extension if ext)
   */
  public static String getFileName(String name, boolean ext)
  {
    int lastSlash, firstPeriod;

    lastSlash = name.lastIndexOf('/');
    lastSlash = Math.max(lastSlash, name.lastIndexOf('\\'));
    if (lastSlash < 0)
      lastSlash = 0;

    if (ext)
    {
      // End of string
      firstPeriod = name.length();
    }
    else
    {
      firstPeriod = name.indexOf('.', lastSlash);
      if (firstPeriod < 0)
        firstPeriod = 0;
    }

    return name.substring(lastSlash, firstPeriod);
  }

}

/**
 * A utility class for working with file types (i.e., extensions)
 *
 * @author  Prof. David Bernstein, James Madison University
 * @version 1.0
 */
public class FileTyper
{

    /**
     * Returns the extension/file-type
     *
     * Note: This method returns the name if it does
     *       not contain a '.' character
     *
     * @param  name  The name (e.g., "/pictures/dome.gif")
     * @return       The extension (e.g., ".gif")
     */
    public static String getExtension(String name)
    {
	int       lastSlash, firstPeriod;
	String    ext, type;

	lastSlash = name.lastIndexOf('/');
	lastSlash = Math.max(lastSlash, name.lastIndexOf('\\'));
	if (lastSlash < 0) lastSlash = 0;

	firstPeriod = name.indexOf('.',lastSlash);
	if (firstPeriod < 0) firstPeriod = 0;

	ext = name.substring(firstPeriod);

	return ext;
    }

}

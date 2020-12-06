package server;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * An input stream for reading both byte-stream data and
 * character-stream that is terminated using the HTTP end-of-line
 * characters ('\r\n')
 *
 * @author Prof. David Bernstein, James Madison University
 * @version 0.0
 */
public class HttpInputStream extends DataInputStream
{
  private static final int INITIAL_BUFFER_SIZE = 256;
  public static final byte CR = (byte) '\r'; // 13;
  public static final byte LF = (byte) '\n'; // 10;
  public static final byte NULL = (byte) '\0'; // 0;

  private byte[] buffer;
  private int currentSize;

  /**
   * Explicit Value Constructor
   *
   * @param is The underlying InputStream
   */
  public HttpInputStream(InputStream is)
  {
    super(is);
  }

  /**
   * Add a byte to the current line buffer (increasing the
   * size of the line buffer if necessary)
   *
   * @Param b The byte to add
   */
  private void addToLineBuffer(byte b)
  {
    byte[] temp;

    buffer[currentSize] = b;
    currentSize++;

    if (currentSize == buffer.length)
    {
      temp = new byte[buffer.length * 2];
      System.arraycopy(buffer, 0, temp, 0, buffer.length);
      buffer = temp;
    }
  }

  /**
   * Reads the input stream, one line at a time.
   * Lines are terminated either by '\r\n' or by end-of-stream.
   *
   * @return A String containing the line
   */
  public String readHttpLine() throws IOException
  {
    byte previous;
    byte[] b;
    int status, stringLength;
    String result;

    // This method will read a byte at a time. It would be much
    // more efficient to read multiple bytes at a time into a buffer
    // but this is much simpler.
    b = new byte[1];

    buffer = new byte[INITIAL_BUFFER_SIZE];
    currentSize = 0;

    status = -1;
    previous = NULL;

    while (status < 0)
    {
      try
      {
        readFully(b); // Blocks until a byte is available
        if ((b[0] == LF) && (previous == CR))
        {
          status = 1;
        }
        else
        {
          previous = b[0];
          addToLineBuffer(b[0]);
        }
      }
      catch (EOFException eofe)
      {
        status = 0;
      }
    }

    if (status == 0)
      stringLength = currentSize; // Terminated by EOS
    else
      stringLength = currentSize - 1; // Remove the CR

    try
    {
      // Use the HTTP "standard" character set
      result = new String(buffer, 0, stringLength, "ISO-8859-1");
    }
    catch (UnsupportedEncodingException uee)
    {
      // Use the default character set
      result = new String(buffer, 0, stringLength);
    }

    return result;
  }
}

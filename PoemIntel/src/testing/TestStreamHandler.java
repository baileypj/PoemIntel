package testing;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

public class TestStreamHandler extends StreamHandler
{
  private OutputStream out;

  @Override
  public void setOutputStream(OutputStream out)
  {
    this.out = out;
  }

  @Override
  public void publish(LogRecord record)
  {
    try
    {
      out.write((record.getLevel() + ":\n" + record.getMessage() + "\n").getBytes());
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    this.flush();
  }

}

package testing;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import server.HttpServer;

public class PoemGETTest
{
  Logger logger;

  @BeforeEach
  public void setupLogger()
  {
    logger = Logger.getLogger("PoemIntelServer");

    TestStreamHandler handler = new TestStreamHandler();
    handler.setOutputStream(System.out);
    handler.setLevel(Level.ALL);
    logger.addHandler(handler);
  }

  @Test
  public void poemGetTest() throws Exception
  {
    HttpServer server = new HttpServer();
    server.start();
    Thread.sleep(1000);

    // Do something here

    Thread.sleep(1000);
    server.stop();
  }
}

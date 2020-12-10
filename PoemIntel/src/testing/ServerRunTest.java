package testing;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import server.HttpServer;

public class ServerRunTest
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
  public void serverRunTest() throws Exception
  {
    HttpServer server = new HttpServer();
    server.start();

    // Some examples to try to run in the browser
    // 127.0.0.1:8080/test.poem
    // 127.0.0.1:8080/test.poem?type=html
    // 127.0.0.1:8080/test.poem?type=text
    // 127.0.0.1:8080/test.asdf

    // 127.0.0.1:8080/test.list

    // 127.0.0.1:8080/test.xsl

    // Just run for 20 seconds
    Thread.sleep(120000);

    server.stop();
  }
}

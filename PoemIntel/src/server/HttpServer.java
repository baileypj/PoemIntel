package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simplified HTTP server
 * Note: This version only supports GET requests
 *
 * @author Prof. David Bernstein, James Madison University
 * @version 3.0
 */
public class HttpServer implements Runnable
{
  protected static final String serverAddress = "127.0.0.1";
  protected static final int serverPort = 8080;
  protected static final String loggerName = "PoemIntelServer";
  protected static final String poemDatabase = "poems.xml";

  private volatile boolean keepRunning;
  private final ExecutorService threadPool;
  private final ServerSocket serverSocket;
  private Thread controlThread;

  private static Logger logger;
  private static final int MAX_THREADS = 100; // Should be tuned

  /**
   * The entry point of the application
   *
   * @param args The command line arguments
   */
  public static void main(String[] args)
  {
    BufferedReader in;
    HttpServer server;

    // Setup the logging system
    logger = Logger.getLogger(loggerName);
    try
    {
      logger.setLevel(Level.parse(args[0]));
      logger.setUseParentHandlers(false);
    }
    catch (Exception e)
    {
      // The FileHandler couldn't be constructed or the Level was bad
      // so use the default ConsoleHandler (at the default Level.INFO)
      logger.setUseParentHandlers(true);
    }

    server = null;
    try
    {
      in = new BufferedReader(new InputStreamReader(System.in));

      // Construct and start the server
      server = new HttpServer();
      server.start();

      System.out.println("Press [Enter] to stop the server...");

      // Block until the user presses [Enter]
      in.readLine();
    }
    catch (IOException ioe)
    {
      System.out.println("Stopping because of an IOException");
    }

    // Stop the server
    if (server != null)
      server.stop();
  }

  /**
   * Default COnstructor
   */
  public HttpServer() throws IOException
  {
    serverSocket = new ServerSocket(serverPort);

    // Setup the logging system
    logger = Logger.getLogger(loggerName);
    try
    {
      logger.setLevel(Level.FINE);
      logger.setUseParentHandlers(false);
    }
    catch (Exception e)
    {
      // The FileHandler couldn't be constructed or the Level was bad
      // so use the default ConsoleHandler (at the default Level.INFO)
      logger.setUseParentHandlers(true);
    }

    logger.log(Level.INFO, "Created Server Socket on " + serverPort);

    threadPool = Executors.newFixedThreadPool(MAX_THREADS);

    serverSocket.setSoTimeout(5000);
  }

  /**
   * The code to run in the server's thread of execution
   */
  @Override
  public void run()
  {
    HttpConnectionHandler connection;
    Socket s;

    while (keepRunning)
    {
      try
      {
        s = serverSocket.accept();
        logger.log(Level.INFO, "Accepted a connection");
        connection = new HttpConnectionHandler(s);

        // Add the connection to a BlockingQueue<Runnable> object
        // and, ultimately, call it's run() method in a thread
        // in the pool
        threadPool.execute(connection);
      }
      catch (SocketTimeoutException ste)
      {
        // The accept() method timed out. Check to see if
        // the thread should keep running or not.
      }
      catch (IOException ioe)
      {
        // Problem with accept()
      }
    }

    stopPool();
    controlThread = null;
  }

  /**
   * Stop the threads in the pool
   */
  private void stopPool()
  {
    // Prevent new Runnable objects from being submitted
    threadPool.shutdown();

    try
    {
      // Wait for existing connections to complete
      if (!threadPool.awaitTermination(5, TimeUnit.SECONDS))
      {
        // Stop executing threads
        threadPool.shutdownNow();

        // Wait again
        if (!threadPool.awaitTermination(5, TimeUnit.SECONDS))
        {
          logger.log(Level.INFO, "Could not stop thread pool");
        }
      }
    }
    catch (InterruptedException ie)
    {
      // Stop executing threads
      threadPool.shutdownNow();

      // Propagate the interrupt status
      controlThread.interrupt();
    }
  }

  /**
   * Start the thread of execution
   */
  public void start()
  {
    if (controlThread == null)
    {
      controlThread = new Thread(this);
      keepRunning = true;

      controlThread.start();
    }
  }

  /**
   * Stop the thread of execution (after it finishes the
   * current connection)
   */
  public void stop()
  {
    keepRunning = false;
  }
}

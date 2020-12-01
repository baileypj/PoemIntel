import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.logging.*;


/**
 * A simplified HTTP server
 *
 * Note: This version only supports GET requests
 *
 * @author  Prof. David Bernstein, James Madison University
 * @version 3.0
 */
public class HttpServer implements Runnable
{
    private volatile boolean            keepRunning;
    private final    ExecutorService    threadPool;    
    private final    ServerSocket       serverSocket;
    private          Thread             controlThread;

    private static   Logger             logger;    
    private static   final int          MAX_THREADS = 100; // Should be tuned

    /**
     * The entry point of the application
     *
     * @param args    The command line arguments
     */
    public static void main(String[] args)
    {
       BufferedReader        in;       
       HttpServer            server;
       Handler               logHandler;       

       // Setup the logging system
       logger     = Logger.getLogger("edu.jmu.cs");
       try
       {
          logHandler = new FileHandler("log.txt");
          logHandler.setFormatter(new SimpleFormatter());
          logger.addHandler(logHandler);          
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
          System.out.println("  Stopping because of an IOException");
       }

       // Stop the server
       if (server != null) server.stop();
    }



    /**
     * Default COnstructor
     */
    public HttpServer() throws IOException
    {
       serverSocket = new ServerSocket(8080);
       logger.log(Level.INFO, "Created Server Socket on 8080");

       threadPool   = Executors.newFixedThreadPool(MAX_THREADS);

       serverSocket.setSoTimeout(5000);          
    }
    

    /**
     * The code to run in the server's thread of execution
     */
    public void run()
    {
       HttpConnectionHandler  connection;
       Socket                 s;

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
             // The accept() method timed out.  Check to see if
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
                logger.log(Level.INFO, "Could not stop thread pool.");
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

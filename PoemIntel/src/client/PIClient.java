package client;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import server.HttpOutputStream;
import server.HttpRequest;

/**
 * This is a basic PIClient. It will connect to the localHost IP on port 8080
 * every time. It will request a poem XML file from the server by name.
 * The file will be saved in this directory.
 *
 * This work complies with the JMU Honor Code.
 *
 * @author Robert Verdisco
 *
 */
public class PIClient {

	public static void main(String[] args) {
        String poemName;
        Socket s;

        poemName = args[0];

        try {
            s = new Socket(InetAddress.getLocalHost(), 8080);

            FileOutputStream fs = new FileOutputStream(poemName);
            OutputStream os = s.getOutputStream();
            HttpOutputStream out = new HttpOutputStream(os);
            HttpRequest req = new HttpRequest();
            byte[] b;


            req.setMethod("GET");
            URI uri = new URI(poemName);
            req.setURI(uri);
            req.write(out);

//            InputStream is = s.getInputStream();
//            b = is.readAllBytes();
//            s.close();
//            fs.write(b);
//            fs.close();




        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
	}

}

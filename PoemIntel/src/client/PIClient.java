package client;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import poem.Poem;
import poem.PubInfo;
import server.HttpInputStream;
import server.HttpOutputStream;
import server.HttpRequest;
import server.HttpResponse;
import server.PoemHandler;

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
        /*String poemName;
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

            InputStream is = s.getInputStream();
            b = is.readAllBytes();
            s.close();
            fs.write(b);
            fs.close();




        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }*/
		
		String command = "";
		BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
		Socket s;
		OutputStream os;
		HttpOutputStream out;
		InputStream is;
		HttpInputStream in;
		
		try {
			System.out.println("Welcome to the PoemIntel client!");
			
			//Main client command loop
			while(!command.equals("quit"))
			{
				//List commands on screen
				System.out.println("\nEnter one of the following commands:");
				System.out.println("'upload'\tupload existing poem to server");
				System.out.println("'list'\tget a list of poems from server");
				System.out.println("'get poem'\trequest specific poem from server");
				System.out.println("'quit'\texit PoemIntel client\n");
				
				//Read command
				command = userIn.readLine();
				
				//check command
				switch(command)
				{
					case "quit":
						System.out.println("Exiting PoemIntel client");
						break;
					case "upload":
						System.out.println("Upload not yet implemented");
						break;
					case "list":
						System.out.println("List not yet implemented");
						break;
					case "get poem":
						//Connect to server
						s = new Socket(InetAddress.getLocalHost(), 8080);
						os = s.getOutputStream();
						out = new HttpOutputStream(os);
						is = s.getInputStream();
						in = new HttpInputStream(is);
						
						//User enters poem name
						System.out.println("Enter name of poem to get:");
						String poemName = userIn.readLine();
			            
						//Request poem from server
			            HttpRequest req = new HttpRequest();
			            req.setMethod("GET");
			            URI uri = new URI(poemName.replaceAll(" ", "_") + ".poem");
			            req.setURI(uri);
			            req.write(out);

			            //Receive response from server
			            HttpResponse res = new HttpResponse();
			            res.read(in);
			            res.readContent(in);
			            byte[] file_content = res.getContent();
			            
			            //Check is request was successful
			            if(res.getStatus() == 200)
			            {
			            	//Parse the poems file
				            ByteArrayInputStream bais = new ByteArrayInputStream(file_content);
				            InputSource inputSource = new InputSource(bais);
				            SAXParserFactory factory = SAXParserFactory.newInstance();
				            SAXParser parser = factory.newSAXParser();
				            PoemHandler handler = new PoemHandler(poemName);
				            parser.parse(inputSource, handler);
				            
	
				            //Get the poem
				            Poem poem = handler.getPoem();
				            PubInfo poem_info = poem.getPubInfo();
				            System.out.printf("\nTitle: %s\nAuthor: %s\nYear: %s\n%s\n", poem_info.getTitle(), poem_info.getAuthor(), poem_info.getYear(), poem.getBody());
			            } 
			            else
			            {
			            	System.out.printf("\nError:\n%s", new String(file_content, "US-ASCII"));
			            }
			            
			            s.close();
			            break;
					default:
						System.out.println("Invalid command");
				}
				
			}
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
	}

}

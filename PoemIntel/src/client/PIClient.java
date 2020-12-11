package client;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
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
 * The PIClient will give the user a number of options 
 * for sending and receiving poem data from the server.
 *
 * This work complies with the JMU Honor Code.
 *
 * @author Robert Verdisco
 *
 */
public class PIClient {

	public static void main(String[] args) {
		String command = "";
		BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
		Socket s;
		OutputStream os;
		HttpOutputStream out;
		InputStream is;
		HttpInputStream in;
		HttpRequest req;
		URI uri;
		HttpResponse res;
		byte[] file_content;
		Poem poem;
		String uriText;
		
		try {
			System.out.println("Welcome to the PoemIntel client!");
			
			//Main client command loop
			while(!command.equals("quit"))
			{
				//List commands on screen
				System.out.println("\nEnter one of the following commands:");
				System.out.println("'upload'\tupload new poem to server");
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
						String newOrOld = "";
						byte[] poemXML = new byte[0];
						String title = "";
						
						//Ask if user wants to upload from clientPoems folder
						System.out.println("Would you like to uplaod a prexisting poem file or create a new one?");
						System.out.println("Enter 'new' for a new file or 'old' for a prexisting file");
						newOrOld = userIn.readLine();
						
						//Make sure user entered valid response
						while((!newOrOld.equals("new")) && (!newOrOld.equals("old")))
						{
							System.out.println("please enter new or old");
							newOrOld = userIn.readLine();
						}
						
						if(newOrOld.equals("new"))
						{
							//get poem publication info
							System.out.print("Enter the poem title: ");
							title = userIn.readLine();
							System.out.print("Enter the poem author name: ");
							String author = userIn.readLine();
							System.out.print("Enter the poem year: ");
							String year = userIn.readLine();
							
							
							System.out.println("\nYou will now enter each line of the poem");
							System.out.println("Type the contents of a line and press enter to proceed to the next line");
							System.out.println("Type '/end' on its own line to indicate the end of the poem\n");
							
							//Poem body loop
							String body = "";
							String line = userIn.readLine();
							while(!line.equals("/end"))
							{
								body = body + "\n" + line;
								line = userIn.readLine();
							}
							
							//construct the poem
							poem = new Poem(new PubInfo(title, author, year), body);
							
							poemXML = poem.getPoemAsXML().getBytes();
						}
						else if(newOrOld.equals("old"))
						{
							System.out.println("Enter the poem title");
							title = userIn.readLine();
							System.out.println("Enter the filename for an xml poem in the clientPoems folder");
							String file = userIn.readLine();
							FileInputStream fis = new FileInputStream(file);
							poemXML = fis.readAllBytes();
							fis.close();
						}
						
						//Connect to server
						s = new Socket(InetAddress.getLocalHost(), 8080);
						os = s.getOutputStream();
						out = new HttpOutputStream(os);
						is = s.getInputStream();
						in = new HttpInputStream(is);
						
						//Send poem to server
			            req = new HttpRequest();
			            req.setMethod("POST");
			            uriText = "/" + title.replaceAll(" ", "%20") + ".poem";
			            uri = new URI(URLEncoder.encode(uriText, "utf-8"));
			            req.setURI(uri);
			            req.setContent(poemXML);
			            req.write(out);
			            
			            //Receive response from server
			            res = new HttpResponse();
			            res.read(in);
			            res.readContent(in);
			            file_content = res.getContent();
						
			            //Check is request was successful
			            if(res.getStatus() == 200)
			            {
				            System.out.print("\nPoem saved on server\n");
			            } 
			            else
			            {
			            	System.out.printf("\nError:\n%s", new String(file_content, "US-ASCII"));
			            }
			            
			            s.close(); 
						break;
					case "list":
						String useFilters = "";
						uriText = "poem.list?type=text";
						
						//Ask if user wants to use filters
						System.out.println("Would you like to apply any filters to the poem list? (y/n)");
						useFilters = userIn.readLine();
						
						//Make sure user entered valid response
						while((!useFilters.equals("y")) && (!useFilters.equals("n")))
						{
							System.out.println("please enter 'y' for yes and 'n' for no");
							useFilters = userIn.readLine();
						}
						
						//Check user response
						if(useFilters.equals("y"))
						{
							String userInput;
							
							System.out.println("Enter a value for each filter. Press enter without typing a value to skip filter");
							System.out.print("Author:");
							userInput = userIn.readLine();
							if(!userInput.equals(""))
							{
								uriText = uriText + "&" + "author=" + userInput;
							}
							
							System.out.print("Year:");
							userInput = userIn.readLine();
							if(!userInput.equals(""))
							{
								uriText = uriText + "&" + "year=" + userInput;
							}
						}
						
						//Connect to server
						s = new Socket(InetAddress.getLocalHost(), 8080);
						os = s.getOutputStream();
						out = new HttpOutputStream(os);
						is = s.getInputStream();
						in = new HttpInputStream(is);
						
						//Request list from server
			            req = new HttpRequest();
			            req.setMethod("GET");
			            uriText = "/" + uriText.replaceAll(" ", "%20");
			            uri = new URI(URLEncoder.encode(uriText, "utf-8"));
			            req.setURI(uri);
			            req.write(out);
			            
			            //Receive response from server
			            res = new HttpResponse();
			            res.read(in);
			            res.readContent(in);
			            file_content = res.getContent();
			            
			            //Check is request was successful
			            if(res.getStatus() == 200)
			            {
							/*
							 * //Transform File StreamSource program = new StreamSource(new
							 * File("public_html/listtext")); ByteArrayOutputStream bos = new
							 * ByteArrayOutputStream(65536); StreamResult result = new StreamResult(bos);
							 * ByteArrayInputStream bis = new ByteArrayInputStream(file_content);
							 * StreamSource source = new StreamSource(bis); TransformerFactory factory =
							 * TransformerFactory.newInstance(); Transformer transformer =
							 * factory.newTransformer(program); transformer.transform(source, result);
							 * 
							 * System.out.println(bos.toString());
							 */
			            	
				            System.out.printf("\nPoem List:\n%s", new String(file_content, "US-ASCII"));
			            } 
			            else
			            {
			            	System.out.printf("\nError:\n%s", new String(file_content, "US-ASCII"));
			            }
			            
			            s.close();
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
			            req = new HttpRequest();
			            req.setMethod("GET");
			            uriText = "/" + poemName.replaceAll(" ", "+") + ".poem";
			            uri = new URI(URLEncoder.encode(uriText, "utf-8"));
			            req.setURI(uri);
			            req.write(out);

			            //Receive response from server
			            res = new HttpResponse();
			            res.read(in);
			            res.readContent(in);
			            file_content = res.getContent();
			            
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
				            poem = handler.getPoem();
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

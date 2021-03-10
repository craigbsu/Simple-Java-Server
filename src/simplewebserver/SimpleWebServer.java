/****************************************************************
SimpleWebServer.java
This toy web server is used to illustrate security vulnerabilities. This web server only supports extremely simple HTTP GET requests.
****************************************************************/

import java.io.*;
import java.net.*;
import java.util.*;

public class SimpleWebServer {

    /* Run the HTTP server on this TCP port. */
    private static final int PORT = 8080;

    /* The socket used to process incoming connections from web clients */
    private static ServerSocket dServerSocket;

    public SimpleWebServer () throws Exception {
    	dServerSocket = new ServerSocket (PORT);
    }

    public void run() throws Exception {
        int i = 1;
		while (true) {
    		/* wait for a connection from a client */
    		Socket s = dServerSocket.accept();
			System.out.println("Connection processRequest #" + i);

    		/* then process the client's request */
    		processRequest(s);
			i++;
    	}
    }

    /* Reads the HTTP request from the client, and responds with the file the user requested or a HTTP error code. */
    public void processRequest(Socket s) throws Exception {
    	/* used to read data from the client */
    	BufferedReader br = new BufferedReader (new InputStreamReader (s.getInputStream()));

    	/* used to write data to the client */
    	OutputStreamWriter osw =  new OutputStreamWriter (s.getOutputStream());

    	/* read the HTTP request from the client */
    	String request = br.readLine();

    	String command = null;
    	String pathname = null;

    	/* parse the HTTP request */
		// Is there a way to put a timer on the connection to prevent hanging up or DoS?
    	StringTokenizer st = new StringTokenizer (request, " ");

		command = st.nextToken();
    	pathname = st.nextToken();
		
		// GET /index.html HTTP/1.0
		// GET /myfile.txt
    	if (command.equals("GET")) {
    		/* if the request is a GET try to respond with the file the user is requesting */
    		// System.out.println("Path name: "+pathname);
			logEntry("logfile.txt", "GET " + pathname + " HTTP/1.0");
    		serveFile (osw,pathname);
    	}
		// PUT blah /myfile.txt
		else if (command.equals("PUT")) {
			/* connect br to put */
			storeFile(br, osw, pathname);
		}
    	else {
    		/* if the request is a NOT a GET, return an error saying this server does not implement the requested command */
    		osw.write ("HTTP/1.0 501 Not Implemented\n\n");
    	}

    	/* close the connection to the client */
		System.out.println("osw.close server socket");
    	osw.close();
    }

	public void logEntry(String filename, String record){
		
	}

	/**
	 * Serve file process a pathname and then writes some data from the output stream (client side) to that path which is a file.
	 * 
	 * For example the client sends this:
	 * 
	 * PUT /myfile.txt
	 * blah
	 * 
	 * The text string "blah" would be written to myfile.txt
	 * If no pathname is provided then the default file is index.html
	 * 
	 * @param osw
	 * @param pathname
	 * @throws Exception
	 */
	public void storeFile(BufferedReader br, OutputStreamWriter osw, String pathname) throws Exception{
		// Step 1. Initialize variables
		FileWriter fw;
	
		// Step 2. Process filename
		if (pathname.charAt(0)=='/') {
			pathname = pathname.substring(1);
		}
	
		/* if there was no filename specified by the client, serve the "index.html" file */
		if (pathname.equals("")) {
			pathname = "index.html";
		}
	
		// Step 3. Open file to write to
		try {
			fw = new FileWriter(pathname);
			osw.write("HTTP/1.0 201 Created");
			osw.flush();
/*
			String s = br.readLine();
	
			// Step 4. read the lines in from the client

			while (s != null) {
				System.out.println("Step 4 writing this line to the file: " + s);
				fw.write(s);
				fw.flush();
				s = br.readLine();
			}
*/	
			fw.write("hello craig");
	        fw.flush();
	
			// Step 5 close the stream
			fw.close();
			
		} catch (Exception e) {
			/* if the file is not found,return the appropriate HTTP response code  */
			osw.write ("HTTP/1.0 500 Internal Server Error\n\n");
			osw.write(e.getMessage());
			osw.flush();
		}
	}	
	

    public void serveFile (OutputStreamWriter osw, String pathname) throws Exception {
    	FileReader fr=null;
    	int c=-1;
    	StringBuffer sb = new StringBuffer();

    	/* remove the initial slash at the beginning of the pathname in the request */
    	if (pathname.charAt(0)=='/')
    		pathname=pathname.substring(1);

    	/* if there was no filename specified by the client, serve the "index.html" file */
    	if (pathname.equals(""))
    		pathname="index.html";

    	/* try to open file specified by pathname */
    	try {
//    		System.out.println("Path name: "+pathname);
    		fr = new FileReader (pathname);
    		c = fr.read();
    	}
    	catch (Exception e) {
    		/* if the file is not found,return the appropriate HTTP response code  */
    		osw.write ("HTTP/1.0 404 Not Found\n\n");
    		return;
    	}

 	/* if the requested file can be successfully opened
 	   and read, then return an OK response code and
 	   send the contents of the file */
    	osw.write ("HTTP/1.0 200 OK\n\n");
    	while (c != -1) {
    		sb.append((char)c);
    		c = fr.read();
    	}
    	osw.write (sb.toString());

		// Close the file reader stream since we are done reading bytes (chars)
		fr.close();
    }

    /* This method is called when the program is run from the command line. */
    public static void main (String argv[]) throws Exception {
    	/* Create a SimpleWebServer object, and run it */
    	SimpleWebServer sws = new SimpleWebServer();
    	sws.run();
    }
}

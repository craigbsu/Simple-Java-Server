import java.io.*;
import java.net.*;

public class SimpleWebClient {
    private static final String hostName = "localhost";
    private static final int PORT = 8080;

	public static void main(String[] args) throws IOException {
        try (
            Socket serverSocket = new Socket(hostName, PORT);
            PrintWriter out =
                new PrintWriter(serverSocket.getOutputStream(), true);
            BufferedReader in =
                new BufferedReader(
                    new InputStreamReader(serverSocket.getInputStream()));
            BufferedReader stdIn =
                new BufferedReader(
                    new InputStreamReader(System.in))
        ) {
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                //out.flush();
                System.out.println("BEFORE userInput: " + userInput);
                String response = in.readLine();
                System.out.println("AFTER response from server: " + response);
                if (response != null) {
                	System.out.println("Response from Server: ");
                	System.out.println(response);
                    System.out.flush();
                	while ((response = in.readLine()) != null) {
                		System.out.println(response);
                	}
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +  hostName);
            System.exit(1);
        } 
    }
}

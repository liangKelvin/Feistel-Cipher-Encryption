import java.net.*;
import java.util.*;
import java.io.*;


public class Server {


	public static List<User> users;


	public static void main(String[] args) throws IOException {

		boolean isListening = true;
		int portNum = 16000;


		startServer();

		try {
			ServerSocket serverSocket = new ServerSocket(portNum);
			// starts a new server thread for each new client
			while(isListening) {
				System.out.println("Server Listening");
				new ServerThread(serverSocket.accept(), users).start();
			}
			
			serverSocket.close();

		} catch(IOException e) {
			System.err.println("error trying to listen to port: " + portNum);
			System.exit(-1);
		}
	}

	private static void startServer() {
		users = new ArrayList<User>();

		users.add(new User("Kelvin", "abcdefg"));
		users.add(new User("Max", "gfedcba"));
		users.add(new User("Jerry", "afdqefewqf"));

		System.out.println("Server Started");
	}
}
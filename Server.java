import java.net.*;
import java.util.*;
import java.io.*;

public class Server {


	public static List<User> users;
	public static String fileName = "users.ser";
	public static SaveLoadController fileManager;
	public static ServerSocket serverSocket;


	public static void main(String[] args) throws IOException {

		boolean isListening = true;
		int portNum = 16000;

		// if shadow file exists ignore 
		File f = new File(fileName);
		if(!f.exists() && !f.isDirectory()) { 
    		startServer();
		}

		try {

			serverSocket = new ServerSocket(portNum);
			System.out.println("Server has started, Now Listening");
			int numOfClients = 0;
			// starts a new server thread for each new client
			while(isListening) {
				new ServerThread(serverSocket.accept()).start();
				numOfClients++;
				System.out.println("made new thread: Current Client Count: " + numOfClients);
			}
			
			serverSocket.close();

		} catch(IOException e) {
			System.err.println("error trying to listen to port: " + portNum);
			System.exit(-1);
		}
	}

	// this function creates the shadow file 
	// file will contain user names and their salted passwords
	private static void startServer() {
		

		try{

			Encryption encryptionHandler = new Encryption();
			BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
			String userName = "bitchass";
			String passWord = null;
			int count = 1; // user count

			users = new ArrayList<User>();
			fileManager = new SaveLoadController();

			System.out.println("Please Enter All Your Users");

			while(!userName.equals("No")) {
				System.out.print("Enter User " + Integer.toString(count) + " : ");
				count++;
				userName = read.readLine();
				System.out.print("Password: ");
				passWord = read.readLine();

				// this is the servers private hash 
				// it uses this to TEA encrypt the shadow passwords
				String privateHash = "abcdefg";
				System.out.println(passWord);

				byte[] encryptedPass = encryptionHandler.encrypt(passWord.getBytes(), privateHash);
				users.add(new User(userName, encryptedPass));

				System.out.print("More Users? Enter Yes or No: ");
				userName = read.readLine();

			}
			fileManager.saveToShadowFile(fileName, users);
		}

		catch(IOException ioe) {
		System.out.println(ioe.getMessage());
		}
	}
}
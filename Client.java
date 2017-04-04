import java.io.*;
import java.net.*;


public class Client {

	public static void main(String args[]) {

		// check arguments
		if(args.length != 3) {
			System.err.println("error expected three arguments: UserId, Pass, HostName");
			System.exit(1);
		}


		System.out.println("Starting Client");
		// server/user info
		int port = 16000; // always 16000
		// user info 
		String userName = args[0];
		String key = args[1];
		String host = args[2];

		Encryption encryptionHandler = new Encryption();
		System.out.println("Created Encryption Handler");

		// establish socket connection (Port 16000)
		try{

			// set up socket and in/out streams; 
			Socket socket = new Socket(host, port);
			System.out.println("Socket and Streams set up");
			
			ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

			// how we read from streams 
			BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
			Response fromServer; // data from Server
			String fromUser; // user data

			// Send initial Message 
			byte[] initialMessage = encryptionHandler.encrypt("AUTH", key);
			outputStream.writeObject(new Request(initialMessage)); // say hi to the server :)
			System.out.println("Sent initial Message");

			// recieve from server
			while((fromServer = (Response) inputStream.readObject()) != null) {
				System.out.println("recieved first response form Server");
				// extract msg and decrypt
				byte[] decrypted = encryptionHandler.decrypt(fromServer.getMessage(), key);
				String newMessage = new String(decrypted);

				if(newMessage.startsWith("EXIT")) {
					break; // user ending connection
					// initial acknowledgement for connection
				} else if(newMessage.startsWith("ACK")) {

					System.out.println("Connected");
					// prompt user for info
					System.out.println("What File would you like to retrieve?");
					System.out.println("To exit: \"exit\"");

				} else if(newMessage.startsWith("NOTFOUND")) {

					System.out.println("File Not Found");

				} else {

					String[] splitMessage = newMessage.split("\n");
					String filename = splitMessage[0];
					File file = new File(filename);
					PrintWriter writer = new PrintWriter(file);

					for(int i = 1; i < splitMessage.length; i++) {
						writer.println(splitMessage[i]);
					}
					writer.close();
					System.out.println("File: " + filename + "downloaded");
				}

				fromUser = read.readLine();
				if(fromUser != null) {
					// encrypt here
					byte[] encryptMessage = encryptionHandler.encrypt(fromUser.getBytes(), key);
					outputStream.writeObject(encryptMessage);
				}
			}
			// done
			socket.close();

		} 

		catch(EOFException eof) {
			System.out.println("Server closed");
			System.exit(1);
		}

		catch(SocketException e) {
			System.out.println(e.getMessage());
		}

		catch(IOException ioe) {
			System.out.println(ioe.getMessage());
		}

		catch(ClassNotFoundException cnfe) {
			System.out.println(cnfe);
		}
	}
}
import java.net.*;
import java.io.*;
import java.util.*;
import java.io.File;
import java.nio.file.Files;
import java.util.*;


public class ServerThread extends Thread{

	private enum ServerState {
		NORM, 
		AUTH
	}


	private Socket socket;
	private User user;
	private List<User> users; 
	private ServerState state;
	public Encryption encryptionHandler;
	private final String fileName = "users.ser";
	public SaveLoadController fileManager = new SaveLoadController();
	private FileInputStream fis;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private DiffieH dhHandler;
	private String key;


	public ServerThread(Socket socket) {

		super("ServerThread");
		this.users = users;
		this.socket = socket;
		this.state = ServerState.AUTH;

	}

	public void run() {

		System.out.println("Server Thread Started");
		encryptionHandler = new Encryption();
		users = fileManager.loadFromShadowFile(fileName); // load user list

		try{
			// set up object streams
			input = new ObjectInputStream(socket.getInputStream());
			output = new ObjectOutputStream(socket.getOutputStream());
			fis = null;
			dhHandler = new DiffieH();

			// Diffie Hellman Key Exchange
			key = dhHandler.handShake(input, output);

			// expecting to Recieve User object
			byte[] encryptedData = (byte[]) input.readObject();
			byte[] data = encryptionHandler.decrypt(encryptedData, key);
			String userName = new String(data);

			// tell Client you got the userName
			String recieved = "ACK";
			encryptedData = encryptionHandler.encrypt(recieved.getBytes(), key);
			output.writeObject(encryptedData);

			// recieve User Password
			encryptedData = (byte[]) input.readObject();
			byte[] saltedPass = encryptionHandler.decrypt(encryptedData, key);

			User client = new User(userName, saltedPass);
			// check if user info matches some user in the shadow file
			if(authorize(client)) {

				this.state = ServerState.NORM;
				String reply = "Access-Granted";
				encryptedData = encryptionHandler.encrypt(reply.getBytes(), key);
				output.writeObject(encryptedData);
				output.flush();
				System.out.println("Access-Granted Response sent");

			} else {

				String reply = "Access-Denied";
				encryptedData = encryptionHandler.encrypt(reply.getBytes(), key);
				output.writeObject(encryptedData);
				output.flush();
				System.out.println("Access Denied Response sent");
				socket.close();

			}


			// enter file transfer mode
			while((encryptedData = (byte[]) input.readObject()) != null) {

				// make sure server state is correct
				if(this.state == ServerState.AUTH) {	

				} else {
					// decrypt for FileName
					byte[] decrypted = encryptionHandler.decrypt(encryptedData, key);
					String fileName = new String(decrypted);
					// if the client wants to terminate
					if(fileName.equals("Exit")) {
						break;
					}
					System.out.println("FileName: " + fileName);

					// set up File for transfer
					File myFile = new File(fileName);
					// if file does not exist
					if(!myFile.exists() && !myFile.isDirectory()) { 
						String reply = "NOTFOUND";
						encryptedData = encryptionHandler.encrypt(reply.getBytes(), key);
						output.writeObject(encryptedData);
						System.out.println("File Not Found, Sent Reply");
 					} else {

						byte[] content = Files.readAllBytes(myFile.toPath());
						byte[] encryptedContent = encryptionHandler.encrypt(content, key);
						output.writeObject(encryptedContent); // send
						System.out.println("File Sent");
					}
				}
			}
			output.close();
			socket.close();
		}

		catch(EOFException eof)
		{		
			System.out.println("Connection terminated.");
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}
	// authenticate
	public boolean authorize(User client) {
		for(User user : this.users) {

			if(user.userName.equals(client.userName)) {
				if(Arrays.equals(user.key, client.key)) {
					return true; 
				}
			}
		}	
		return false; 
	}	
}	
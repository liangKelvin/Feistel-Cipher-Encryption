import java.net.*;
import java.io.*;
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
	private Encryption encryptionHandler;

	public ServerThread(Socket socket, List<User> users) {

		super("ServerThread");
		this.users = users;
		this.socket = socket;
		this.state = ServerState.AUTH;

	}

	public void run() {

		try{

			ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

			Request request;
			Response response; 

			while((request = (Request) input.readObject()) != null) {

				if(this.state == ServerState.AUTH) {
					if(authorize(request.message)) {

						state = ServerState.NORM;
						String reply = "Connected";
						response = new Response(encryptionHandler.encrypt(reply.getBytes(), user.key));
						output.writeObject(response);
						output.flush();
					} else {
						break;
					}
				} else {
					// file stuff
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
	public boolean authorize(byte[] message) {

		for(User user : this.users) {
			byte[] decrypted = encryptionHandler.decrypt(message, user.key);
			String decryptedMsg = new String(decrypted);

			if(decryptedMsg.startsWith("AUTH")) {
				user.authenticated = true;
				this.user = user;
				System.out.println("Authenticated: " + user.userName);
				return true;
			}
		}
		return false; 
	}
}	
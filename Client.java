import java.io.*;
import java.net.*;
import java.io.File;
import java.nio.file.*;
import java.util.*;


public class Client {

	public static Socket socket;
	public static ObjectOutputStream outputStream;
	public static ObjectInputStream inputStream;
	public static DiffieH dhHandler;
	public static Encryption encryptionHandler;
	public static final String privateHash = "abcdefg";
	public static BufferedReader read;
	public static String key;

	public static void main(String args[]) throws Exception {

		// check arguments
		if(args.length != 3) {
			System.err.println("error expected three arguments: UserId, Pass, HostName");
			System.exit(1);
		}


		System.out.println("Starting Client");

		Encryption encryptionHandler = new Encryption();

		// server/user info
		int port = 16000; // always 16000
		// user info 
		String userName = args[0];
		String pass = args[1];
		String host = args[2];
		String fromUser = null; // user data

		// establish socket connection (Port 16000)
		try{

			// set up socket and in/out streams; 
			socket = new Socket(host, port);
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
			dhHandler = new DiffieH();

			// how we read from commandline
			read = new BufferedReader(new InputStreamReader(System.in));

			// Diffie Hellman key exchange
			key = dhHandler.handShake(inputStream, outputStream);

            byte[] saltedPass = encryptionHandler.encrypt(pass.getBytes(), privateHash);
			
			// first send UserName
			byte[] encryptedData = encryptionHandler.encrypt(userName.getBytes(), key);
			outputStream.writeObject(encryptedData);

			encryptedData =  (byte[]) inputStream.readObject();
			byte[] data = encryptionHandler.decrypt(encryptedData, key);
			String recieved = new String(data);

			System.out.println(recieved);

			if(!recieved.equals("ACK")) {
				System.out.println("userName not sent error");
				socket.close();
			}

			encryptedData = encryptionHandler.encrypt(saltedPass, key);
			outputStream.writeObject(encryptedData);

			// Access granted?
			encryptedData = (byte[]) inputStream.readObject();
			data = encryptionHandler.decrypt(encryptedData, key);
			String access = new String(data);

			if(access.equals("Access-Denied")) {
				System.out.println(access + ", Password Incorect");
				System.exit(1);
			}


			System.out.println("Access-Granted");
			System.out.println("To exit: \"exit\"");
			System.out.print("What File would you like to retrieve?: ");

			String fileName = read.readLine();
			encryptedData = encryptionHandler.encrypt(fileName.getBytes(), key);
			outputStream.writeObject(encryptedData);

			// recieve from server
			while((encryptedData = (byte[]) inputStream.readObject()) != null) {

				// extract msg and decrypt
				byte[] decrypted = encryptionHandler.decrypt(encryptedData, key);
				String newMessage = new String(decrypted);

				if(newMessage.startsWith("EXIT")) {
					break; // user ending connection
					// initial acknowledgement for connection

				} else if(newMessage.startsWith("NOTFOUND")) {

					System.out.println("File Not Found");
					System.out.print("Would you like to try to download another file? Answer Yes or No: ");
					fromUser = read.readLine();

					if (fromUser.equals("No")) {
						break;
					}

					System.out.print("What is the FileName?: ");
					fileName = read.readLine();
					encryptedData = encryptionHandler.encrypt(fileName.getBytes(), key);
					outputStream.writeObject(encryptedData);

				} else {
					// encrypted File being sent over.
					System.out.println("Recieving File");
					Path currentRelativePath = Paths.get("");
					String s = currentRelativePath.toAbsolutePath().toString();
					File newFile = new File(fileName);
					byte[] decryptedContent = encryptionHandler.decrypt(encryptedData, key);
					Files.write(newFile.toPath(), decryptedContent);
					System.out.println("File downloaded");

					System.out.println("Would you like to downloaded another file? Answer Yes or No: ");
					fromUser = read.readLine();

					if(fromUser.equals("No")) {
						break;
					}

					System.out.print("What is the FileName?: ");
					fileName = read.readLine();
					encryptedData = encryptionHandler.encrypt(fileName.getBytes(), key);
					outputStream.writeObject(encryptedData);
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
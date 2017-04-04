import java.io.Serializable;

// class to make a serializable object for requests
public class Request implements Serializable {

	private static final long serialV = 1L;
	public byte[] message;

	public Request(byte[] message) {
		this.message = message; 
	}
}
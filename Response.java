import java.io.Serializable;


public class Response implements Serializable {


	private static final long serialV = 1L;
	public byte[] message;

	public Response(byte[] message) {
		this.message = message;
	}

	public byte[] getMessage() {
		return this.message;
	}

}

import java.io.Serializable;

public class User implements Serializable{

	public String userName;
	public byte[] key;

	public User(String name, byte[] key) {
		this.userName = name;
		this.key = key;
	}
}
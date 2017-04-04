public class User {

	public String userName;
	public String key;
	public boolean authenticated;

	public User(String name, String key) {
		this.userName = name;
		this.key = key;
		this.authenticated = false;
	}
}
import java.io.*;
public class User implements Serializable
{
	String username,password;
	User(String username,String password)
	{
		this.username=username;
		this.password=password;
	}
}
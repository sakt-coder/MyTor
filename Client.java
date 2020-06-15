import java.io.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.net.*;
public class Client
{
	public static void main(String args[])throws Exception
	{
		TorObjectStream tos=new TorObjectStream("127.0.0.1",5000);
		tos.writeObject(new User("Nikhil","123456"));
	}
}
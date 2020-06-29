package MyTor;
import java.io.*;
import java.util.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.net.*;
public class TorServer
{
	ServerSocket serverSocket;
	public TorServer(int port)throws Exception
	{
		serverSocket=new ServerSocket(port);
	}
	public TorSocket accept()throws Exception
	{
		Socket socket=serverSocket.accept();
		ObjectOutputStream oos=new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream ois=new ObjectInputStream(socket.getInputStream());
		KeyPair keyPair=genKey();
		oos.writeObject(keyPair.getPublic());
		PublicKey clientKey=(PublicKey)ois.readObject();
		return new TorSocket(clientKey,keyPair.getPrivate(),ois,oos);
	}
	KeyPair genKey()throws Exception
	{
		KeyPairGenerator keyGen=KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024*2);
		return keyGen.genKeyPair();
	}
}
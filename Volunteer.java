package MyTor;
import java.io.*;
import java.util.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.net.*;
public class Volunteer
{
	ObjectInputStream nextois,prevois;
	ObjectOutputStream nextoos,prevoos;
	KeyPair keyPair;
	ServerSocket serverSocket;
	Volunteer()throws Exception
	{
		System.out.println("Enter the port");
		int port=new Scanner(System.in).nextInt();
		System.out.println(port);
		serverSocket=new ServerSocket(port);
	}
	void genKey()throws Exception
	{
		KeyPairGenerator keyGen=KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024*2);
		keyPair=keyGen.genKeyPair();
	}
	void extendPath()throws Exception
	{
		ExtendPath obj=(ExtendPath)decrypt((SealedObject)prevois.readObject());
		System.out.println("Extending path to "+obj.ip+" "+obj.port);
		Socket nextSocket=new Socket(obj.ip,obj.port);
		nextois=new ObjectInputStream(nextSocket.getInputStream());
		nextoos=new ObjectOutputStream(nextSocket.getOutputStream());
	}
	Object decrypt(SealedObject obj)throws Exception
	{
		Cipher cipher=Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE,keyPair.getPrivate());
		return(obj.getObject(cipher));
	}
	public static void main(String args[])throws Exception
	{
		Volunteer vol=new Volunteer();
		while(true)
			vol.init();
	}
	void init()throws Exception
	{
		Socket prevSocket=serverSocket.accept();
		System.out.println("Peer Connected");
		prevoos=new ObjectOutputStream(prevSocket.getOutputStream());
		prevois=new ObjectInputStream(prevSocket.getInputStream());
		genKey();
		prevoos.writeObject(keyPair.getPublic());
		System.out.println("Sent Public Key");
		this.extendPath();
		FwdThread fthread=new FwdThread(this);
		RevThread rthread=new RevThread(this);
		new Thread(fthread).start();
		new Thread(rthread).start();
	}
}

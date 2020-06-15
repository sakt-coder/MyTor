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
	Volunteer()throws Exception
	{
		System.out.println("Enter the port");
		int port=new Scanner(System.in).nextInt();
		System.out.println(port);
		ServerSocket serverSocket=new ServerSocket(port);
		Socket prevSocket=serverSocket.accept();
		System.out.println("Peer Connected");
		prevoos=new ObjectOutputStream(prevSocket.getOutputStream());
		prevois=new ObjectInputStream(prevSocket.getInputStream());
		genKey();
		prevoos.writeObject(keyPair.getPublic());
		System.out.println("Sent Public Key");
	}
	void genKey()throws Exception
	{
		KeyPairGenerator keyGen=KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024);
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
		vol.extendPath();
		FwdThread fthread=new FwdThread(vol);
		RevThread rthread=new RevThread(vol);
		new Thread(fthread).start();
		new Thread(rthread).start();
	}
}

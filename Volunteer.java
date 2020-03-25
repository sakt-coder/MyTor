import java.io.*;
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
		ServerSocket serverSocket=new ServerSocket(5000);
		Socket prevSocket=serverSocket.accept();
		prevois=new ObjectInputStream(prevSocket.getInputStream());
		prevoos=new ObjectOutputStream(prevSocket.getOutputStream());
		genKey();
		prevoos.writeObject(keyPair.getPublic());
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

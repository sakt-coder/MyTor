import java.io.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.net.*;
public class TorSocket
{
	PublicKey clientKey;
	PrivateKey myKey;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	TorSocket(PublicKey clientKey,PrivateKey myKey,ObjectInputStream ois,ObjectOutputStream oos)
	{
		this.clientKey=clientKey;
		this.myKey=myKey;
		this.ois=ois;
		this.oos=oos;
	}
	void writeObject(Serializable ob)throws Exception
	{
		Cipher cipher=Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE,clientKey);
		oos.writeObject(new SealedObject(ob,cipher));
	}
	Object readObject()throws Exception
	{
		SealedObject obj=(SealedObject)ois.readObject();
		Cipher cipher=Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE,myKey);
		return obj.getObject(cipher);
	}
}
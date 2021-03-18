package MyTor;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

public class TorSocket {
	SecretKey AESKey;
	ObjectInputStream ois;
	ObjectOutputStream oos;

	TorSocket(SecretKey AESKey, ObjectInputStream ois, ObjectOutputStream oos) {
		this.AESKey = AESKey;
		this.ois=ois;
		this.oos=oos;
	}
	public void writeObject(Serializable ob)throws Exception {
		Cipher cipher=Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, AESKey);
		oos.writeObject(new SealedObject(ob,cipher));
	}
	public Object readObject()throws Exception {
		SealedObject obj=(SealedObject)ois.readObject();
		Cipher cipher=Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, AESKey);
		return obj.getObject(cipher);
	}
	public void close()throws Exception {
		ois.close();
		oos.close();
		//TODO - close the volunteers also
	}
	public void flush()throws Exception {
		oos.flush();
	}
}
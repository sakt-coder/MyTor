package MyTor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.*;

public class TorObjectStream {
	final long REFRESH_INTERVAL = 3000L;
	SecretKey[] AESKey;
	String[] ipList;
	int[] portList;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	Socket socket;
	final int AES_KEY_SIZE = 128;

	public TorObjectStream(String ip,int port)throws Exception {
		AESKey = new SecretKey[4];
		ipList=new String[4];
		portList=new int[4];

		findVolunteers();// find 3 random volunteers

		ipList[3]=ip;
		portList[3]=port;
		for(int i=0;i<4;i++)
			connect(i);
	}
	private void findVolunteers() throws Exception {
		List<String> pool = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader("volunteers.txt"));
			String line;
			while ((line = br.readLine()) != null)
				pool.add(line);
		} catch(IOException e) {
			throw new Exception("Error reading volunteers.txt");
		}
		if(pool.size() < 3) {
			throw new Exception("Found "+pool.size()+" volunteers, Required 3");
		}
		Collections.shuffle(pool);
		for(int i=0;i<3;i++) {
			String[] addr = pool.get(i).split(":");
			ipList[i] = addr[0];
			portList[i] = Integer.parseInt(addr[1]);
		}
	}
	private void connect(int idx)throws Exception {
		if(idx==0) {
			socket=new Socket(ipList[0],portList[0]);
			oos=new ObjectOutputStream(socket.getOutputStream());
			ois=new ObjectInputStream(socket.getInputStream());
		} else {
			ExtendPath ep=new ExtendPath(ipList[idx],portList[idx]);
			writeObject(ep, idx);
		}
		PublicKey publicKey = (PublicKey) readObject(idx);
		AESKey[idx] = genAESKey();
		writeObject(RSAEncrypt(AESKey[idx], publicKey), idx);
	}
	private SecretKey genAESKey() throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(AES_KEY_SIZE);
		SecretKey key = keyGenerator.generateKey();
		return key;
	}
	private SealedObject RSAEncrypt(Serializable obj, PublicKey publicKey)throws Exception {
		Cipher cipher=Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE,publicKey);
		return new SealedObject(obj,cipher);
	}
	private SealedObject AESEncrypt(Serializable obj, SecretKey AESKey)throws Exception {
		Cipher cipher=Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, AESKey);
		return new SealedObject(obj,cipher);
	}
	private Object AESDecrypt(SealedObject obj, SecretKey AESKey)throws Exception {
		Cipher cipher=Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, AESKey);
		return(obj.getObject(cipher));
	}
	private void writeObject(Serializable obj, int layers)throws Exception {
		for(int i = layers-1; i >= 0; i--)
			obj = AESEncrypt(obj, AESKey[i]);
		oos.writeObject(obj);
	}
	private Object readObject(int layers) throws Exception {
		Object obj = ois.readObject();
		for(int i = 0;i < layers; i++) {
			obj = AESDecrypt((SealedObject) obj, AESKey[i]);
		}
		return obj;
	}
	public void writeObject(Serializable obj)throws Exception {
		writeObject(obj, 4);
	}
	public Object readObject()throws Exception {
		return readObject(4);
	}
	public void flush()throws Exception {
		oos.flush();
	}
	public void close()throws Exception {
		// TODO close volunteers
		socket.close();
	}
}

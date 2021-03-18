package MyTor;

import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;

public class TorServer {
	ServerSocket serverSocket;
	KeyPair keyPair;

	final int RSA_KEY_SIZE = 2048;

	public TorServer(int port)throws Exception {
		serverSocket = new ServerSocket(port);
	}
	public TorSocket accept()throws Exception {
		Socket socket=serverSocket.accept();
		ObjectOutputStream oos=new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream ois=new ObjectInputStream(socket.getInputStream());
		keyPair=genKey();
		oos.writeObject(keyPair.getPublic());
		SecretKey AESKey = (SecretKey) RSADecrypt((SealedObject) ois.readObject(), keyPair.getPrivate());
		return new TorSocket(AESKey, ois, oos);
	}
	private Object RSADecrypt(SealedObject obj, PrivateKey privateKey)throws Exception {
		Cipher cipher=Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return(obj.getObject(cipher));
	}
	private KeyPair genKey()throws Exception {
		KeyPairGenerator keyGen=KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(RSA_KEY_SIZE);
		return keyGen.genKeyPair();
	}
}
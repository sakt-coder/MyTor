package MyTor;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;

public class VolunteerThread extends Thread {
    ObjectInputStream nextois,prevois;
    ObjectOutputStream nextoos,prevoos;
    KeyPair keyPair;
    SecretKey AESKey;
    Socket prevSocket;
    final int RSA_KEY_SIZE = 2048;

    VolunteerThread(Socket prevSocket) {
        this.prevSocket = prevSocket;
    }
    void genKey()throws Exception {
        KeyPairGenerator keyGen=KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(RSA_KEY_SIZE);
        keyPair=keyGen.genKeyPair();
    }
    void extendPath()throws Exception {
        ExtendPath obj = (ExtendPath) AESDecrypt((SealedObject)prevois.readObject(), AESKey);
        Socket nextSocket = new Socket(obj.ip, obj.port);
        nextois=new ObjectInputStream(nextSocket.getInputStream());
        nextoos=new ObjectOutputStream(nextSocket.getOutputStream());
    }
    SealedObject AESEncrypt(Serializable obj, SecretKey AESKey)throws Exception {
        Cipher cipher=Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, AESKey);
        return new SealedObject(obj,cipher);
    }
    Object AESDecrypt(SealedObject obj, SecretKey AESKey)throws Exception {
        Cipher cipher=Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, AESKey);
        return(obj.getObject(cipher));
    }
    Object RSADecrypt(SealedObject obj, PrivateKey privateKey)throws Exception {
        Cipher cipher=Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return obj.getObject(cipher);
    }
    public void run() {
        try {
            prevoos = new ObjectOutputStream(prevSocket.getOutputStream());
            prevois = new ObjectInputStream(prevSocket.getInputStream());
            genKey();
            prevoos.writeObject(keyPair.getPublic());
            AESKey = (SecretKey) RSADecrypt((SealedObject) prevois.readObject(), keyPair.getPrivate());
            this.extendPath();
            new FwdThread(this).start();
            new RevThread(this).start();
        } catch(Exception e) { }
    }
}
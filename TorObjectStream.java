import java.io.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.net.*;
import java.sql.*;
public class TorObjectStream
{
	PublicKey[] publicKey;//publicKey[3] is publicKey of server
	String ipList[];
	int portList[];
	ObjectInputStream ois;
	ObjectOutputStream oos;
	KeyPair keyPair;
	TorObjectStream(String ip,int port)throws Exception
	{
		publicKey=new PublicKey[4];
		ipList=new String[4];
		portList=new int[4];
		findVolunteers();
		System.out.println("Volunteers are");
		for(int i=0;i<3;i++)
			System.out.println(ipList[i]+" "+portList[i]);
		ipList[3]=ip;
		portList[3]=port;
		for(int i=0;i<4;i++)
			connect(i);
		//generate and send public key to server
		genKey();
		oos.writeObject(keyPair.getPublic());
	}
	void genKey()throws Exception
	{
		KeyPairGenerator keyGen=KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024);
		keyPair=keyGen.genKeyPair();
	}
	private void findVolunteers()throws Exception
	{
		Class.forName("com.mysql.cj.jdbc.Driver");
		String url="jdbc:mysql://127.0.0.1:3306/TorDirectory";
		System.out.println("Enter root password");
		String root_password=new String(System.console().readPassword());
		Connection connection=DriverManager.getConnection(url,"root",root_password);
		String query="SELECT * FROM TorDirectory";
		PreparedStatement preStat=connection.prepareStatement(query);
		ResultSet rs=preStat.executeQuery(query);
		int idx=0;
		while(rs.next() && idx<3)
		{
			ipList[idx]=rs.getString("IP");
			portList[idx]=rs.getInt("Port");
			idx++;
		}
	}
	private void connect(int idx)throws Exception
	{
		if(idx==0)
		{
			Socket socket=new Socket(ipList[0],portList[0]);
			oos=new ObjectOutputStream(socket.getOutputStream());
			ois=new ObjectInputStream(socket.getInputStream());
			publicKey[0]=(PublicKey)ois.readObject();
		}
		else
		{
			ExtendPath ep=new ExtendPath(ipList[idx],portList[idx]);
			oos.writeObject(encrypt(ep,idx-1));
			publicKey[idx]=(PublicKey)(ois.readObject());//public keys are unencrypted
		}
		System.out.println(publicKey[idx]);
	}
	private SealedObject encrypt(ExtendPath ep,int idx)throws Exception
	{
		Cipher cipher=Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE,publicKey[idx]);
		return new SealedObject(ep,cipher);
	}
	Object decrypt(SealedObject obj)throws Exception
	{
		Cipher cipher=Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE,keyPair.getPrivate());
		return(obj.getObject(cipher));
	}
	public void writeObject(Serializable obj)throws Exception
	{
		Cipher cipher=Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE,publicKey[3]);
		oos.writeObject(new SealedObject(obj,cipher));//Object cannot be converted to serializable
	}
	public Object readObject()throws Exception
	{
		//todo - change to decrypted read as per own private key
		Object ob=ois.readObject();
		return decrypt((SealedObject)ob);
	}
}
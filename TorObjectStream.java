import java.io.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.net.*;
import java.sql.*;
public class TorObjectStream
{
	PublicKey[] publicKey;
	String ipList[];
	int portList[];
	ObjectInputStream ois;
	ObjectOutputStream oos;
	TorObjectStream(String ip,int port)throws Exception
	{
		publicKey=new PublicKey[3];
		ipList=new String[3];
		portList=new int[3];
		findVolunteers();
		for(int i=0;i<3;i++)
			connect(i);
	}
	void findVolunteers()throws Exception
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
	void connect(int idx)throws Exception
	{
		if(idx==0)
		{
			Socket socket=new Socket(ipList[0],portList[0]);
			ois=new ObjectInputStream(socket.getInputStream());
			oos=new ObjectOutputStream(socket.getOutputStream());
			publicKey[0]=(PublicKey)ois.readObject();
		}
		else
		{
			ExtendPath ep=new ExtendPath(ipList[idx],portList[idx]);
			writeObject(encrypt(ep,idx-1));
			publicKey[idx]=(PublicKey)readObject();
		}
	}
	SealedObject encrypt(ExtendPath ep,int idx)throws Exception
	{
		Cipher cipher=Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE,publicKey[idx]);
		return new SealedObject(ep,cipher);
	}
	void writeObject(Object obj)throws Exception
	{
		oos.writeObject(obj);
	}
	Object readObject()throws Exception
	{
		return ois.readObject();
	}
}
public class Server
{
	public static void main(String args[])throws Exception
	{
		TorServer server=new TorServer(5000);
		TorSocket socket=server.accept();
		User ob=(User)socket.readObject();
		System.out.println(ob.username+" "+ob.password);
	}
}
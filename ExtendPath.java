import java.io.Serializable;
class ExtendPath implements Serializable
{
	String ip;
	int port;
	ExtendPath(String ip,int port)
	{
		this.ip=ip;
		this.port=port;
	}
}
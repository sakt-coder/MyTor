class FwdThread implements Runnable
{
	Volunteer vol;
	FwdThread(Volunteer vol)
	{
		this.vol=vol;
	}
	public void run()
	{
		try
		{
			Object obj=vol.prevois.readObject();
			vol.nextoos.writeObject(obj);
		}catch(Exception e){}
	}
}
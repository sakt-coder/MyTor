class RevThread implements Runnable
{
	Volunteer vol;
	RevThread(Volunteer vol)
	{
		this.vol=vol;
	}
	public void run()
	{
		try
		{
			Object obj=vol.nextois.readObject();
			vol.prevoos.writeObject(obj);
		}catch(Exception e){}
	}
}
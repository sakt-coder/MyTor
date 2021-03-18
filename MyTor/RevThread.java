package MyTor;

import java.io.Serializable;

public class RevThread extends Thread {
	VolunteerThread vol;

	RevThread(VolunteerThread vol) {
		this.vol=vol;
	}
	public void run() {
		try {
			while(true) {
				Object obj=vol.nextois.readObject();
				obj = vol.AESEncrypt((Serializable) obj, vol.AESKey);
				vol.prevoos.writeObject(obj);
			}
		} catch(Exception e){ }
	}
}
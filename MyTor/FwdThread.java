package MyTor;

import javax.crypto.SealedObject;

public class FwdThread extends Thread {
	VolunteerThread vol;

	FwdThread(VolunteerThread vol) {
		this.vol=vol;
	}
	public void run() {
		try {
			while(true) {
				Object obj=vol.prevois.readObject();
				obj = vol.AESDecrypt((SealedObject) obj, vol.AESKey);
				vol.nextoos.writeObject(obj);
			}
		} catch(Exception e) { }
	}
}
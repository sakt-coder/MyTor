package MyTor;

import java.net.ServerSocket;
import java.net.Socket;

public class Volunteer extends Thread {
	ServerSocket serverSocket;

	public Volunteer(int port)throws Exception {
		serverSocket=new ServerSocket(port);
	}
	public void run() {
		while(true) {
			try {
				Socket socket = serverSocket.accept();
				new VolunteerThread(socket).start();
			} catch(Exception e) { }
		}
	}
}

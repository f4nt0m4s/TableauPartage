package thread;

import java.net.MulticastSocket;

import gui.PanelDessin;
import gui.PanelMessage;

import java.net.InetAddress;
import java.net.DatagramPacket;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class ThreadEmission extends Thread {
	
	private static final long serialVersionUID = 1L;
	
	private boolean stop;

	private int port;
	private String ip;
	private InetAddress ipMulticast;
	private MulticastSocket socketEmission;

	private PanelMessage hoteMsg;
	private PanelDessin hoteDessin;

	public ThreadEmission(String ip, int port, PanelMessage hoteMsg, PanelDessin hoteDessin) {
		this.stop = false;
		this.ip = ip;
		this.port = port;
		this.hoteMsg = hoteMsg;
		this.hoteDessin = hoteDessin;
		try {
			this.ipMulticast = InetAddress.getByName(this.ip);
			this.socketEmission = new MulticastSocket(port);
			this.socketEmission.joinGroup(this.ipMulticast);
			this.socketEmission.setTimeToLive(15);
		} catch (Exception e) {
		}
	}

	public void startEmission() {
		this.start();
	}

	@Override
	public void run() {
		// Demande de tchat lors de la connexion du client
		this.emission("TD-");
		// Demande de récupération du dessin lors de la connexion du client
		this.emission("DES-D");
		while (!stop) {
		}
		try {
			this.socketEmission.leaveGroup(this.ipMulticast);
		} catch (Exception e) {
		}
		this.socketEmission.close();
		// System.out.println("[CLOSING EMISSION]");
	}

	// Emission
	public boolean emission(Object obj) {
		if (obj == null)
			return false;
		try {
			// System.out.println("Envoi d'un objet...");
			Object objSend = obj;
			DatagramPacket packet = null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(objSend);
			byte[] data = baos.toByteArray();
			packet = new DatagramPacket(data, data.length, this.ipMulticast, this.port);
			this.socketEmission.send(packet);
			oos.close();
			baos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean setStop() {
		return this.stop = !this.stop;
	}
}

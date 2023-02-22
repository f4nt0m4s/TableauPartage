package thread;

import java.net.MulticastSocket;
import java.util.ArrayList;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.concurrent.CopyOnWriteArrayList;

import formes.Carre;
import formes.Cercle;
import formes.Fleche;
import formes.Forme;
import formes.Texte;
import gui.PanelDessin;
import gui.PanelMessage;

public class ThreadReception extends Thread {

	private static final long serialVersionUID = 1L;

	private boolean stop;

	private int port;
	private String ip;
	private InetAddress ipMulticast;
	private MulticastSocket socketReception;

	private PanelMessage hoteMsg;
	private PanelDessin hoteDessin;

	public ThreadReception(String ip, int port, PanelMessage hoteMsg, PanelDessin hoteDessin) {
		this.stop = false;
		this.ip = ip;
		this.port = port;
		this.hoteMsg = hoteMsg;
		this.hoteDessin = hoteDessin;
		try {
			this.ipMulticast = InetAddress.getByName(this.ip);
			this.socketReception = new MulticastSocket(port);
			this.socketReception.joinGroup(this.ipMulticast);
		} catch (Exception e) {}
	}
	
	public void startReception() {
		this.start();
	}

	@Override
	public void run() {
		DatagramPacket packetRecu;
		byte[] contenuPacket;

		while (!stop) {
			contenuPacket = new byte[(1024 * 4)]; // (1024*4) maximum transfer objet
			packetRecu = new DatagramPacket(contenuPacket, contenuPacket.length, this.ipMulticast, this.port);

			try {
				// System.out.println("En attente...");
				// Attente de réception d'un packet
				this.socketReception.receive(packetRecu);
			} catch (Exception e) {
			}

			try {
				// Désérialise l'objet
				ByteArrayInputStream bais = new ByteArrayInputStream(contenuPacket);
				ObjectInputStream ois = new ObjectInputStream(bais);
				Object obj = ois.readObject();

				// Traitement du tchat
				if (obj instanceof String) {
					String response = (String) obj;
					this.processingMessage(response);
				}

				// Traitement du dessin
				if (obj.getClass().getSuperclass().getName().equals("Forme")) {
					Forme forme = (Forme) obj;
					this.processingDraw(forme);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		try {
			this.socketReception.leaveGroup(this.ipMulticast);
		} catch (Exception e) {
		}
		this.socketReception.close();
		// System.out.println("[CLOSING RECEPTION]");
	}

	// Traitement de message du tchat
	private void processingMessage(String response) {
		String res = response;
		res = res.trim();
		// System.out.println("Recu : " + res);

		// T- lorsqu'un message a été envoyé dans le tchat
		if (res.startsWith("T-")) {
			// System.out.println("[T] : TAILLE : " + this.hoteMsg.getTabMsg().size());
			res = res.replace("T-", ""); // Enlève le T- pour garder l'esthétique du tchat
			this.hoteMsg.messageReceived(res, true); // Mis à jour graphiquement et enregistre ce message
		}

		// Signal [TD-] : pour une demande de tchat
		if (res.equals("TD-")) {
			// Inutile d'envoyer une demande de tchat si le tchat enregistré est vide
			if (this.hoteMsg.getTabMsg().size() != 0) {
				// System.out.println("[TD] : TAILLE : " + this.hoteMsg.getTabMsg().size());

				// Pour chaque ligne du tchat qui doit être récupéré par le nouveau client
				String sHistory = "";
				ArrayList<String> alString = this.hoteMsg.getTabMsg();
				for (String s : alString) {
					sHistory += s + "\n"; // On ajoute chaque ligne du tchat
				}
				// Envoie l'historique tchat pour qu'il soit afficher
				this.hoteMsg.messageSend("TA-" + sHistory);
			}

		}

		// Signal [TA-] : pour afficher la demande de tchat
		if (res.startsWith("TA-")) {
			// System.out.println("TA-RECU pour demande de tchat");
			// System.out.println("[TA] : TAILLE : " + this.hoteMsg.getTabMsg().size());
			// Effectue la mis à jour du jour que si la liste du nouveau client est == 0
			// Sinon ça fait des doublons dans le tchat
			if (this.hoteMsg.getTabMsg().size() == 0) {
				res = res.replace("TA-", ""); // Enlève le TA- pour garder l'esthétique du tchat
				this.hoteMsg.messageReceived(res, true);
			}
		}

		// Signal [DES-D] : pour une demande de dessin
		if (res.equals("DES-D")) {
			if (this.hoteDessin.getLstForme().size() != 0) {
				CopyOnWriteArrayList<Forme> listForm = this.hoteDessin.getLstForme();
				// System.out.println("Demande de dessin reçu - [DES] taille : " +
				// this.hoteDessin.getLstForme());
				for (Forme forme : listForm) {
					// System.out.println("Demande de dessin reçu - [DES] : " + forme.toString());
					this.hoteDessin.objectSend(forme);
				}
			}
		}

		// Signal [DES-S] : pour supprimer un objet
		if (res.startsWith("DES-S")) {
			if (res.indexOf(';') != -1) // Suppresion pour le undo/gomme
			{
				String[] sRes = res.split(";");
				int numDeleteForme = Integer.parseInt(sRes[1]);

				for (Forme forme : this.hoteDessin.getLstForme()) {
					if (forme.getNumForme() == numDeleteForme) {
						// System.out.println("[SUPPRESION]" + forme.getNumForme() + " taille avant : "
						// + this.hoteDessin.getLstForme().size());

						this.hoteDessin.getLstForme().remove(forme);
						// System.out.println("[SUPPRESION]" + forme.getNumForme() + " taille après : "
						// + this.hoteDessin.getLstForme().size());
						this.hoteDessin.repaint();
						break;
					}
				}
			} else // suppresion pour actualiser l'affichage
			{
				this.hoteDessin.deleteLastSaved();
				this.hoteDessin.repaint();
			}
		}
	}

	// Traitement du dessin
	private void processingDraw(Forme forme) {
		Forme formeReceived = forme;
		CopyOnWriteArrayList<Forme> listForm = this.hoteDessin.getLstForme();
		boolean bFound = false;
		for (Forme formeCompare : listForm) {
			if (formeReceived instanceof Carre && formeCompare instanceof Carre) {
				Carre carreReceived = (Carre) formeReceived;
				Carre carreCompare = (Carre) formeCompare;
				if (carreReceived.equals(carreCompare)) {
					bFound = true;
					break;
				}
			} else if (formeReceived instanceof Cercle && formeCompare instanceof Cercle) {
				Cercle cercleReceived = (Cercle) formeReceived;
				Cercle cercleCompare = (Cercle) formeCompare;
				if (cercleReceived.equals(cercleCompare)) {
					bFound = true;
					break;
				}
			} else if (formeReceived instanceof Fleche && formeCompare instanceof Fleche) {
				Fleche flecheReceived = (Fleche) formeReceived;
				Fleche flecheCompare = (Fleche) formeCompare;
				if (flecheReceived.equals(flecheCompare)) {
					bFound = true;
					break;
				}
			} else if (formeReceived instanceof Texte && formeCompare instanceof Texte) {
				Texte texteReceived = (Texte) formeReceived;
				Texte texteCompare = (Texte) formeCompare;
				if (texteReceived.equals(texteCompare)) {
					bFound = true;
					break;
				}
			}
		}

		// Si une forme existe déjà alors pas d'ajout
		if (!bFound) {
			// System.out.println("[RECEPTION] : Objet reçu de type Forme et pas trouvé !");
			this.hoteDessin.objectReceived(formeReceived, true);
		}
		// System.out.println("Taille trop elevée ? : " +
		// this.hoteDessin.getLstForme().size());
	}
}

package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import configuration.ConfigurationLoader;
import thread.ThreadEmission;
import thread.ThreadReception;

import java.util.Properties;

/**
 * Classe FrameApp
 * @version : 1.0
 * @date : 08/03/2021
 */

public class FrameApp extends JFrame {
	/**
	 * Définition du serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private PanelMessage panelMessage;
	private PanelDessin panelDessin;

	private ThreadEmission emis;
	private ThreadReception recep;

	/*-----------------*/
	/* Le Constructeur */
	/*-----------------*/
	/**
	 * @param ip   : L'ip de connexion.
	 * @param port : Le port de connexion.
	 */
	public FrameApp(String ip, int port) {
		/*---------------------------*/
		/* Informations sur la Frame */
		/*---------------------------*/
		int largeurEcran, hauteurEcran;
		Dimension dimEcran;

		dimEcran = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		hauteurEcran = (int) dimEcran.getHeight();
		largeurEcran = (int) dimEcran.getWidth();

		double coeffLargeur = (largeurEcran / (double) 700);
		double coeffHauteur = (hauteurEcran / (double) 620);

		int largeurFrameApp = (int) (largeurEcran / coeffLargeur);
		int hauteurFrameApp = (int) (hauteurEcran / coeffHauteur);

		this.setTitle("Application partagée");
		this.setSize(largeurFrameApp, hauteurFrameApp);
		this.setLocation((largeurEcran / 2) - (this.getWidth() / 2), (hauteurEcran / 2) - (this.getHeight() / 2));
		this.setLayout(new BorderLayout());

		/*-------------------------*/
		/* Création des composants */
		/*-------------------------*/
		this.panelMessage = new PanelMessage();
		int width = (int) (this.getWidth() * 0.30);
		this.panelMessage.setPreferredSize(new Dimension(width, this.panelMessage.getHeight()));
		this.panelDessin = new PanelDessin();

		/*-------------------------------*/
		/* Positionnement des composants */
		/*-------------------------------*/
		this.add(panelMessage, BorderLayout.WEST);
		this.add(panelDessin, BorderLayout.CENTER);

		/*-------------------------*/
		/* Activation du composant */
		/*-------------------------*/
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);

		/*--------------------*/
		/* Réseau avec Thread */
		/*--------------------*/
		this.recep = new ThreadReception(ip, port, this.panelMessage, this.panelDessin);
		this.emis = new ThreadEmission(ip, port, this.panelMessage, this.panelDessin);
		this.panelMessage.setEmis(this.emis);
		this.panelDessin.setEmis(this.emis);
		this.recep.startReception();
		this.emis.startEmission();
	}

	public static void main(String[] args) {
		if (args.length != 0) {
			System.out.println("USAGE: java FrameApp");
		} else {
			Properties props = ConfigurationLoader.load();
			if (props == null) {
				System.err.println("Une erreur est survenue lors du chargement du fichier de configuration.");
				System.exit(ERROR);
			}
			String groupeIP = props.getProperty("GROUP_IP");
			final int port = Integer.parseInt(props.getProperty("PORT"));
			new FrameApp(groupeIP, port);
		}
	}
}
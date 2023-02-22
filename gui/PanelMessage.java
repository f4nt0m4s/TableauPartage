package gui;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;

import javax.swing.BorderFactory;
import javax.swing.border.LineBorder;

import thread.ThreadEmission;

import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.Border;

import java.util.ArrayList;

/**
 * Classe PanelMessage
 * @version : 1.0 
 * @date : 08/03/2021
 */

public class PanelMessage extends JPanel implements ActionListener {
	/**
	 * Définition du serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private static final String MSG_DEFAULT_TEXT_ZONE = "Zone de saisie";

	private JTextArea txtArea; // Affichage des message
	private JTextField txtFld; // Zone pour saisir un message
	private JButton btnSend; // Bouton d'envoi du message

	/*-----------------*/
	/* Communication */
	/*-----------------*/

	private ThreadEmission emis;
	private ArrayList<String> tabMsg;

	/*-----------------*/
	/* Le Constructeur */
	/*-----------------*/

	public PanelMessage() {
		super();

		/*---------------------------*/
		/* Informations sur le Panel */
		/*---------------------------*/
		this.setLayout(new BorderLayout());
		this.setBackground(Color.RED);
		// int top, int left, int bottom, int right
		this.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, Color.BLACK));

		/*-------------------------*/
		/* Création des composants */
		/*-------------------------*/

		this.txtArea = new JTextArea();
		this.txtArea.setBackground(Color.GREEN);
		this.txtArea.setEditable(false);
		this.txtArea.setLineWrap(true);

		this.txtFld = new JTextField(PanelMessage.MSG_DEFAULT_TEXT_ZONE);
		this.txtFld.setPreferredSize(new Dimension(95, 25));
		this.txtFld.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (PanelMessage.this.txtFld.getText().equals(PanelMessage.MSG_DEFAULT_TEXT_ZONE)) {
					PanelMessage.this.txtFld.setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (PanelMessage.this.txtFld.getText().equals("") || PanelMessage.this.txtFld.getText().length() == 0) {
					PanelMessage.this.txtFld.setText(PanelMessage.MSG_DEFAULT_TEXT_ZONE);
				}
			}
		});

		this.btnSend = createJButton("Envoyer");
		int width = 55;
		int height = 20;
		this.btnSend.setPreferredSize(new Dimension(width, height));

		JPanel panelAction = new JPanel();
		panelAction.setLayout(new FlowLayout());
		panelAction.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));

		/*-------------------------------*/
		/* Positionnement des composants */
		/*-------------------------------*/
		panelAction.add(this.txtFld);
		panelAction.add(this.btnSend);

		this.add(this.txtArea, BorderLayout.CENTER);
		this.add(panelAction, BorderLayout.SOUTH);

		/*-------------------------*/
		/* Activation du composant */
		/*-------------------------*/
		this.btnSend.addActionListener(this);

		this.setVisible(true);

		this.emis = null;

		/*-------------------*/
		/* Messages du tchat */
		/*-------------------*/

		this.tabMsg = new ArrayList<String>();
	}

	/*----------------------------------------------*/
	/* L'ÉVÈNEMENT ACTION-LISTENER */
	/*----------------------------------------------*/

	/**
	 * Si l'utilisateur clique sur le bouton pour envoyé un message.
	 * 
	 * @param e : L' objet évènement détecté.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.btnSend) {
			this.messageSend("T-" + this.txtFld.getText());
		}
	}

	/**
	 * Méthode pour créer un JButton stylisé
	 * 
	 * @param texte : Le texte du JButton src =
	 *              https://stackoverflow.com/questions/1839074/howto-make-jbutton-with-simple-flat-style
	 */
	private static JButton createJButton(String texte) {
		JButton btn = new JButton(texte);
		btn.setBackground(Color.WHITE);
		btn.setForeground(Color.BLACK);
		Border line = new LineBorder(Color.BLACK);
		Border margin = new EmptyBorder(0, 0, 0, 0);
		Border compound = new CompoundBorder(line, margin);
		btn.setBorder(compound);
		btn.setFont(new Font("Arial", Font.BOLD, 10));
		btn.setFocusable(false);
		btn.setMargin(new Insets(0, 0, 0, 0));
		return btn;
	}

	/*------------------------------------------*/
	/* RÉSEAU */
	/*------------------------------------------*/
	public void setEmis(ThreadEmission emis) {
		this.emis = emis;
	}

	public ArrayList<String> getTabMsg() {
		return this.tabMsg;
	}

	public void messageReceived(String msg, boolean register) {
		this.txtArea.setText(this.txtArea.getText() + msg + "\n");
		if (register) {
			this.tabMsg.add(msg);
		}
	}

	public void messageSend(String msg) {
		// System.out.println("Envoi de : " + msg);
		if (msg != null && msg.length() > 0)
			this.emis.emission(msg);
	}

}
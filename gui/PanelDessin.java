package gui;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JOptionPane;

import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

import formes.Carre;
import formes.Cercle;
import formes.Fleche;
import formes.Forme;
import formes.Texte;
import thread.ThreadEmission;

import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;

import java.lang.Math;

/**
 * Classe PanelDessin
 * @version : 1.0 
 * @date : 08/03/2021
 */

public class PanelDessin extends JPanel implements ActionListener {
	/**
	 * Définition du serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	// Panel North
	private static final String[] TAB_OBJECTS = { "carré", "rond", "flèche", "gomme", "undo", "texte", "plein",
			"vide" };
	private LinkedHashMap<String, JButton> lkdHSetObjects;

	// Panel Center
	private PanelZoneDessin panelZoneDessin;

	// Panel South
	private static final String[] TAB_NAMES_COLORS = { "rouge", "bleu", "vert", "jaune", "noir", "blanc" };
	private static final Color[] TAB_COLORS = { Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.BLACK,
			Color.WHITE };
	private LinkedHashMap<String, JButton> lkdHSetColors;
	private JPanel[] tabPanelColors;

	// Gestion des formes et des couleurs
	private CopyOnWriteArrayList<Forme> alListForme;
	private Forme formSelected;
	private Color colorSelected;
	private String currentText; // Le texte courant lorsque l'utilisateur veut afficher du texte sur le dessin
	private boolean isEraserEnabled; // L'outil gomme activé
	private boolean isFormFilledEnabled; // Forme(Carré/Rond) plein ou vide activé
	private int indexLastForm;

	private ThreadEmission emis;

	/*-----------------*/
	/* Le Constructeur */
	/*-----------------*/

	public PanelDessin() {
		super();
		/*---------------------------*/
		/* Informations sur le Panel */
		/*---------------------------*/
		this.setLayout(new BorderLayout());
		this.setBackground(Color.WHITE);
		// int top, int left, int bottom, int right
		this.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));

		this.alListForme = new CopyOnWriteArrayList<Forme>();
		this.formSelected = null;
		this.currentText = new String();
		this.colorSelected = Color.BLACK; // Par défaut : Couleur noir
		this.isEraserEnabled = false; // Par défaut : Gomme désactivé
		this.isFormFilledEnabled = false; // Par défaut : Forme vide
		PanelDessin.this.indexLastForm = -1;

		/*-------------------------*/
		/* Création des composants */
		/*-------------------------*/

		/*-----------PANEL-NORTH-----------*/
		JPanel panelNorth = new JPanel();
		panelNorth.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
		panelNorth.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
		this.lkdHSetObjects = new LinkedHashMap<String, JButton>();
		for (int cpt = 0; cpt < PanelDessin.TAB_OBJECTS.length; cpt++)
			this.lkdHSetObjects.put(PanelDessin.TAB_OBJECTS[cpt], PanelDessin.createJButton(PanelDessin.TAB_OBJECTS[cpt]));

		for (JButton btn : this.lkdHSetObjects.values()) {
			btn.setVerticalTextPosition(SwingConstants.BOTTOM);
			btn.setHorizontalTextPosition(SwingConstants.CENTER);
		}

		/*-----------PANEL-CENTER-----------*/
		this.panelZoneDessin = new PanelZoneDessin();

		/*-----------PANEL-SOUTH-----------*/
		JPanel panelSouth = new JPanel();
		panelSouth.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
		panelSouth.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
		this.lkdHSetColors = new LinkedHashMap<String, JButton>();
		for (int cpt = 0; cpt < TAB_NAMES_COLORS.length; cpt++)
			this.lkdHSetColors.put(PanelDessin.TAB_NAMES_COLORS[cpt], new JButton());

		this.tabPanelColors = new JPanel[PanelDessin.TAB_NAMES_COLORS.length];
		int cpt = 0;
		for (JButton btn : this.lkdHSetColors.values()) {
			btn.setVerticalTextPosition(SwingConstants.BOTTOM);
			btn.setHorizontalTextPosition(SwingConstants.CENTER);
			btn.setBackground(PanelDessin.TAB_COLORS[cpt]);
			btn.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
			btn.setPreferredSize(new Dimension(30, 20));

			this.tabPanelColors[cpt] = new JPanel();
			this.tabPanelColors[cpt].add(btn);
			Border border = BorderFactory.createEmptyBorder();
			border = BorderFactory.createTitledBorder(border, PanelDessin.TAB_NAMES_COLORS[cpt], TitledBorder.CENTER,
					TitledBorder.BELOW_BOTTOM);
			this.tabPanelColors[cpt].setBorder(border);
			cpt++;
		}
		/*-----------PANEL-SOUTH-----------*/

		/*-------------------------------*/
		/* Positionnement des composants */
		/*-------------------------------*/

		/*-----------PANEL-NORTH-----------*/
		for (JButton btn : this.lkdHSetObjects.values())
			panelNorth.add(btn);

		/*-----------PANEL-SOUTH-----------*/
		for (JPanel panelColors : this.tabPanelColors)
			panelSouth.add(panelColors);

		this.add(panelNorth, BorderLayout.NORTH);
		this.add(panelZoneDessin, BorderLayout.CENTER);
		this.add(panelSouth, BorderLayout.SOUTH);

		/*---------------------------*/
		/* Activation des composants */
		/*---------------------------*/

		for (JButton btn : this.lkdHSetObjects.values())
			btn.addActionListener(this);

		for (JButton btn : this.lkdHSetColors.values())
			btn.addActionListener(this);

		this.setVisible(true);

		this.emis = null;
	}

	/*-------------------------------------------------*/
	/* Évènement Action lors d'un clique sur un bouton */
	/*-------------------------------------------------*/

	/**
	 * Si l'utilisateur clique sur le bouton.
	 * 
	 * @param e : L' objet évènement détecté.
	 */
	public void actionPerformed(ActionEvent e) {
		// Gestion du bouton des actions (dessiner une forme, gomme, undo, plein ou
		// vide)
		for (Map.Entry<String, JButton> elementForm : this.lkdHSetObjects.entrySet()) {
			if (e.getSource() == elementForm.getValue()) {
				String sForm = elementForm.getValue().getText();

				// Forme à dessiner : carré - rond - flèche - texte
				if (sForm.equals("carré") || sForm.equals("rond") || sForm.equals("flèche") || sForm.equals("texte")) {
					if (sForm.equals("texte")) {
						this.currentText = JOptionPane.showInputDialog(this, "Entrer votre texte :");
						if (this.currentText == null || currentText.length() == 0 || this.currentText.equals(""))
							this.currentText = "Texte";
					}

					Forme fTmp = this.getCorrespondingForm(elementForm.getValue().getText());
					if (fTmp != null)
						this.formSelected = fTmp;

					this.isEraserEnabled = false;
				}
				// Gomme
				else if (sForm.equals("gomme")) {
					this.isEraserEnabled = true;
					this.formSelected = null;
				}
				// Undo
				else if (sForm.equals("undo")) {
					this.isEraserEnabled = false;
					this.formSelected = null;

					// undo permet a l’utilisateur de supprimer le dernier élément qu’il a lui-même
					// ajouté
					// (ce n’est pas forcement le dernier élément qui a eté ajouté à l’espace
					// partagée)
					int cpt = 1;
					for (Forme element : PanelDessin.this.alListForme) {
						if (cpt == PanelDessin.this.alListForme.size()) {
							// Envoie aux utilisateurs le numéro de la forme à supprimer
							PanelDessin.this.objectSend(new String("DES-S;" + element.getNumForme()));
							break;
						}
						cpt++;
					}
				} else if (sForm.equals("plein")) // Forme(Carré/Rond) plein ou vide
				{
					isFormFilledEnabled = true;
					this.isEraserEnabled = false;
				} else if (sForm.equals("vide")) {
					isFormFilledEnabled = false;
					this.isEraserEnabled = false;
				}

				// System.out.println( elementForm.getValue().getText() );
				break;
			}
		}

		// Gestion du bouton des couleurs (utilisateur du for pour correspondre un texte
		// à une couleur car le button n'a pas de texte, c'est un title-border)
		Collection<JButton> colBtns = this.lkdHSetColors.values();
		Object[] tabBtns = colBtns.toArray();
		for (int cptColor = 0; cptColor < tabBtns.length; cptColor++) {
			if (e.getSource() == tabBtns[cptColor]) {
				Color cTmp = this.getCorrespondingColor(PanelDessin.TAB_NAMES_COLORS[cptColor]);
				if (cTmp != null)
					this.colorSelected = cTmp;
				break;
			}
		}
	}

	/**
	 * @return Retourne l'objet Forme correspondant au texte du bouton.
	 * @param nom : Le texte du bouton qui est le nom de la forme.
	 */
	private Forme getCorrespondingForm(String nom) {
		if (nom.equals("") || nom.length() == 0 || nom == null)
			return null;
		if (nom.equals("carré"))
			return new Carre();
		else if (nom.equals("rond"))
			return new Cercle();
		else if (nom.equals("flèche"))
			return new Fleche();
		else if (nom.equals("texte"))
			return new Texte();
		return null;
	}

	/**
	 * @return Retourne l'objet Color correspondant au texte du bouton.
	 * @param nom : Le texte du bouton qui est le nom de la couleur.
	 */
	private Color getCorrespondingColor(String nom) {
		if (nom.equals("") || nom.length() == 0 || nom == null)
			return null;
		for (int cpt = 0; cpt < PanelDessin.TAB_NAMES_COLORS.length; cpt++)
			if (nom.equals(PanelDessin.TAB_NAMES_COLORS[cpt]))
				return PanelDessin.TAB_COLORS[cpt];
		return null;
	}

	/**
	 * Méthode pour créer un JButton stylisé.
	 * 
	 * @param texte : Le texte du JButton. src =
	 *              https://stackoverflow.com/questions/1839074/howto-make-jbutton-with-simple-flat-style
	 */
	private static JButton createJButton(String texte) {
		JButton btn = new JButton(texte);
		btn.setBackground(Color.WHITE);
		btn.setForeground(Color.BLACK);
		Border line = new LineBorder(Color.BLACK);
		Border margin = new EmptyBorder(3, 3, 3, 3);
		Border compound = new CompoundBorder(line, margin);
		btn.setBorder(compound);
		btn.setFont(new Font("Arial", Font.BOLD, 12));
		btn.setFocusable(false);
		btn.setMargin(new Insets(0, 0, 0, 0));
		return btn;
	}

	private class PanelZoneDessin extends JPanel {
		
		private static final long serialVersionUID = 1L;
		
		private int xDeb;
		private int yDeb;
		private int xFin;
		private int yFin;

		public PanelZoneDessin() {
			super();
			this.xDeb = 0;
			this.yDeb = 0;
			this.xFin = 0;
			this.yFin = 0;
			/*---------------------------*/
			/* Informations sur le Panel */
			/*---------------------------*/
			this.setLayout(null);

			/*-------------------------*/
			/* Activation du composant */
			/*-------------------------*/
			GereSouris souris = new GereSouris();
			this.addMouseListener(souris);
			this.addMouseMotionListener(souris);

			this.setVisible(true);
		}

		/*---------------*/
		/* Modificateurs */
		/*---------------*/

		public void setPosDebut(int x, int y) {
			this.xDeb = x;
			this.yDeb = y;
		}

		public void setPosFin(int x, int y) {
			this.xFin = x;
			this.yFin = y;
		}

		private void initValues() {
			this.xDeb = 0;
			this.yDeb = 0;
			this.xFin = 0;
			this.yFin = 0;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			for (Forme element : PanelDessin.this.alListForme) {
				if (element instanceof Carre)
					this.dessinerCarre(g2, element.getColor(), ((Carre) (element)).isFormFilled(),
							((Carre) (element)).getXDeb(), ((Carre) (element)).getYDeb(), ((Carre) (element)).getXFin(),
							((Carre) (element)).getYFin());
				else if (element instanceof Cercle)
					this.dessinerCercle(g2, element.getColor(), ((Cercle) (element)).isFormFilled(),
							((Cercle) (element)).getX(), ((Cercle) (element)).getY(),
							((Cercle) (element)).getDiametre(), ((Cercle) (element)).getDiametre());
				else if (element instanceof Fleche)
					this.dessinerFleche(g2, element.getColor(), ((Fleche) (element)).getXDeb(),
							((Fleche) (element)).getYDeb(), ((Fleche) (element)).getXFin(),
							((Fleche) (element)).getYFin(), ((Fleche) (element)).getLargeur(),
							((Fleche) (element)).getHauteur());
				else if (element instanceof Texte)
					this.dessinerTexte(g2, element.getColor(), ((Texte) (element)).getTexte(),
							((Texte) (element)).getX(), ((Texte) (element)).getY());
			}
			try {
				Thread.sleep((long) 10);
			} catch (Exception e) {
			}

			g2.dispose();

			// System.out.println("[paintComponent] Taille d'objets : " +
			// PanelDessin.this.alListForme.size());

		}

		/*----------------------------------------------------------*/
		/* Méthode de déssin utilisé dans la méthode paintComponent */
		/*----------------------------------------------------------*/

		// source : https://stackoverflow.com/questions/59144101/java-drawrect-problems
		private void dessinerCarre(Graphics2D g2, Color color, boolean isFormFilledEnabled, int x, int y, int x2,
				int y2) {
			int px = Math.min(x, x2);
			int py = Math.min(y, y2);
			int pw = Math.abs(x - x2);
			int ph = Math.abs(y - y2);
			g2.setColor(color);
			if (!isFormFilledEnabled)
				g2.drawRect(px, py, pw, ph);
			else
				g2.fillRect(px, py, pw, ph);
		}

		/**
		 * source : https://stackoverflow.com/questions/59144101/java-drawrect-problems
		 */
		private void dessinerCercle(Graphics2D g2, Color color, boolean isFormFilledEnabled, int x, int y, int largeur,
				int hauteur) {
			g2.setColor(color);
			// int rayon = (int) (largeur / 2);
			if (!isFormFilledEnabled)
				g2.drawOval(x, y, largeur, hauteur);
			else
				g2.fillOval(x, y, largeur, hauteur);
		}

		/**
		 * source :
		 * https://stackoverflow.com/questions/2027613/how-to-draw-a-directed-arrow-line-in-java
		 * Draw an arrow line between two points.
		 * 
		 * @param g  the graphics component.
		 * @param x1 x-position of first point.
		 * @param y1 y-position of first point.
		 * @param x2 x-position of second point.
		 * @param y2 y-position of second point.
		 * @param d  the width of the arrow.
		 * @param h  the height of the arrow.
		 */
		private void dessinerFleche(Graphics2D g2, Color color, int x1, int y1, int x2, int y2, int d, int h) {
			int dx = x2 - x1, dy = y2 - y1;
			double D = Math.sqrt(dx * dx + dy * dy);
			double xm = D - d, xn = xm, ym = h, yn = -h, x;
			double sin = dy / D, cos = dx / D;

			x = xm * cos - ym * sin + x1;
			ym = xm * sin + ym * cos + y1;
			xm = x;

			x = xn * cos - yn * sin + x1;
			yn = xn * sin + yn * cos + y1;
			xn = x;

			int[] xpoints = { x2, (int) xm, (int) xn };
			int[] ypoints = { y2, (int) ym, (int) yn };

			g2.setColor(color);
			g2.drawLine(x1, y1, x2, y2);
			g2.fillPolygon(xpoints, ypoints, 3);
		}

		private void dessinerTexte(Graphics2D g2, Color color, String texte, int x, int y) {
			g2.setColor(color);
			g2.drawString(texte, x, y);
			// int largeurTexte = g2.getFontMetrics().stringWidth(text);
		}

		/**
		 * Classe privée GereSouris
		 */
		private class GereSouris extends MouseAdapter {
			// private int lastMouseX;
			// private int lastMouseY;

			public GereSouris() {
				super();

				// this.lastMouseX = 0;
				// this.lastMouseY = 0;
			}

			/*
			 * private void setLastMousePosition(int x, int y) { this.lastMouseX = x;
			 * this.lastMouseY = y; }
			 */

			/**
			 * Utilisateur appuye le bouton de la souris.
			 * 
			 * @param e : L'objet clique-souris qui s'est produit.
			 */
			public void mousePressed(MouseEvent e) {
				// System.out.println("Pressed sur le panel !");
				PanelZoneDessin.this.setPosDebut(e.getX(), e.getY());

				if (formSelected instanceof Carre) {
					Carre carre = (Carre) formSelected;
					carre.setX(PanelZoneDessin.this.xDeb);
					carre.setY(PanelZoneDessin.this.yDeb);
					carre.setFormFilled(PanelDessin.this.isFormFilledEnabled);
					carre.setColor(PanelDessin.this.colorSelected);

					PanelDessin.this.alListForme.add(carre);
					PanelDessin.this.objectSend(carre);
				} else if (formSelected instanceof Cercle) {
					Cercle cercle = (Cercle) formSelected;
					cercle.setX(PanelZoneDessin.this.xDeb - (cercle.getDiametre() / 2));
					cercle.setY(PanelZoneDessin.this.yDeb - (cercle.getDiametre() / 2));
					cercle.setFormFilled(PanelDessin.this.isFormFilledEnabled);
					cercle.setColor(PanelDessin.this.colorSelected);

					PanelDessin.this.alListForme.add(cercle);
					PanelDessin.this.objectSend(cercle);
				} else if (formSelected instanceof Fleche) {
					Fleche fleche = (Fleche) formSelected;
					fleche.setX(PanelZoneDessin.this.xDeb);
					fleche.setY(PanelZoneDessin.this.yDeb);
					fleche.setColor(PanelDessin.this.colorSelected);

					PanelDessin.this.alListForme.add(fleche);
					PanelDessin.this.objectSend(fleche);
				} else if (formSelected instanceof Texte) {
					Texte texte = (Texte) formSelected;
					texte.setX(PanelZoneDessin.this.xDeb);
					texte.setY(PanelZoneDessin.this.yDeb);
					texte.setTexte(PanelDessin.this.currentText);
					texte.setColor(PanelDessin.this.colorSelected);

					PanelDessin.this.alListForme.add(texte);
					PanelDessin.this.objectSend(texte);
				}

				// System.out.println("[mousePressed] Après : Nombre d'élements restants : " +
				// PanelDessin.this.alListForme.size());
			}

			/**
			 * Utilisateur enfonce le bouton de la souris.
			 * 
			 * @param e : L'objet clique-souris qui s'est produit.
			 */
			public void mouseDragged(MouseEvent e) {
				// Vérifie que la gomme n'est pas activé pour pouvoir supprimer l'ancien forme
				// dessiné
				if (!isEraserEnabled) {
					// Supprime l'ancien objet sauvegardé
					PanelDessin.this.objectSend(new String("DES-S"));
				}

				// System.out.println("[mouseDragged] Avant : Nombre d'élements restants : " +
				// PanelDessin.this.alListForme.size());

				// System.out.println("Dragged sur le panel !");
				PanelZoneDessin.this.setPosFin(e.getX(), e.getY());

				if (formSelected instanceof Carre) {
					Carre carre = (Carre) formSelected;
					carre.setXDeb(PanelZoneDessin.this.xDeb);
					carre.setYDeb(PanelZoneDessin.this.yDeb);
					carre.setXFin(PanelZoneDessin.this.xFin);
					carre.setYFin(PanelZoneDessin.this.yFin);
					carre.setFormFilled(PanelDessin.this.isFormFilledEnabled);
					carre.setColor(PanelDessin.this.colorSelected);
					PanelDessin.this.indexLastForm = carre.getNumForme();
					PanelDessin.this.alListForme.add(carre);
					PanelDessin.this.objectSend(carre);
				} else if (formSelected instanceof Cercle) {
					Cercle cercle = (Cercle) formSelected;
					cercle.setX(PanelZoneDessin.this.xDeb - (cercle.getDiametre() / 2));
					cercle.setY(PanelZoneDessin.this.yDeb - (cercle.getDiametre() / 2));
					cercle.setDiametre(cercle.getDiametre() + 1);
					cercle.setFormFilled(PanelDessin.this.isFormFilledEnabled);
					formSelected.setColor(PanelDessin.this.colorSelected);
					PanelDessin.this.indexLastForm = cercle.getNumForme();
					PanelDessin.this.alListForme.add(cercle);
					PanelDessin.this.objectSend(cercle);
				} else if (formSelected instanceof Fleche) {
					Fleche fleche = (Fleche) formSelected;
					fleche.setX(PanelZoneDessin.this.xDeb);
					fleche.setY(PanelZoneDessin.this.yDeb);
					fleche.setXFin(PanelZoneDessin.this.xFin);
					fleche.setYFin(PanelZoneDessin.this.yFin);
					fleche.setColor(PanelDessin.this.colorSelected);
					PanelDessin.this.indexLastForm = fleche.getNumForme();
					PanelDessin.this.alListForme.add(fleche);
					PanelDessin.this.objectSend(fleche);
				} else if (formSelected instanceof Texte) {
					Texte texte = (Texte) formSelected;
					texte.setX(PanelZoneDessin.this.xFin);
					texte.setY(PanelZoneDessin.this.yFin);
					texte.setTexte(PanelDessin.this.currentText);
					texte.setColor(PanelDessin.this.colorSelected);
					PanelDessin.this.indexLastForm = texte.getNumForme();
					PanelDessin.this.alListForme.add(texte);
					PanelDessin.this.objectSend(texte);
				}

				// System.out.println("[mouseDragged] Après : Nombre d'élements restants : " +
				// PanelDessin.this.alListForme.size());

				// Mis à jour du dessin
				// PanelZoneDessin.this.revalidate();
			}

			/**
			 * Utilisateur relâche le bouton de la souris.
			 * 
			 * @param e : L'objet clique-souris qui s'est produit.
			 */
			public void mouseReleased(MouseEvent e) {
				// Vérifie que la gomme n'est pas activé pour pouvoir supprimer l'ancien forme
				// dessiné
				if (!isEraserEnabled) {
					// Supprime l'ancien objet sauvegardé
					PanelDessin.this.objectSend(new String("DES-S"));
				}

				// System.out.println("[mouseReleased] Avant : Nombre d'élements restants : " +
				// PanelDessin.this.alListForme.size());

				// System.out.println("Released sur le panel !");
				PanelZoneDessin.this.setPosFin(e.getX(), e.getY());

				if (formSelected instanceof Carre) {
					Carre carre = (Carre) formSelected;
					// carre.setXDeb( PanelZoneDessin.this.xDeb );
					// carre.setYDeb( PanelZoneDessin.this.yDeb );
					carre.setXFin(PanelZoneDessin.this.xFin);
					carre.setYFin(PanelZoneDessin.this.yFin);
					carre.setFormFilled(PanelDessin.this.isFormFilledEnabled);
					carre.setColor(PanelDessin.this.colorSelected);

					if (Math.abs(carre.getXFin() - carre.getXDeb()) > 1
							&& Math.abs(carre.getYFin() - carre.getYDeb()) > 1) {
						PanelDessin.this.indexLastForm = carre.getNumForme();
						PanelDessin.this.alListForme.add(carre);
						PanelDessin.this.objectSend(carre);
					}

					// System.out.println("Création d'un nouveau carré(numéro "+
					// formSelected.getNumForme() +") :");
					// System.out.println("xDeb : " + carre.getXDeb());
					// System.out.println("yDeb : " + carre.getYDeb());
					// System.out.println("xFin : " + carre.getXFin());
					// System.out.println("yFin : " + carre.getYFin());

					// Réinitialisation de la forme, afin de dessiner une nouvelle forme carré
					formSelected = new Carre();
				} else if (formSelected instanceof Cercle) {
					Cercle cercle = (Cercle) formSelected;
					cercle.setX(PanelZoneDessin.this.xDeb - (cercle.getDiametre() / 2));
					cercle.setY(PanelZoneDessin.this.yDeb - (cercle.getDiametre() / 2));
					cercle.setDiametre(cercle.getDiametre() + 1);
					cercle.setFormFilled(PanelDessin.this.isFormFilledEnabled);
					cercle.setColor(PanelDessin.this.colorSelected);

					if (Math.abs(cercle.getX() + cercle.getDiametre()) > 1
							&& Math.abs(cercle.getY() + cercle.getDiametre()) > 1) {
						PanelDessin.this.indexLastForm = cercle.getNumForme();
						PanelDessin.this.alListForme.add(cercle);
						PanelDessin.this.objectSend(cercle);
					}

					/*
					 * System.out.println("Création d'un nouveau cercle(numéro "+
					 * formSelected.getNumForme() +") :"); System.out.println("x : " +
					 * cercle.getX()); System.out.println("y : " + cercle.getY());
					 * System.out.println("diametre : " + cercle.getDiametre());
					 */

					// Réinitialisation de la forme, afin de dessiner une nouvelle forme cercle
					formSelected = new Cercle();
				} else if (formSelected instanceof Fleche) {
					Fleche fleche = (Fleche) formSelected;
					fleche.setX(PanelZoneDessin.this.xDeb);
					fleche.setY(PanelZoneDessin.this.yDeb);
					fleche.setXFin(PanelZoneDessin.this.xFin);
					fleche.setYFin(PanelZoneDessin.this.yFin);
					fleche.setColor(PanelDessin.this.colorSelected);

					if (Math.abs(fleche.getX() - fleche.getXFin()) > 1
							&& Math.abs(fleche.getY() - fleche.getYFin()) > 1) {
						PanelDessin.this.indexLastForm = fleche.getNumForme();
						PanelDessin.this.alListForme.add(fleche);
						PanelDessin.this.objectSend(fleche);
					}

					// Réinitialisation de la forme, afin de dessiner une nouvelle forme fleche
					formSelected = new Fleche();
				} else if (formSelected instanceof Texte) {
					Texte texte = (Texte) formSelected;
					texte.setX(PanelZoneDessin.this.xFin);
					texte.setY(PanelZoneDessin.this.yFin);
					texte.setTexte(PanelDessin.this.currentText);
					texte.setColor(PanelDessin.this.colorSelected);
					PanelDessin.this.indexLastForm = texte.getNumForme();
					PanelDessin.this.alListForme.add(texte);
					PanelDessin.this.objectSend(texte);

					// Réinitialisation de la forme, afin de dessiner une nouvelle forme texte
					formSelected = new Texte();
				}

				// System.out.println("Nombre d'élements restants total : " +
				// PanelDessin.this.alListForme.size());

				// Réinitialisation des coordonées
				PanelZoneDessin.this.initValues();
				// Réinitialisation du dernier élement sélectionné
				PanelDessin.this.indexLastForm = -1;

			}

			/**
			 * Utilisateur clique(enfonce et relâche) le bouton de la souris.
			 * 
			 * @param e : L'objet clique-souris qui s'est produit.
			 */
			public void mouseClicked(MouseEvent e) {
				// System.out.println("Cliked ("+e.getX()+":"+e.getY()+")");

				// if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1)
				// System.out.println("Double Cliked ! ");

				// Si l'objet gomme est activé
				if (PanelDessin.this.isEraserEnabled) {
					int mouseX = e.getX();
					int mouseY = e.getY();

					// System.out.println("Cliked Gomme activé ("+mouseX+":"+mouseY+")");

					Forme fDelete = null;

					// Parcourir à la linkedHashMap à l'envers pour enlever les formes :
					// https://www.benchresources.net/how-to-iterate-through-linkedhashmap-in-reverse-order-in-java/
					CopyOnWriteArrayList<Forme> alFormKeys = PanelDessin.this.alListForme;
					// Retourne l'ordre des clés de alFormKeys
					Collections.reverse(alFormKeys);

					// Parcourt des formes enregistrées
					for (Forme form : alFormKeys) {
						// Si c'est un carré
						if (form instanceof Carre) {
							// System.out.println("Gomme activé sur Carré");

							Carre cTmp = (Carre) form;

							// System.out.print( "form==cTmp ? " );
							// System.out.println( form == cTmp );

							// System.out.print( "numForm ? " );
							// System.out.println( form.getNumForme() );

							// System.out.print( "mouseX"+"("+mouseX+")"+ ">=
							// cTmp.getXDeb("+cTmp.getXDeb()+")");
							// System.out.println( mouseX >= cTmp.getXDeb() );

							// System.out.print( "mouseX"+"("+mouseX+")"+ "<=
							// cTmp.getXFin("+cTmp.getXFin()+")");
							// System.out.println( mouseX <= cTmp.getXFin() );

							// System.out.print( "mouseY"+"("+mouseY+")"+ ">=
							// cTmp.getYDeb"+"("+cTmp.getYDeb()+")" );
							// System.out.println( mouseY >= cTmp.getYDeb() );

							// System.out.print( "mouseY"+"("+mouseY+")"+ "<=
							// cTmp.getYFin"+"("+cTmp.getYFin()+")" );
							// System.out.println( mouseY <= cTmp.getYFin() );

							// Diagonale de haut gauche vers le bas
							if (cTmp.getXDeb() < cTmp.getXFin() && cTmp.getYDeb() < cTmp.getYFin()) {
								if (mouseX >= cTmp.getXDeb() && mouseX <= cTmp.getXFin() && mouseY >= cTmp.getYDeb()
										&& mouseY <= cTmp.getYFin()) {
									// System.out.println("1er condition vérifier !");
									fDelete = form;
									break;
								}
							}
							// Diagonale du bas gauche vers le haut droit
							else if (cTmp.getXDeb() < cTmp.getXFin() && cTmp.getYDeb() > cTmp.getYFin()) {
								if (mouseX >= cTmp.getXDeb() && mouseX <= cTmp.getXFin() && mouseY <= cTmp.getYDeb()
										&& mouseY >= cTmp.getYFin()) {
									// System.out.println("2eme condition vérifier !");
									fDelete = form;
									break;
								}
							}
							// Diagonale du bas droit vers le haut gauche
							else if (cTmp.getXDeb() > cTmp.getXFin() && cTmp.getYDeb() > cTmp.getYFin()) {
								if (mouseX <= cTmp.getXDeb() && mouseX >= cTmp.getXFin() && mouseY <= cTmp.getYDeb()
										&& mouseY >= cTmp.getYFin()) {
									// System.out.println("3eme condition vérifier !");
									fDelete = form;
									break;
								}
							}
							// Diagonale du haut droit vers le bas gauche
							else if (cTmp.getXDeb() > cTmp.getXFin() && cTmp.getYDeb() < cTmp.getYFin()) {
								if (mouseX <= cTmp.getXDeb() && mouseX >= cTmp.getXFin() && mouseY >= cTmp.getYDeb()
										&& mouseY <= cTmp.getYFin()) {
									// System.out.println("4eme condition vérifier !");
									fDelete = form;
									break;
								}
							}
						}
						// Sinon si c'est un cercle
						else if (form instanceof Cercle) {
							// System.out.println("Gomme activé sur Cercle");

							Cercle cTmp = (Cercle) form;

							// System.out.print( "form==cTmp ? " );
							// System.out.println( form == cTmp );

							// System.out.print( "numForm ? " );
							// System.out.println( form.getNumForme() );

							// System.out.println("mouseX : " + mouseX);
							// System.out.println("mouseY : " + mouseY);

							// System.out.println("cTmp.getX() : " + cTmp.getX() + " != ? " + form.getX());
							// System.out.println("cTmp.getY() : " + cTmp.getY() + " != ? " + form.getY());
							// System.out.println("cTmp.getDiametre() : " + cTmp.getDiametre());
							// System.out.println("donc rayon : " + cTmp.getDiametre()/2);

							// Point inclus dans un cercle :
							// https://openclassrooms.com/forum/sujet/bonjour-251
							// Le centre du cercle, position X et Y
							int centreCercleX = cTmp.getX() + ((int) (cTmp.getDiametre() / 2));
							int centreCercleY = cTmp.getY() + ((int) (cTmp.getDiametre() / 2));

							// System.out.println( "centreCercleX : " + centreCercleX );
							// System.out.println( "centreCercleY : " + centreCercleY );
							int distance = (int) Math
									.sqrt(Math.pow(mouseX - centreCercleX, 2) + Math.pow(mouseY - centreCercleY, 2));
							int rayon = (int) ((cTmp.getDiametre()) / 2);
							// System.out.println("distance : " + distance);
							// System.out.println("rayon : " + rayon);
							if (distance <= rayon) {
								fDelete = form;
								// System.out.println("Sûppresion Cercle");
								break;
							}
						} else if (form instanceof Fleche) {
							// System.out.println("Forme cercle");
							Fleche fTmp = (Fleche) form;

							// Pour vérifier si le point appartient à la droite de la flèche, on vérifie si
							// c'est colinéaire
							int xA = fTmp.getXDeb(); // xDeb
							int yA = fTmp.getYDeb(); // yDeb

							int xB = fTmp.getXFin(); // xFin
							int yB = fTmp.getYFin(); // xFin

							int xAB = xB - xA;
							int yAB = yB - yA;

							int xAM = mouseX - xA;
							int yAM = mouseY - yA;

							// System.out.println( (xAB*yAM) );
							// System.out.println( (yAB*xAM) );
							// System.out.println( (xAB*yAM) == (yAB*xAM) );

							// Produit en crois de (xAB*yAM) == (yAB*xAM)
							int produitCroix1 = Math.abs((xAB * yAM));
							int produitCroix2 = Math.abs((yAB * xAM));

							// Différence de 200 maximum est toléré entre ProduitCroix1 et ProduitCroix2
							int pas = 200;
							if (Math.abs(produitCroix1 - produitCroix2) <= pas) {
								fDelete = form;
								break;
							}
						} else if (form instanceof Texte) {
							Texte tTmp = (Texte) form;

							int pasGraphic = 7;
							int largeurTexte = tTmp.getTexte().length() + pasGraphic;

							int marge = 10;

							if (mouseX >= tTmp.getX() - marge && mouseX <= (tTmp.getX() + largeurTexte + marge)
									&& mouseY <= (tTmp.getY() + pasGraphic + marge)
									&& mouseY >= (tTmp.getY() - marge)) {
								fDelete = form;
								// System.out.println("Suppression du texte");
								break;
							}
						}
					}

					// System.out.println("[GOMME] fDelete != null ? (taille:" +
					// PanelDessin.this.alListForme.size() + ")");
					if (fDelete != null) {
						// System.out.println(fDelete.toString());
						// System.out.println("[GOMME] fDelete != null ? true");
						// System.out.println("Taille de la liste avant ? " +
						// PanelDessin.this.alListForme.size());
						// Envoie aux utilisateurs le numéro de la forme à supprimer
						PanelDessin.this.objectSend(new String("DES-S;" + fDelete.getNumForme()));
						// System.out.println("Taille de la liste après ? " +
						// PanelDessin.this.alListForme.size());
					}
				}
			}
		}
	}

	/*------------------------------------------*/
	/* RÉSEAU */
	/*------------------------------------------*/

	public void setEmis(ThreadEmission emis) {
		this.emis = emis;
	}

	public CopyOnWriteArrayList<Forme> getLstForme() {
		return this.alListForme;
	}

	/**
	 * Réception d'un objet
	 * 
	 * @param obj      : L'objet.
	 * @param register : Enregistrer ou non l'objet dans la liste des formes.
	 */
	public void objectReceived(Object obj, boolean register) {
		// System.out.println("[objectReceived] Réception d'un objet...");

		if (register) {
			if (obj.getClass().getSuperclass().getName().equals("Forme")) {
				Forme fm = (Forme) obj;

				// System.out.println(fm.toString());

				if (fm instanceof Carre) {
					Carre carre = (Carre) fm;
					carre.setXDeb(carre.getXDeb());
					carre.setYDeb(carre.getYDeb());
					carre.setXFin(carre.getXFin());
					carre.setYFin(carre.getYFin());
					carre.setFormFilled(carre.isFormFilled());
					carre.setColor(carre.getColor());
					PanelDessin.this.indexLastForm = carre.getNumForme();
					PanelDessin.this.alListForme.add((carre));
				} else if (fm instanceof Cercle) {
					Cercle cercle = (Cercle) fm;
					cercle.setX(cercle.getX());
					cercle.setY(cercle.getY());
					cercle.setDiametre(cercle.getDiametre());
					cercle.setFormFilled(cercle.isFormFilled());
					cercle.setColor(cercle.getColor());
					PanelDessin.this.indexLastForm = cercle.getNumForme();
					PanelDessin.this.alListForme.add((cercle));
				} else if (fm instanceof Fleche) {
					Fleche fleche = (Fleche) fm;
					fleche.setX(fleche.getX());
					fleche.setY(fleche.getY());
					fleche.setXFin(fleche.getXFin());
					fleche.setYFin(fleche.getYFin());
					fleche.setColor(fleche.getColor());
					PanelDessin.this.indexLastForm = fleche.getNumForme();
					PanelDessin.this.alListForme.add(fleche);
				} else if (fm instanceof Texte) {
					Texte texte = (Texte) fm;
					texte.setX(texte.getX());
					texte.setY(texte.getY());
					texte.setTexte(texte.getTexte());
					texte.setColor(texte.getColor());
					PanelDessin.this.indexLastForm = texte.getNumForme();
					PanelDessin.this.alListForme.add(texte);
				}
			}
		}
		// Mis à jour du dessin
		PanelDessin.this.panelZoneDessin.repaint();
	}

	/**
	 * Envoie d'un objet.
	 * @param obj : L'objet.
	 */
	public void objectSend(Object obj) {
		if (obj != null) {
			this.emis.emission(obj);
		}
	}

	/**
	 * Supprime la dernière forme enregistrée.
	 */
	public void deleteLastSaved() {
		if (PanelDessin.this.indexLastForm != -1 && PanelDessin.this.alListForme.size() > 0) {
			// Supprime la forme précédente enregistrée
			Forme fDelete = PanelDessin.this.alListForme.get(PanelDessin.this.alListForme.size() - 1);
			if (fDelete != null)
				PanelDessin.this.alListForme.remove(fDelete);
			// System.out.println("[deleteLastSaved] Après : Nombre d'élements restants : "
			// + PanelDessin.this.alListForme.size());
		}
		// Mis à jour du dessin
		PanelDessin.this.panelZoneDessin.repaint();
	}

}
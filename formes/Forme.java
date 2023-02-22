package formes;
import java.awt.Color;
import java.io.Serializable;

/**
 * Classe Abstraite Forme sérialisable
 * @version : 1.0
 * @date : 10/03/2021
 */

public abstract class Forme implements Serializable {
	/**
	 * Définition du serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private static int nbForme;
	private int numForme;

	private int posX; // équivalent à xDeb pour les formes à deux points
	private int posY; // équivalent à yDeb pour les formes à deux points

	private Color color;
	private boolean isFormFilled;

	/**
	 * Constructeur Forme
	 */
	public Forme() {
		this.numForme = Forme.nbForme++;
		this.posX = 0;
		this.posY = 0;
		this.color = Color.BLACK;
		this.isFormFilled = false;
	}

	/*------------*/
	/* Accesseurs */
	/*------------*/
	public int getNumForme() {
		return this.numForme;
	}

	public int getX() {
		return this.posX;
	}

	public int getY() {
		return this.posY;
	}

	public Color getColor() {
		return this.color;
	}

	public boolean isFormFilled() {
		return this.isFormFilled;
	}

	/*---------------*/
	/* Modificateurs */
	/*---------------*/

	public void setX(int posX) {
		this.posX = posX;
	}

	public void setY(int posY) {
		this.posY = posY;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setFormFilled(boolean bFilled) {
		this.isFormFilled = bFilled;
	}

	/*-----------*/
	/* Affichage */
	/*-----------*/
	public String toString() {
		return "Forme " + this.numForme + "(" + this.posX + ":" + this.posY + ")";
	}
}
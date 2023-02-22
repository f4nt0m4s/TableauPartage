package formes;
import java.io.Serializable;
import java.awt.Color;

/**
 * Classe Texte sérialisable
 * @version : 1.0 
 * @date : 10/03/2021
 */

public class Texte extends Forme implements Serializable {
	/**
	 * Définition du serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private final String TEXTE_DEFAUT = "Aucun texte";
	private String texte;

	public Texte() {
		super();
		this.texte = TEXTE_DEFAUT;
	}

	public int getX() {
		return super.getX();
	}

	public int getY() {
		return super.getY();
	}

	public String getTexte() {
		return this.texte;
	}

	public Color getColor() {
		return super.getColor();
	}

	public void setX(int x) {
		super.setX(x);
	}

	public void setY(int y) {
		super.setY(y);
	}

	public void setTexte(String texte) {
		this.texte = texte;
	}

	public void setColor(Color color) {
		super.setColor(color);
	}

	public boolean equals(Texte texte) {
		if (texte == null)
			return false;
		if (texte == Texte.this)
			return true;
		return (this.getX() == texte.getX()) && (this.getY() == texte.getY())
				&& (this.getTexte().equals(texte.getTexte()))
				&& (this.getColor().getRGB() == texte.getColor().getRGB());
	}
}
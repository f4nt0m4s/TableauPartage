package formes;
import java.io.Serializable;
import java.awt.Color;

/**
	* Classe Fleche sérialisable
	* @author 	: -
	* @version 	: 1.0
	* date 		: 08/03/2021
*/

public class Fleche extends Forme implements Serializable
{
	/**
		* Définition du serialVersionUID
	*/
	private static final long serialVersionUID = 1L;

	private final int VALEUR_PAR_DEFAUT = 5;
	private int largeur;
	private int hauteur;

	private int xFin;
	private int yFin;

	public Fleche()
	{
		super();

		this.largeur = this.VALEUR_PAR_DEFAUT;
		this.hauteur = this.VALEUR_PAR_DEFAUT;

		this.xFin = 0;
		this.yFin = 0;
	}

	public int getXDeb() { return super.getX(); 	}
	public int getYDeb() { return super.getY(); 	}
	public int getXFin() { return this.xFin; 		}
	public int getYFin() { return this.yFin;		}
	public int getLargeur() { return this.largeur; }
	public int getHauteur() { return this.hauteur; }
	public Color getColor() { return super.getColor(); 	}

	public void setXDeb(int xDeb) { super.setX(xDeb); }
	public void setYDeb(int yDeb) { super.setY(yDeb); }
	public void setXFin(int xFin) { this.xFin = xFin; }
	public void setYFin(int yFin) { this.yFin = yFin; }
	public void setColor(Color color) { super.setColor(color); }

	public boolean equals(Fleche fleche)
	{
		if ( fleche == null )
			return false;

		if ( fleche == Fleche.this )
			return true;

		return (this.getXDeb() == fleche.getXDeb()) && (this.getYDeb() == fleche.getYDeb()) &&
				(this.getXFin() == fleche.getXFin()) && (this.getYFin() == fleche.getYFin()) &&
				(this.getLargeur() == fleche.getLargeur()) && (this.getHauteur() == fleche.getHauteur()) &&
				(this.getColor().getRGB() == fleche.getColor().getRGB()) && (this.isFormFilled() == fleche.isFormFilled());
	}
}
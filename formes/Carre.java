package formes;
import java.io.Serializable;
import java.awt.Color;

/**
 * Classe Carre sérialisable
 * @version : 1.0 date 
 * @date : 08/03/2021
 */

public class Carre extends Forme implements Serializable {

	private static final long serialVersionUID = 1L;
	private int xFin;
	private int yFin;

	public Carre() {
		super();
		this.xFin = 0;
		this.yFin = 0;
	}

	public int getXDeb() {
		return super.getX();
	}

	public int getYDeb() {
		return super.getY();
	}

	public int getXFin() {
		return this.xFin;
	}

	public int getYFin() {
		return this.yFin;
	}

	public Color getColor() {
		return super.getColor();
	}

	public boolean isFormFilled() {
		return super.isFormFilled();
	}

	public void setXDeb(int xDeb) {
		super.setX(xDeb);
	}

	public void setYDeb(int yDeb) {
		super.setY(yDeb);
	}

	public void setXFin(int xFin) {
		this.xFin = xFin;
	}

	public void setYFin(int yFin) {
		this.yFin = yFin;
	}

	public void setColor(Color color) {
		super.setColor(color);
	}

	public void setFormFilled(boolean bFilled) {
		super.setFormFilled(bFilled);
	}

	public boolean equals(Carre carre) {
		if (carre == null)
			return false;
		if (carre == Carre.this)
			return true;
		return (this.getXDeb() == carre.getXDeb()) && (this.getYDeb() == carre.getYDeb())
				&& (this.getXFin() == carre.getXFin()) && (this.getYFin() == carre.getYFin())
				&& (this.getColor().getRGB() == carre.getColor().getRGB())
				&& (this.isFormFilled() == carre.isFormFilled());
	}

	public String toString() {
		return super.toString() + " - (" + this.xFin + ":" + this.yFin + ")";
	}
}
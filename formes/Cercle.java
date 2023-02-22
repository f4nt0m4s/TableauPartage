package formes;
import java.io.Serializable;
import java.awt.Color;

/**
	* Classe Cercle sérialisable
	* @author 	: -
	* @version 	: 1.0
	* @date 	: 08/03/2021
*/

public class Cercle extends Forme implements Serializable
{
	/**
		* Définition du serialVersionUID
	*/
	private static final long serialVersionUID = 1L;
	
	private int diametre;

	public Cercle()
	{
		super();

		this.diametre = 0;
	}

	public int getX() 				{ return super.getX();	}
	public int getY() 				{ return super.getY();	}
	public int getDiametre()		{ return this.diametre; }
	public Color getColor() 		{ return super.getColor(); 	}
	public boolean isFormFilled() 	{ return super.isFormFilled(); }

	public void setX(int x) 					{ super.setX(x); 				}
	public void setY(int y) 					{ super.setY(y); 				}
	public void setDiametre(int diametre)		{ this.diametre = diametre;		}
	public void setColor(Color color) 			{ super.setColor(color); 		}
	public void setFormFilled(boolean bFilled) 	{ super.setFormFilled(bFilled); }

	public boolean equals(Cercle cercle)
	{
		if ( cercle == null )
			return false;

		if ( cercle == Cercle.this )
			return true;

		return (this.getX() == cercle.getX()) && (this.getY() == cercle.getY()) &&  (this.getDiametre() == cercle.getDiametre()) &&
				(this.getColor().getRGB() == cercle.getColor().getRGB()) && (this.isFormFilled() == cercle.isFormFilled());
	}
}
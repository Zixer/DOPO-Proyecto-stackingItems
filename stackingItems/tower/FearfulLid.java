package tower;
import Shapes.*;

/**
 * Representa una tapa que depende de su copa asociada para poder entrar
 * o salir de la torre.
 */
public class FearfulLid extends Lid
{
    private Rectangle mark; 
    
    /**
     * Constructor for objects of class Fearful
     */
    public FearfulLid(int number, String color){
        super(number,color);
        mark = new Rectangle();
    }

    /**
     * Determina si la tapa puede entrar en la torre.
     *
     * Solo puede hacerlo si su copa asociada existe y está en la torre.
     */
    @Override
    public boolean canEnter(Tower tower) {
        return getPartnerCup() != null && tower.containsCup(getPartnerCup());
    }

    public void erase() {
        if (lid != null) lid.makeInvisible();  
        if (mark!= null) mark.makeInvisible();
    }
    
    @Override
    public boolean canExit(Tower tower) {
        return tower.isCoveringPartner(this);
    }
    
    /**
     * Hace visible la tapa y fuerza su redibujado con decoración.
     */
    @Override
    public void makeVisible() {
        super.makeVisible();
        draw();
    }
    
    /**
     * Dibuja la tapa junto con su marca decorativa.
     */
    public void draw() {
        if (!isVisible()) return;
        erase();
    
        lid = new Rectangle();
        lid.changeSize(getHeight(), getWidth());
        lid.changeColor(getColor());
        lid.changeP(getXpo(), getYpo() - getHeight());
        lid.makeVisible();
    
        
        mark.changeSize(2, getWidth() / 2);
        mark.changeColor("lightYellow");
        mark.changeP(getXpo() + (getWidth() / 4), getYpo()- getHeight() + 1);
        mark.makeVisible();
        
    }
}
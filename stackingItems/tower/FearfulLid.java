package tower;
import Shapes.*;

/**
 * Write a description of class Fearful here.
 *
 * @author (your name)
 * @version (a version number or a date)
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
    
    public void draw() {
        if (!isVisible()) return;
    
        erase();
    
        lid = new Rectangle();
        lid.changeSize(getHeight(), getWidth());
        lid.changeColor(getColor());
        lid.changeP(getXpo(), getYpo() - getHeight());
        lid.makeVisible();
    
        if (hasDecoration()) {
            mark.changeSize(2, getWidth() / 2);
            mark.changeColor(getSecondaryColor());
            mark.changeP(getXpo() + (getWidth() / 4), getYpo()- getHeight() + 1);
            mark.makeVisible();
        }
    }
}
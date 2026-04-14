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

    /**
     * Constructor for objects of class Fearful
     */
    public FearfulLid(int number, String color){
        super(number,color);
    }

    @Override
    public boolean canEnter(Tower tower) {
        return getPartnerCup() != null && tower.containsCup(getPartnerCup());
    }

    @Override
    public boolean canExit(Tower tower) {
        return tower.isCoveringPartner(this);
    }
    
    @Override
    public boolean hasDecoration() {
        return true;
    }
    
    @Override
    public String getSecondaryColor() {
        return "lightGreen";
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
            Rectangle mark = new Rectangle();
            mark.changeSize(2, getWidth() / 2);
            mark.changeColor(getSecondaryColor());
            mark.changeP(getXpo() + (getWidth() / 4), getYpo()- getHeight() + 1);
            mark.makeVisible();
        }
    }
}
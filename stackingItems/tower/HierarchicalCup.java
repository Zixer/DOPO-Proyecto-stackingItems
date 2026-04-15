package tower;
import Shapes.*;


/**
 * Write a description of class Hierarchical here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class HierarchicalCup extends Cup
{
    
    private Rectangle decoration1;
    private boolean deadlocked;
    
    /**
     * Constructor for objects of class Hierarchical
     */
    public HierarchicalCup (int number, String color) {
        super(number, color);
        deadlocked = false;
    }
    
    @Override
    public void beforeEnter(Tower tower) {
        tower.repositionForHierarchical(this);
    }
    
    public boolean isLocked() {
        return deadlocked;
    }

    public void lock() {
        deadlocked = true;
    }
    
    @Override
    public boolean canBeRemoved() {
        return !deadlocked;
    }
    
    @Override
    public void drawDecoration() {
        decoration1 = new Rectangle();
        decoration1.changeSize(3, getWidth()); 
        decoration1.changeColor("turquoise");     
        decoration1.changeP(getXpo(), getYpo() - 3); 
        decoration1.makeVisible();
    }
    
    public void erase() {
        if (body != null) body.makeInvisible();
        if (interior != null) interior.makeInvisible();
        if (decoration1 != null) decoration1.makeInvisible();
    }
}
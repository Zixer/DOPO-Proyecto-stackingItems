package Shapes;
import tower.*;

/**
 * Write a description of class Hierarchical here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class HierarchicalCup extends Cup
{
    
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
}
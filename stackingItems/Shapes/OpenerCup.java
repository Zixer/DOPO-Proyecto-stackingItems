package Shapes;
import tower.*;
import java.util.*;

/**
 * Write a description of class Opener here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class OpenerCup extends Cup{
    
    public OpenerCup(int number, String color) {
        super(number, color);
    }  
    
    @Override
    public void beforeEnter(Tower tower) {
        tower.removeBlockingLids(this);
    }
}
package tower;
import Shapes.*;
import java.util.List;

/**
 * Write a description of class OpenerCup here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class OpenerCup extends Cup{
    private Rectangle decoration1;
    private Rectangle decoration2;
    
    public OpenerCup(int number, String color) {
        super(number, color);
        decoration1 = new Rectangle();
        decoration2 = new Rectangle();
    }  
    
    @Override
    public void beforeEnter(Tower tower) {
        int index = tower.findCupIndex(getNumber());
        if (index == -1) return;
    
        Cup container = tower.findContainerBefore(index, this);
    
        if (container != null) {
            // 🔹 INSIDE
            List<Lid> lids = tower.getLidsBetween(container, this);
            tower.removeLids(lids);
        } else {
            // 🔹 OUTSIDE
            List<Lid> lids = tower.getOutsideBlockingLids(this, index);
            tower.removeLids(lids);
        }
    }
    
    @Override
    public void drawDecoration() {
        decoration1 = new Rectangle();
        decoration1.changeSize(getHeight(), 2);
        decoration1.changeColor("lightOrange");
        decoration1.changeP(getXpo() + 3, getYpo() - getHeight());
        decoration1.makeVisible();
    
        decoration2 = new Rectangle();
        decoration2.changeSize(getHeight(), 2);
        decoration2.changeColor("lightOrange");
        decoration2.changeP(getXpo() + getWidth() - 5, getYpo() - getHeight());
        decoration2.makeVisible();
    }
}
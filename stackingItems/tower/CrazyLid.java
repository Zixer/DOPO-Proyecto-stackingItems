package tower;
import Shapes.*;
import java.util.ArrayList;


/**
 * Write a description of class Crazy here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class CrazyLid extends Lid
{

    /**
     * Constructor for objects of class Crazy
     */
    public CrazyLid(int number, String color){
        super(number,color);
    }
    
    @Override
    public boolean shouldBeBeforeCup() {
        return true;
    }
    
    public void reorderInTower(Tower tower) {
        Cup partner = getPartnerCup();
        if (partner == null) return;
    
        ArrayList<String[]> order = tower.getInsertionOrder();
        int lidNumber = this.getNumber();
    
        int lidIndex = -1;
        int cupIndex = -1;
    
        for (int i = 0; i < order.size(); i++) {
            String[] elem = order.get(i);
    
            if (elem[0].equals("lid") &&
                elem[1].equals(String.valueOf(lidNumber))) {
                lidIndex = i;
            }
    
            if (elem[0].equals("cup") &&
                elem[1].equals(String.valueOf(partner.getNumber()))) {
                cupIndex = i;
            }
        }
    
        if (lidIndex == -1 || cupIndex == -1) return;
    
        // si ya está antes, no hacemos nada
        if (lidIndex < cupIndex) return;
    
        // mover la lid antes de la cup
        String[] lidElem = order.remove(lidIndex);
        order.add(cupIndex, lidElem);
    }
    
    @Override
    public boolean hasDecoration() {
        return true;
    }
    
    @Override
    public String getSecondaryColor() {
        return "lightPink";
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
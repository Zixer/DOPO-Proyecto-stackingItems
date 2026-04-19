package tower;
import Shapes.*;
import java.util.ArrayList;


/**
 * Representa una tapa que altera el orden de inserción para colocarse
 * antes de su copa asociada.
 */
public class CrazyLid extends Lid
{
    private Rectangle mark; 
    /**
     * Constructor for objects of class Crazy
     */
    public CrazyLid(int number, String color){
        super(number,color);
        mark = new Rectangle();
    }
    
    public void erase() {
        if (lid != null) lid.makeInvisible();  
        if (mark != null) mark.makeInvisible();
    }
    
    /**
     * Indica que esta tapa debe colocarse antes que su copa.
     */
    @Override
    public boolean shouldBeBeforeCup() {
        return true;
    }
    
    /**
     * Reordena la tapa dentro del orden de inserción para que quede
     * antes de su copa asociada.
     */
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
    
        if (lidIndex < cupIndex) return;
    
        String[] lidElem = order.remove(lidIndex);
        order.add(cupIndex, lidElem);
    }

    @Override
    public boolean actsAsBaseForPartner() {
        return true;
    }
    
    /**
     * Dibuja la tapa con una marca decorativa.
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
        mark.changeColor("lightOrange");
        mark.changeP(getXpo() + (getWidth() / 4), getYpo()- getHeight() + 1);
        mark.makeVisible();
    }

}
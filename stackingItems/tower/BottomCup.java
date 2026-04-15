package tower;
import Shapes.*;

public class BottomCup extends Cup {
    private Rectangle decoration;

    public BottomCup(int number, String color) {
        super(number, color);
    }

    @Override
    public void beforeEnter(Tower tower) {
        tower.moveCupToBottom(this);
    }

    public void erase() {
        if (body != null) body.makeInvisible();
        if (interior != null) interior.makeInvisible();
        if (decoration != null) decoration.makeInvisible();
    }
    
    @Override
    public void drawDecoration() {
        decoration = new Rectangle();
        decoration.changeSize(3, getWidth());
        decoration.changeColor("mint");
        decoration.changeP(getXpo(), getYpo() - 2);
        decoration.makeVisible();
    }
}
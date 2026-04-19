package tower;
import Shapes.*;

/**
 * Representa una copa especial que siempre se mueve al fondo
 * del orden de inserción antes de posicionarse en la torre.
 */
public class BottomCup extends Cup {
    private Rectangle decoration;

    public BottomCup(int number, String color) {
        super(number, color);
    }

    /**
     * Ejecuta la lógica previa a la inserción.
     *
     * Mueve la copa al inicio del orden de inserción, simulando que
     * queda en la base de la torre.
     *
     * @param tower torre donde se inserta la copa
     */
    @Override
    public void beforeEnter(Tower tower) {
        tower.moveCupToBottom(this);
    }

    /**
     * Elimina la representación visual de la copa.
     */
    public void erase() {
        if (body != null) body.makeInvisible();
        if (interior != null) interior.makeInvisible();
        if (decoration != null) decoration.makeInvisible();
    }
    
    /**
     * Dibuja la decoración característica de la BottomCup.
     *
     * Se representa como una línea horizontal en la parte superior.
     */
    @Override
    public void drawDecoration() {
        decoration = new Rectangle();
        decoration.changeSize(3, getWidth());
        decoration.changeColor("mint");
        decoration.changeP(getXpo(), getYpo() - 2);
        decoration.makeVisible();
    }
}
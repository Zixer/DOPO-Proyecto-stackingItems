package tower;
import Shapes.*;


/**
* Representa una copa jerárquica que puede cambiar su posición dentro
* del orden de inserción antes de ser colocada en la torre.
*
* Este tipo de copa puede quedar bloqueada (locked), impidiendo que sea removida.
*/
public class HierarchicalCup extends Cup
{
    
    private Rectangle decoration1;
    private boolean deadlocked;
    
    /**
     * Construye una nueva HierarchicalCup.
     *
     * Inicializa la copa con su número y color, y establece su estado
     * como desbloqueado.
     *
     * @param number tamaño de la copa
     * @param color color de la copa
     */
    public HierarchicalCup (int number, String color) {
        super(number, color);
        deadlocked = false;
    }
    
    /**
     * Ejecuta la lógica previa a la inserción de la copa en la torre.
     *
     * Reposiciona la copa dentro del orden de inserción según las reglas
     * jerárquicas definidas en la torre.
     *
     * @param tower torre donde se inserta la copa
     */
    @Override
    public void beforeEnter(Tower tower){
        tower.repositionForHierarchical(this);
    }
    
    /**
     * Indica si la copa se encuentra bloqueada.
     *
     * @return true si la copa no puede moverse o eliminarse
     */
    public boolean isLocked() {
        return deadlocked;
    }

    /**
     * Bloquea la copa, evitando que pueda ser removida posteriormente.
     */
    public void lock(){
        deadlocked = true;
    }
    
    /**
     * Determina si la copa puede ser removida de la torre.
     *
     * Una copa jerárquica no puede eliminarse si está bloqueada.
     *
     * @return true si puede ser removida, false en caso contrario
     */
    @Override
    public boolean canBeRemoved() {
        return !deadlocked;
    }
    
    /**
     * Dibuja la decoración de la copa jerárquica.
     *
     * Consiste en una franja horizontal en la parte superior de la copa.
     */
    @Override
    public void drawDecoration() {
        decoration1 = new Rectangle();
        decoration1.changeSize(3, getWidth()); 
        decoration1.changeColor("turquoise");     
        decoration1.changeP(getXpo(), getYpo() - 3); 
        decoration1.makeVisible();
    }
    
    /**
     * Elimina la representación visual completa de la copa.
     *
     * Oculta tanto el cuerpo como la decoración.
     */
    public void erase() {
        if (body != null) body.makeInvisible();
        if (interior != null) interior.makeInvisible();
        if (decoration1 != null) decoration1.makeInvisible();
    }
}
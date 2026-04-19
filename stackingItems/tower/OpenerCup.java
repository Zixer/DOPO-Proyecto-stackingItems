package tower;
import Shapes.*;
import java.util.List;

/**
 * Representa una copa especial de tipo "OpenerCup".
 *
 * Este tipo de copa tiene la capacidad de eliminar tapas que bloquean su
 * entrada en la torre antes de ser posicionada, permitiendo así abrir espacio
 * dentro de la estructura.
 */
public class OpenerCup extends Cup{
    private Rectangle decoration1;
    private Rectangle decoration2;
    
    public OpenerCup(int number, String color) {
        super(number, color);
        decoration1 = new Rectangle();
        decoration2 = new Rectangle();
    }  
  
    
    /**
     * Ejecuta la lógica previa a la inserción de la copa en la torre.
     *
     * Este método permite que la OpenerCup elimine cualquier tapa que impida
     * su correcta ubicación antes de ser posicionada en el layout.
     *
     * El comportamiento depende de si existe una copa contenedora previa:
     *
     * - Si existe una copa contenedora, se eliminan todas las tapas ubicadas
     *   entre dicha copa y la OpenerCup dentro del orden de inserción.
     *
     * - Si no existe una copa contenedora, se eliminan las tapas externas que
     *   bloquean la entrada de la OpenerCup en su posición actual.
     *
     * Esto garantiza que la copa pueda insertarse sin interferencias.
     *
     * @param tower torre en la cual se va a insertar la copa.
     */
    @Override
    public void beforeEnter(Tower tower) {
        int index = tower.findCupIndex(getNumber());
        if (index == -1) return;
    
        Cup container = tower.findContainerBefore(index, this);
    
        if (container != null) {
            List<Lid> lids = tower.getLidsBetween(container, this);
            tower.removeLids(lids);
        } else {
                       List<Lid> lids = tower.getOutsideBlockingLids(this, index);
            tower.removeLids(lids);
        }
    }
    
    /**
     * Oculta completamente la representación visual de la OpenerCup.
     *
     * Este método hace invisibles tanto el cuerpo principal de la copa como
     * su interior y los elementos de decoración asociados.
     *
     * Se utiliza principalmente durante procesos de redibujado para evitar
     * artefactos visuales antes de recalcular posiciones.
     */
    public void erase() {
        if (body != null) body.makeInvisible();
        if (interior != null) interior.makeInvisible();
        if (decoration1 != null) decoration1.makeInvisible();
        if (decoration2 != null) decoration2.makeInvisible();
    }
    
    /**
     * Dibuja la decoración característica de la OpenerCup.
     *
     * La decoración consiste en dos rectángulos verticales ubicados a los lados
     * de la copa, los cuales se posicionan en función de las coordenadas actuales
     * y dimensiones del objeto.
     *
     * Cada vez que se invoca este método, se crean nuevas instancias de los
     * elementos gráficos y se hacen visibles en pantalla.
     *
     * Este método es llamado como parte del proceso de renderizado de la copa.
     */
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
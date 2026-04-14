package tower; 
import Shapes.*;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import java.util.HashSet;

/**
 * Write a description of class Tower here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Tower
{
    private int maxWidth;
    private int maxHeight;
    private int Y = 200;
    private int X = 130;
    private boolean isVisible;
    private Stack<Cup> cups;
    private Stack<Lid> lids; 
    private boolean isOK;
    private Random random;
    private ArrayList<String[]> insertionOrder;
    private Map<Cup, Lid> topInsideLidByCup;
    private Cup outer;
    private Stack<Cup> insideStack;
    private int outsideSize;
    private int outsideBaseY;
    private int highestCupTopY;    
    private boolean lastOutsideWasLid;
    private List<String> availableColors;
    private HashSet<String> usedColors;
    private Lid topOutsideLid;
    private Map<Cup, Cup> parentByCup;
    
    /**
     * Construye una torre vacía con límites máximos de altura y ancho.
     *
     * @param nmaxHeight altura máxima permitida para la torre (suma de alturas externas).
     * @param nmaxWidth  tamaño máximo permitido para una copa (número/diámetro).
     */
    public Tower(int nmaxHeight, int nmaxWidth) {
        cups = new Stack<Cup>();
        lids = new Stack<Lid>();
        random = new Random();
        availableColors = new ArrayList<>(List.of("red","black","blue","yellow","green",
                                                  "magenta","orange","cyan",
                                                  "gray","pink","brown","purple","violet",
                                                  "indigo","gold","silver","lightYellow",
                                                  "lightBlue","lavender","mint","peach"));
        usedColors = new HashSet<>();
        
        insertionOrder = new ArrayList<String[]>();
        maxWidth = nmaxWidth;
        maxHeight = nmaxHeight;
        isVisible = false;
        isOK = true;
    
        parentByCup = new HashMap<Cup, Cup>();
        insideStack = new Stack<Cup>();
        topInsideLidByCup = new HashMap<Cup, Lid>();
    
        
        outer = null;
        outsideSize = -1;
        outsideBaseY = Y;
        highestCupTopY = Y;
        lastOutsideWasLid = false;
    }
    
    private String getUniqueColor() {
        for (String color : availableColors) {
            if (!usedColors.contains(color)) {
                usedColors.add(color);
                return color;
            }
        }
        return null;
    }
    
    private void releaseColor(String color) {
        usedColors.remove(color);
    }
    
    /**
     * Construye una torre inicial con una cantidad dada de copas.
     *
     * Las copas se crean con tamaños impares consecutivos:
     * 1, 3, 5, ..., 2n-1.
     * Después de crearlas, la torre se hace visible y se redibuja.
     *
     * @param numCups número de copas iniciales de la torre.
     */
    public Tower(int numCups) {
        this(numCups * 2, numCups * 10);
        availableColors = new ArrayList<>(List.of("magenta", "green", "yellow", "blue", "black","red", "orange", "cyan", "pink", "grey"));
        if (numCups!= 0){
            for (int i = 1; i <= numCups; i++) {
                int size = (2 * i) - 1;  
                cups.push(new NormalCup(size, randomColor()));
                insertionOrder.add(new String[]{"cup", String.valueOf(size)});
            }
        }else {
            this.isVisible = false;
        }      
    
        makeVisible();
        redraw();
    }

    /**
     * Genera y retorna un color aleatorio de una lista predefinida.
     *
     * @return un String con el nombre del color aleatorio.
     */
    private String randomColor(){
        random = new Random();
        List<String> lista = List.of("magenta", "green", "yellow", "blue", "black", "red", "orange", "cyan", "pink", "grey");
        return lista.get(random.nextInt(lista.size()));
    }
    
    /**
     * Agrega una nueva copa a la torre si cumple las restricciones definidas.
     *
     * Restricciones:
     * - El tamaño no debe estar repetido.
     * - El tamaño no debe ser mayor que el ancho máximo permitido.
     * - Si existe una tapa del mismo color, la copa debe tener el mismo tamaño.
     *
     * Efectos:
     * - Crea una nueva copa con color aleatorio.
     * - La inserta en la estructura de copas.
     * - Registra la inserción en insertionOrder.
     * - Redibuja la torre.
     * - Actualiza el estado de éxito de la operación.
     *
     * @param i tamaño o número de la copa a insertar.
     */
    public void pushCup(int i) {
        if (i > maxWidth || duplicatedSize(i)) {
            isOK = false;
            return;
        }
    
        Lid existingLid = null;
        for (Lid l : lids) {
            if (l.getNumber() == i && !l.hasPartnerCup()) {
                existingLid = l;
                break;
            }
        }
    
        Cup nueva;
        String color;
    
        if (existingLid != null) {
            color = existingLid.getColor();
            nueva = new NormalCup(i, color);
            nueva.addLid(existingLid);
        } else {
            color = getUniqueColor();
            if (color == null) {
                isOK = false;
                return;
            }
    
            nueva = new NormalCup(i, color);
            Lid partner = new NormalLid(i, color);
            nueva.addLid(partner);
        }
    
        cups.push(nueva);
        insertionOrder.add(new String[]{"cup", String.valueOf(i)});
        isOK = true;
    
        if (this.isVisible) {
            redraw();
        }
    }

    /**
     * Recalcula completamente la disposición visual de copas y tapas.
     *
     * El algoritmo recorre insertionOrder y decide, para cada elemento,
     * si debe ubicarse por fuera o por dentro de la torre según su tamaño
     * y el estado actual del layout.
     *
     * Efectos:
     * - Oculta temporalmente todos los elementos.
     * - Reinicia el estado auxiliar de layout.
     * - Reubica copas y tapas.
     * - Vuelve visibles los elementos en el orden de inserción.
     */
    public void redraw() {
        final int CANVAS_WIDTH = 300;
        final int GROSOR = 5;
        prepareCupsBeforeLayout();
        reorderSpecialLidsInPlace();
        hideAllElements();
        resetLayoutState();
    
        for (int i = 0; i < insertionOrder.size(); i++) {
            String tipo = insertionOrder.get(i)[0];
            int numero = Integer.parseInt(insertionOrder.get(i)[1]);
    
            if (isCup(tipo)) {
                Cup c = findCup(numero);
                if (c == null) continue;
    
                if (outer == null) {
                    if (topOutsideLid != null) {
                        c.placeAboveLid(topOutsideLid);
                    } else {
                        c.placeOutside(CANVAS_WIDTH, Y);
                    }
    
                    outer = c;
                    insideStack.clear();
                    outsideSize = c.getNumber();
                    outsideBaseY = c.getYpo() - c.getHeight();
                    lastOutsideWasLid = false;
                    topOutsideLid = null;
                    updateHighestCupTop(c);
                    c.makeVisible();
    
                } else {
                    boolean goesOutside = lastOutsideWasLid || (c.getNumber() >= outsideSize);
    
                    if (goesOutside) {
                        if (lastOutsideWasLid && topOutsideLid != null) {
                            c.placeAboveLid(topOutsideLid);
                        } else {
                            c.placeOutside(CANVAS_WIDTH, highestCupTopY);
                        }
                    
                        c.setInside(false);
                        parentByCup.remove(c);
                    
                        outer = c;
                        insideStack.clear();
                        outsideSize = c.getNumber();
                        outsideBaseY = c.getYpo() - c.getHeight();
                        lastOutsideWasLid = false;
                        topOutsideLid = null;
                        updateHighestCupTop(c);
                        c.makeVisible();
                    } else {
                        Cup container = findContainerForCup(c);
                        if (container == null) continue;
                    
                        Cup support = findSupportForCup(c, container);

                        if (support == null) {
                            c.placeInside(container, topInsideLidByCup.get(container), GROSOR);
                        } else {
                            c.placeAbove(support, container, topInsideLidByCup.get(support), GROSOR);
                        }
                        
                        c.setInside(true);
                        parentByCup.put(c, container);
                        insideStack.push(c);
                        updateHighestCupTop(c);
                    }
                }
    
                c.makeVisible();
    
            } else if (isLid(tipo)) {
                Lid l = findLid(numero);
                if (l == null) continue;
    
                if (outer == null) {
                    if (lastOutsideWasLid && topOutsideLid != null) {
                        l.placeAboveLid(topOutsideLid);
                    } else {
                        l.placeOutside(CANVAS_WIDTH, highestCupTopY);
                    }
    
                    l.setInside(false);
                    insideStack.clear();
                    outsideSize = l.getNumber();
                    outsideBaseY = l.getYpo() - l.getHeight();
                    lastOutsideWasLid = true;
                    topOutsideLid = l;
                    updateHighestTopWithLid(l);
                    l.makeVisible();
                } else {
                    Cup container = findContainerForLid(l);
                    boolean goesOutside = (container == null) || (l.getNumber() >= outsideSize);
    
                    if (goesOutside) {
                        if (topOutsideLid == null) {
                            l.placeOutside(CANVAS_WIDTH, highestCupTopY);
                        } else {
                            l.placeAboveLid(topOutsideLid);
                        }
    
                        l.setInside(false);
                        insideStack.clear();
                        outsideSize = l.getNumber();
                        outsideBaseY = l.getYpo() - l.getHeight();
                        lastOutsideWasLid = true;
                        topOutsideLid = l;
                        updateHighestTopWithLid(l);
                        l.makeVisible();
                    } else {
                        Cup support = findSupportForLid(l, container);
                        Lid topInside = topInsideLidByCup.get(container);
                        
                        if (container.getNumber() == l.getNumber()) {
                            l.placeOnCup(container, topInsideLidByCup.get(container));
                            topInsideLidByCup.put(container, l);
                        
                        } else if (support == null) {
                            l.placeInside(container, topInside, GROSOR);
                            topInsideLidByCup.put(container, l);
                        
                        } else {
                            l.placeAboveCup(support, container, topInsideLidByCup.get(support), GROSOR);
                            topInsideLidByCup.put(support, l);
                        }
                        
                        l.setInside(true);
                        l.makeVisible();
                        }
                }
    
                isOK = true;
            }
        }
    }
            
    private Cup findSupportForCup(Cup c) {
        Cup support = null;
    
        for (Cup inside : insideStack) {
            if (inside.getNumber() < c.getNumber()) {
                if (support == null || inside.getNumber() > support.getNumber()) {
                    support = inside;
                }
            }
        }
    
        return support;
    }
    
    /**
     * Oculta visualmente todas las copas y tapas de la torre.
     */
    private void hideAllElements() {
        for (Cup c : cups) c.makeInvisible();
        for (Lid l : lids) l.makeInvisible();
    }
    
    /**
     * Reinicia el estado auxiliar usado para calcular el layout visual de la torre.
     *
     * Efectos:
     * - Elimina la referencia a la copa externa actual.
     * - Vacía la pila de copas internas.
     * - Limpia el registro de tapas internas asociadas a cada copa.
     * - Restablece los valores de tamaño y posición externos.
     * - Reinicia la marca de si el último elemento externo fue una tapa.
     */
    private void resetLayoutState() {
        outer = null;
        insideStack.clear();
        topInsideLidByCup.clear();
    
        lastOutsideWasLid = false;
        topOutsideLid = null;
    
        outsideSize = 0;
        outsideBaseY = 0;
    
        highestCupTopY = Y;
        parentByCup.clear();
    }
    
    /**
     * Hace visibles las copas y tapas siguiendo el orden definido
     * en insertionOrder.
     */
    private void showAllElementsInInsertionOrder() {
        for (String[] item : insertionOrder) {
            if (isCup(item[0])) {
                int number = Integer.parseInt(item[1]);
                Cup c = findCup(number);
                if (c != null) c.makeVisible();
            }
        }
    
        for (String[] item : insertionOrder) {
            if (isLid(item[0])) {
                int number = Integer.parseInt(item[1]);
                Lid l = findLid(number);
                if (l != null) l.makeVisible();
            }
        }
    }
    
    private boolean isCup(String type) {
        return "cup".equals(type);
    }
    
    private boolean isLid(String type) {
        return "lid".equals(type);
    }
    
    /**
     * Actualiza la coordenada más alta ocupada por una copa o tapa en la torre.
     *
     * Toma la parte superior de la copa dada y, si está más arriba que la
     * registrada actualmente, actualiza highestCupTopY.
     *
     * @param c copa cuya posición superior se usa para actualizar el layout.
     */
    private void updateHighestCupTop(Cup c) {
        int top = c.getYpo() - c.getHeight();
        highestCupTopY = Math.min(highestCupTopY, top);
    }

    private Cup findSupportForLid(Lid l, Cup container) {
        Cup bestSupport = null;
    
        for (Cup inside : insideStack) {
            if (inside == container) {
                continue;
            }
    
            Cup parent = parentByCup.get(inside);
    
            if (parent == container && inside.getNumber() < l.getNumber()) {
                if (bestSupport == null || inside.getNumber() > bestSupport.getNumber()) {
                    bestSupport = inside;
                }
            }
        }
    
        return bestSupport;
    }
    
    /**
     * Retorna el contenedor actual en el que debe ubicarse un elemento interno.
     *
     * Si existen copas dentro de la torre, retorna la más interna actual.
     * En caso contrario, retorna la copa externa.
     *
     * @return la copa contenedora actual.
     */
    private Cup currentContainer() {
        return (!insideStack.isEmpty()) ? insideStack.peek() : outer;
    }
    
    /**
     * Actualiza la coordenada más alta ocupada por una tapa en la torre.
     *
     * Calcula la parte superior de la tapa dada y, si está más arriba que la
     * registrada actualmente, actualiza highestCupTopY.
     *
     * @param l tapa cuya posición superior se usa para actualizar el layout.
     */
    private void updateHighestTopWithLid(Lid l) {
        int top = l.getYpo() - l.getHeight();
        highestCupTopY = Math.min(highestCupTopY, top);
    }
    
    /**
     * Verifica si ya existe una copa con el tamaño indicado.
     *
     * @param newSize tamaño a validar.
     * @return true si ya existe una copa con ese tamaño.
     */
    private boolean duplicatedSize(int newSize){
        for (int i = 0; i < cups.size(); i++) {
            Cup c = cups.get(i);
            if (newSize == c.getNumber()){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Elimina y retorna la copa ubicada en el tope de la torre.
     *
     * Efectos:
     * - Oculta visualmente la copa removida.
     * - Elimina su registro del orden de inserción.
     * - Redibuja la torre.
     *
     * @return la copa removida, o null si no había copas.
     */
    public Cup popCup() {
        if (cups.isEmpty()) {
            isOK = false;
            return null;
        }
        Cup removida = cups.peek();
        Lid lid = removida.getLid();    
        if (lid != null && isCoveringPartner(lid)) {
            removeLidForced(removida.getNumber());
        }
    
        if (!removida.canBeRemoved()) {
            isOK = false;
            return null;
        }
        removida = cups.pop();
        removida.makeInvisible();
        for (int i = 0; i < insertionOrder.size(); i++) {
            String[] elem = insertionOrder.get(i);
            if (elem[0].equals("cup") &&
                elem[1].equals(String.valueOf(removida.getNumber()))) {
                insertionOrder.remove(i);
                break;
            }
        }
        isOK = true;
        if (this.isVisible) {
            redraw();
        }
        return removida;
    }
        
    /**
     * Elimina la primera copa encontrada cuyo número coincida con el indicado.
     *
     * Efectos:
     * - Remueve la copa del stack de copas.
     * - La oculta visualmente.
     * - Elimina su referencia de insertionOrder.
     * - Redibuja la torre.
     *
     * @param number número de la copa que se desea eliminar.
     */
    public void removeCup(int number) {
        if (cups.isEmpty()) {
            isOK = false;
            return;
        }
        Cup target = findCup(number);
        if (target == null) {
            isOK = false;
            return;
        }
        Lid lid = target.getLid();
        if (lid != null) {
            removeLidForced(lid.getNumber());
        }
    
        if (!target.canBeRemoved()) {
            isOK = false;
            return;
        }
        Stack<Cup> temp = new Stack<>();
        boolean removed = false;
        while (!cups.isEmpty()) {
            Cup c = cups.pop();
    
            if (!removed && c.getNumber() == number) {
                c.makeInvisible();
                removed = true;
            } else {
                temp.push(c);
            }
        }
    
        while (!temp.isEmpty()) {
            cups.push(temp.pop());
        }
    
        for (int i = 0; i < insertionOrder.size(); i++) {
            String[] elem = insertionOrder.get(i);
            if (elem[0].equals("cup") &&
                elem[1].equals(String.valueOf(number))) {
                insertionOrder.remove(i);
                break;
            }
        }
        isOK = removed;
        if (this.isVisible) {
            redraw();
        }
    }
    
    /**
     * Agrega una nueva tapa a la torre si cumple las restricciones definidas.
     *
     * Restricciones:
     * - El tamaño no debe estar repetido entre las tapas.
     * - El tamaño no debe superar el ancho máximo.
     * - Si existe una copa del mismo color, la tapa debe tener el mismo tamaño.
     *
     * Efectos:
     * - Crea la tapa.
     * - La agrega al stack de tapas.
     * - La registra en insertionOrder.
     * - Intenta asociarla con una copa del mismo color.
     * - Redibuja la torre.
     *
     * @param i tamaño o número de la tapa.
     * @param color color de la tapa.
     */
    public void pushLid(int i) {
        if (i > maxWidth || duplicatedLidSize(i)) {
            isOK = false;
            return;
        }
    
        Cup cup = findCup(i);
        Lid nueva;
    
        if (cup != null) {
            nueva = cup.getLid();
    
            if (nueva == null) {
                nueva = new NormalLid(i, cup.getColor());
                cup.addLid(nueva);
            }
    

            if (lids.contains(nueva)) {
                isOK = false;
                return;
            }
    
        } else {
            String color = getUniqueColor();
            if (color == null) {
                isOK = false;
                return;
            }
    
            nueva = new NormalLid(i, color);
            Cup partner = new NormalCup(i, color);
            partner.addLid(nueva);
        }
        lids.push(nueva);
        insertionOrder.add(new String[]{"lid", String.valueOf(i)});
        isOK = true;
        if (this.isVisible) {
            redraw();
        }
    }
    
    /**
     * Elimina y retorna la tapa ubicada en el tope del stack de tapas.
     *
     * Efectos:
     * - Oculta la tapa removida.
     * - Elimina su referencia de insertionOrder.
     * - Redibuja la torre.
     *
     * @return la tapa removida, o null si no había tapas.
     */
    public Lid popLid() {
        if (lids.isEmpty()) {
            isOK = false;
            return null;
        }    
        Lid removida = lids.peek();
        if (!removida.canExit(this)) {
            isOK = false;
            return null;
        }
        removida = lids.pop();
        removida.makeInvisible();
        for (int i = 0; i < insertionOrder.size(); i++) {
            String[] elem = insertionOrder.get(i);
            if (elem[0].equals("lid") &&
                elem[1].equals(String.valueOf(removida.getNumber()))) {
                insertionOrder.remove(i);
                break;
            }
        }
        isOK = true;
        if (this.isVisible) {
            redraw();
        }
        return removida;
    }
    
    /**
     * Verifica si ya existe una tapa con el tamaño indicado.
     *
     * @param newSize tamaño a validar.
     * @return true si ya existe una tapa con ese tamaño, false en caso contrario.
     */
    private boolean duplicatedLidSize(int newSize) {
        for (Lid l : lids) {
            if (l.getNumber() == newSize) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Elimina la primera tapa encontrada cuyo número coincida con el indicado.
     *
     * Efectos:
     * - Remueve la tapa del stack de tapas.
     * - La oculta visualmente.
     * - Elimina su relación en ListLid.
     * - Elimina su referencia en insertionOrder.
     * - Redibuja la torre al finalizar.
     *
     * @param number número de la tapa que se desea eliminar.
     */
    public void removeLid(int number) {
        if (lids.isEmpty()) {
            isOK = false;
            return;
        }
        Stack<Lid> temp = new Stack<>();
        boolean found = false;
        Lid target = null;
        while (!lids.isEmpty()) {
            Lid l = lids.pop();
            if (!found && l.getNumber() == number) {
                target = l;
                found = true;
                break;
            } else {
                temp.push(l);
            }
        }
        
        if (!found) {
            while (!temp.isEmpty()) {
                lids.push(temp.pop());
            }
            isOK = false;
            return;
        }
    
        if (!target.canExit(this)) {
            lids.push(target);
            while (!temp.isEmpty()) {
                lids.push(temp.pop());
            }
            isOK = false;
            return;
        }
        target.makeInvisible();
        for (int i = 0; i < insertionOrder.size(); i++) {
            String[] elem = insertionOrder.get(i);
            if (elem[0].equals("lid") &&elem[1].equals(String.valueOf(number))) {
                insertionOrder.remove(i);
                break;
            }
        }
    
        while (!temp.isEmpty()) {
            lids.push(temp.pop());
        }
        isOK = true;
        if (this.isVisible) {
            redraw();
        }
    }
    
    /**
     * Ordena las copas de la torre de mayor a menor según su número.
     *
     * Efectos:
     * - Extrae los tamaños actuales de las copas.
     * - Vacía la estructura de copas y el orden de inserción.
     * - Reconstruye la torre insertando nuevamente las copas en orden descendente.
     * - Redibuja la torre.
     *
     * Nota:
     * al reconstruir la torre, las copas se crean otra vez y pueden cambiar de color.
     */
    public void orderTower() {
        ArrayList<Cup> orderedCups = new ArrayList<Cup>();
        this.makeInvisible();
        for (Cup c : cups) {
            orderedCups.add(c);
        }
    
        for (int i = 0; i < orderedCups.size() - 1; i++) {
            for (int j = i + 1; j < orderedCups.size(); j++) {
                if (orderedCups.get(i).getNumber() < orderedCups.get(j).getNumber()) {
                    Cup temp = orderedCups.get(i);
                    orderedCups.set(i, orderedCups.get(j));
                    orderedCups.set(j, temp);
                }
            }
        }
    
        cups.clear();
        insertionOrder.clear();
    
        for (Cup c : orderedCups) {
            cups.push(c);
            insertionOrder.add(new String[]{"cup", String.valueOf(c.getNumber())});
        }
    
        for (Lid l : lids) {
            insertionOrder.add(new String[]{"lid", String.valueOf(l.getNumber())});
        }
        
        redraw();
        isOK = true;
    }

    
    /**
     * Invierte el orden actual de las copas en la torre.
     *
     * Efectos:
     * - Extrae las copas a un stack temporal para invertir su orden.
     * - Invierte también el orden registrado en insertionOrder.
     * - Redibuja la torre con la nueva disposición.
     * - Marca la operación como exitosa.
     */
    public void reverseTower() {
        Stack<Cup> temp = new Stack<Cup>();
    
        while (!cups.isEmpty()) {
            temp.push(cups.pop());
        }
        
        Collections.reverse(insertionOrder);
        cups = temp;
        redraw();
        isOK = true;
    }
    
    public ArrayList<String[]> getInsertionOrder(){
        return insertionOrder;
    }
    
    /**
     * Calcula la altura efectiva de la torre.
     *
     * Solo suma la altura de las copas que están posicionadas externamente,
     * es decir, aquellas que no están marcadas como inside.
     *
     * @return altura total visible de la torre.
     */
    public int Height() {
        int top = Integer.MAX_VALUE;
        int bottom = Integer.MIN_VALUE;
        boolean hasElements = false;
    
        for (Cup c : cups) {
            int elementTop = c.getYpo() - c.getHeight();
            int elementBottom = c.getYpo();
    
            top = Math.min(top, elementTop);
            bottom = Math.max(bottom, elementBottom);
            hasElements = true;
        }
    
        for (Lid l : lids) {
            int elementTop = l.getYpo() - l.getHeight();
            int elementBottom = l.getYpo();
    
            top = Math.min(top, elementTop);
            bottom = Math.max(bottom, elementBottom);
            hasElements = true;
        }
    
        if (!hasElements) {
            return 0;
        }
    
        return bottom - top;
    }
    
    /**
     * Marca la torre como visible.
     *
     * Este método solo cambia el estado lógico de visibilidad;
     * no redibuja automáticamente los elementos.
     */
    public void makeVisible() {
        isVisible = true;
        redraw();
    }
    
    /**
     * Hace invisible toda la torre.
     *
     * Efectos:
     * - Marca la torre como no visible.
     * - Oculta todas las copas y tapas actualmente existentes.
     */
    public void makeInvisible() {
        isVisible = false;
    
        for (Cup c : cups) {
            if (c != null) {
                c.makeInvisible();
            }
        }
    
        for (Lid l : lids) {
            if (l != null) {
                l.makeInvisible();
            }
        }
    }
    
    /**
     * Verifica si la torre es visible.
     */
    public boolean isVisible() {
        return this.isVisible;
    }
    
    /**
     * Busca una copa por su número.
     *
     * @param number número de la copa a buscar.
     * @return la copa encontrada, o null si no existe.
     */
    private Cup findCup(int number) {
        for (Cup c : cups) {
            if (c.getNumber() == number) return c;
        }
        return null;
    }
    
    /**
     * Busca una tapa por su número.
     *
     * @param number número de la tapa a buscar.
     * @return la tapa encontrada, o null si no existe.
     */
    private Lid findLid(int number) {
        for (Lid l : lids) {
            if (l.getNumber() == number) return l;
        }
        return null;
    }

    public Cup getCupByNumber(int number) {
        return findCup(number);
    }
    
    public Lid getLidByNumber(int number) {
        return findLid(number);
    }
    
    /**
     * Finaliza la simulación de la torre.
     *
     * Efectos:
     * - Oculta todas las copas y tapas.
     * - Vacía las estructuras principales.
     * - Limpia el orden de inserción.
     * - Marca la torre como no visible.
     */
    public void exit(){
        for (Cup c : cups) c.makeInvisible();
        for (Lid l : lids) l.makeInvisible();
        cups.clear();
        lids.clear();
        insertionOrder.clear();
        isVisible = false;
    }
    
    /**
     * Verifica si la última operación fue exitosa.
     */
    public boolean isOk() {
        return isOK;
    }
    
    /**
     * Retorna el tamaño del stack de tazas.
     */
    public int getCupsSize() {
        return cups.size();
    }
    
    /**
     * Retorna el tamaño del stack de tapas.
     */
    public int getLidsSize() {
        return lids.size();
    }
    
    /**
     * Dibuja una regla vertical.
     */
    public void drawRule(){
        for (int i = 0; i <= maxHeight; i = i + 1){
            Rectangle r = new Rectangle();
            r.changeSize(2, 10);
            r.changeP(0, i * 10);
            r.changeColor("black");
            r.makeVisible();
        }
    }
    
    /**
     * Reorganiza el orden de inserción para que cada tapa quede ubicada
     * inmediatamente después de su copa correspondiente, si ambas existen.
     *
     * Efectos:
     * - Recorre insertionOrder.
     * - Cuando encuentra una copa, busca una tapa con el mismo número.
     * - Si la encuentra, mueve la tapa para dejarla justo después de la copa.
     * - Redibuja la torre.
     */
    public void cover() {
        for (int i = 0; i < insertionOrder.size(); i++) {
            String[] actual = insertionOrder.get(i);
            if (actual[0].equals("cup")) {
                String numero = actual[1];
                for (int j = 0; j < insertionOrder.size(); j++) {
                    String[] posibleLid = insertionOrder.get(j);
                    if (posibleLid[0].equals("lid") && posibleLid[1].equals(numero)) {
                        insertionOrder.remove(j);
                        insertionOrder.add(i, posibleLid);
                        break;
                    }
                }
            }
        }
        redraw();
        isOK = true;
    }
    
    /**
     * Intercambia la posición de dos objetos dentro del orden de inserción.
     *
     * Cada objeto se identifica mediante un arreglo de dos posiciones:
     * [tipo, numero], por ejemplo {"cup", "5"} o {"lid", "5"}.
     *
     * Efectos:
     * - Busca ambos objetos dentro de insertionOrder.
     * - Si existen, intercambia sus posiciones.
     * - Redibuja la torre.
     * - Si alguno no existe, marca la operación como fallida.
     *
     * @param o1 identificador del primer objeto.
     * @param o2 identificador del segundo objeto.
     */
    public void swap(String[] o1, String[] o2) {
        int idx1 = -1, idx2 = -1;
    
        for (int i = 0; i < insertionOrder.size(); i++) {
            String[] item = insertionOrder.get(i);
            if (item[0].equals(o1[0]) && item[1].equals(o1[1])) idx1 = i;
            if (item[0].equals(o2[0]) && item[1].equals(o2[1])) idx2 = i;
        }
    
        if (idx1 == -1 || idx2 == -1) {
            JOptionPane.showMessageDialog(null, "Objeto no encontrado");
            isOK = false;
            return;
        }
    
        Collections.swap(insertionOrder, idx1, idx2);
        redraw();
        isOK = true;
    }
    
    /**
     * Busca dos objetos de insertionOrder cuyo intercambio reduzca la altura
     * de la torre lo máximo posible.
     *
     * No modifica la torre de forma permanente; solo analiza y retorna
     * los dos objetos que deberían intercambiarse.
     *
     * @return un arreglo de la forma { {tipo1, numero1}, {tipo2, numero2} }.
     *         Si no existe ningún intercambio que reduzca la altura,
     *         retorna un arreglo vacío.
     */
    public String[][] swapToReduce() {
        if (insertionOrder.size() < 2) {
            isOK = false;
            return new String[0][0];
        }
    
        redraw();
        int originalHeight = Height();
    
        int bestI = -1;
        int bestJ = -1;
        int bestHeight = originalHeight;
    
        for (int i = 0; i < insertionOrder.size() - 1; i++) {
            for (int j = i + 1; j < insertionOrder.size(); j++) {
    
                Collections.swap(insertionOrder, i, j);
                redraw();
                int newHeight = Height();
    
                if (newHeight < bestHeight) {
                    bestHeight = newHeight;
                    bestI = i;
                    bestJ = j;
                }
    
                Collections.swap(insertionOrder, i, j);
                redraw();
            }
        }
    
        if (bestI == -1 || bestJ == -1) {
            isOK = false;
            return new String[0][0];
        }
    
        isOK = true;
    
        String[] obj1 = {insertionOrder.get(bestI)[0],insertionOrder.get(bestI)[1]};
        String[] obj2 = {insertionOrder.get(bestJ)[0],insertionOrder.get(bestJ)[1]};
        return new String[][] { obj1, obj2 };
    }
    
    /**
     * Encuentra el contenedor correcto para una cup nueva.
     * Es la cup más pequeña del insideStack dentro de la cual cabe c,
     * es decir, insideStack[i].number > c.number.
     * Si ninguna cup del stack la contiene, usa outer.
     */
    private Cup findContainerForCup(Cup c) {
        Cup best = null;
        for (Cup inside : insideStack) {
            if (inside.getNumber() > c.getNumber()) {
                if (best == null || inside.getNumber() < best.getNumber()) {
                    best = inside;
                }
            }
        }
        return (best != null) ? best : outer;
    }
    
    /**
     * Encuentra el contenedor correcto para una lid nueva.
     * Es la cup más pequeña del insideStack dentro de la cual cabe la lid,
     * es decir, insideStack[i].number >= l.number.
     * Si ninguna cup del stack la contiene, usa outer.
     */
    private Cup findContainerForLid(Lid l) {
        Cup best = null;
    
        for (Cup inside : insideStack) {
            if (inside.getNumber() >= l.getNumber()) {
                if (best == null || inside.getNumber() < best.getNumber()) {
                    best = inside;
                }
            }
        }
    
        if (best != null) {
            return best;
        }
    
        if (outer != null && outer.getNumber() >= l.getNumber()) {
            return outer;
        }
    
        return null;
    }
    
    /**
     * Encuentra el soporte para una cup nueva DENTRO de un contenedor dado.
     * Es la cup más grande del insideStack que esté contenida en 'container'
     * y cuyo número sea menor que c.number.
     */
    private Cup findSupportForCup(Cup c, Cup container) {
        Cup bestSupport = null;
    
        for (Cup inside : insideStack) {
            if (inside == container) {
                continue;
            }
    
            Cup parent = parentByCup.get(inside);
    
            if (parent == container && inside.getNumber() < c.getNumber()) {
                if (bestSupport == null || inside.getNumber() > bestSupport.getNumber()) {
                    bestSupport = inside;
                }
            }
        }
    
        return bestSupport;
    }
    
    public void pushCup(String type, int i) throws towerException {
        if (i > maxWidth || duplicatedSize(i)) {
            throw new towerException(towerException.DUPLICATED_SIZE);
        }
        String color = getUniqueColor();
        if (color == null) {
            isOK = false;
            return;
        }
        Cup nueva;
        if (type.equalsIgnoreCase("normal")) {
            nueva = new NormalCup(i, color);
        } else if (type.equalsIgnoreCase("opener")) {
            nueva = new OpenerCup(i, color);
        } else if (type.equalsIgnoreCase("hierarchical")) {
            nueva = new HierarchicalCup(i, color);
        } else {
            throw new towerException(towerException.NON_EXISTENT_TYPE);
        }
        Lid partner = null;
        for (Lid l : lids) {
            if (l.getNumber() == i && !l.hasPartnerCup()) {
                partner = l;
                break;
            }
        }
    
        if (partner != null) {
            nueva.addLid(partner);
        }
        cups.push(nueva);
        insertionOrder.add(new String[]{"cup", String.valueOf(i)});
        isOK = true;
        if (this.isVisible) {
            redraw();
        }
    }

    private void removeLidsBetween(int containerNumber, int cupIndex) {
        boolean started = false;
        ArrayList<Lid> toRemove = new ArrayList<>();
    
        for (int i = 0; i < insertionOrder.size(); i++) {
            String[] elem = insertionOrder.get(i);
    
            if (elem[0].equals("cup") && 
                elem[1].equals(String.valueOf(containerNumber))) {
                started = true;
                continue;
            }
    
            if (i >= cupIndex) break;
    
            if (started && elem[0].equals("lid")) {
                Lid lid = findLid(Integer.parseInt(elem[1]));
                if (lid != null) {
                    toRemove.add(lid);
                }
            }
        }
    
        for (Lid lid : toRemove) {
            removeSpecificLid(lid);
        }
    }
    
    private void removeOutsideLidsBefore(int cupIndex, int cupNumber) {
        ArrayList<Lid> toRemove = new ArrayList<>();
    
        int previousCupNumber = -1;
    
        for (int i = cupIndex - 1; i >= 0; i--) {
            String[] elem = insertionOrder.get(i);
    
            if (elem[0].equals("cup")) {
                previousCupNumber = Integer.parseInt(elem[1]);
                break;
            }
        }
    
        if (previousCupNumber == -1) {
            return;
        }
    
        for (int i = cupIndex - 1; i >= 0; i--) {
            String[] elem = insertionOrder.get(i);
    
            if (elem[0].equals("cup")) {
                break;
            }
    
            if (elem[0].equals("lid")) {
                int lidNum = Integer.parseInt(elem[1]);
                Lid lid = findLid(lidNum);
    
                if (lid != null && lidNum > previousCupNumber) {
                    toRemove.add(lid);
                }
            }
        }
    
        for (Lid lid : toRemove) {
            removeSpecificLid(lid);
        }
    }
    
    public Cup findContainerBefore(int cupIndex, Cup cup) {
        Cup best = null;
    
        for (int i = cupIndex - 1; i >= 0; i--) {
            String[] elem = insertionOrder.get(i);
    
            if (elem[0].equals("cup")) {
                int num = Integer.parseInt(elem[1]);
    
                if (num > cup.getNumber()) {
                    Cup candidate = findCup(num);
    
                    if (best == null || num < best.getNumber()) {
                        best = candidate; // el más cercano que la contiene
                    }
                }
            }
        }
    
        return best;
    }
    
    private int findPreviousCupIndex(int fromIndex) {
        for (int i = fromIndex - 1; i >= 0; i--) {
            String[] element = insertionOrder.get(i);
    
            if (element[0].equals("cup")) {
                return i;
            }
        }
        return -1;
    }
    
    
    private void removeSpecificLid(Lid lid) {
        Stack<Lid> temp = new Stack<Lid>();
    
        while (!lids.isEmpty()) {
            Lid actual = lids.pop();
    
            if (actual != lid) {
                temp.push(actual);
            } else {
                actual.makeInvisible();
            }
        }
    
        while (!temp.isEmpty()) {
            lids.push(temp.pop());
        }
    
        for (int i = 0; i < insertionOrder.size(); i++) {
            String[] elemento = insertionOrder.get(i);
    
            if (elemento[0].equals("lid") &&
                elemento[1].equals(String.valueOf(lid.getNumber()))) {
                insertionOrder.remove(i);
                break;
            }
        }
    
        Cup partner = lid.getPartnerCup();
        if (partner != null && partner.getLid() == lid) {
            partner.addLid(null);
        }
    }
    
    public void repositionForHierarchical(HierarchicalCup cup) {
        int currentIndex = findCupIndex(cup.getNumber());
    
        if (!isValidIndex(currentIndex)) {
            return;
        }
    
        int targetIndex = findTargetIndexForHierarchical(cup.getNumber(), currentIndex);
        moveCupInInsertionOrder(currentIndex, targetIndex);
        lockCupIfNeeded(cup, targetIndex);
    }
    
    private int findTargetIndexForHierarchical(int cupNumber, int currentIndex) {
        int targetIndex = currentIndex;
    
        for (int i = currentIndex - 1; i >= 0; i--) {
            String[] element = insertionOrder.get(i);
    
            if (isSmallerObject(element, cupNumber)) {
                targetIndex = i;
            } else {
                break;
            }
        }
    
        return targetIndex;
    }
    
    private void moveCupInInsertionOrder(int currentIndex, int targetIndex) {
        if (currentIndex == targetIndex) {
            return;
        }
    
        String[] cupData = insertionOrder.remove(currentIndex);
        insertionOrder.add(targetIndex, cupData);
    }
    
    private void lockCupIfNeeded(Cup cup, int targetIndex) {
        if (targetIndex == 0) {
            cup.lock();
        }
    }
    
    private boolean isValidIndex(int index) {
        return index != -1;
    }
    
    private boolean isSmallerObject(String[] element, int cupNumber) {
        int number = Integer.parseInt(element[1]);
        return number < cupNumber;
    }
    
    public boolean containsCup(Cup cup) {
        return cups.contains(cup);
    }
    
    public boolean isCoveringPartner(Lid lid) {
        if (lid == null || !lid.hasPartnerCup()) {
            return false;
        }
        Cup cup = lid.getPartnerCup();
        if (!cups.contains(cup) || !lids.contains(lid)) {
            return false;
        }
        int cupIndex = -1;
        int lidIndex = -1;
        for (int i = 0; i < insertionOrder.size(); i++) {
            String[] elem = insertionOrder.get(i);
            if (elem[0].equals("cup") &&
                elem[1].equals(String.valueOf(cup.getNumber()))) {
                cupIndex = i;
            }
    
            if (elem[0].equals("lid") &&
                elem[1].equals(String.valueOf(lid.getNumber()))) {
                lidIndex = i;
            }
        }
    
        if (cupIndex == -1 || lidIndex == -1) {
            return false;
        }
        return lidIndex == cupIndex + 1;
    }
    
    public void pushLid(String type, int i) throws towerException {
        if (i > maxWidth || duplicatedLidSize(i)) {
            throw new towerException(towerException.DUPLICATED_SIZE);
        }
        Cup cup = findCup(i);
        String color;
        if (cup != null) {
            color = cup.getColor();
        } else {
            color = getUniqueColor();
            if (color == null) {
                isOK = false;
                return;
            }
        }
        Lid nueva;
        if (type.equalsIgnoreCase("normal")) {
            nueva = new NormalLid(i, color);
        } else if (type.equalsIgnoreCase("fearful")) {
            nueva = new FearfulLid(i, color);
        } else if (type.equalsIgnoreCase("crazy")) {
            nueva = new CrazyLid(i, color);
        } else {
            throw new towerException(towerException.NON_EXISTENT_TYPE);
        }
    
        if (cup != null) {
            cup.addLid(nueva);
        }
    
        if (!nueva.canEnter(this)) {
            isOK = false;
            return;
        }
        lids.push(nueva);
        insertionOrder.add(new String[]{"lid", String.valueOf(i)});
        reorderSpecialLidsInPlace();
        isOK = true;
        if (this.isVisible) {
            this.redraw();
        }
    }
    
    private void removeLidForced(int number) {
        Stack<Lid> temp = new Stack<>();
        Lid removed = null;
    
        while (!lids.isEmpty()) {
            Lid current = lids.pop();
    
            if (removed == null && current.getNumber() == number) {
                removed = current;
                current.makeInvisible();
            } else {
                temp.push(current);
            }
        }
    
        while (!temp.isEmpty()) {
            lids.push(temp.pop());
        }
    
        for (int i = 0; i < insertionOrder.size(); i++) {
            String[] elem = insertionOrder.get(i);
            if (elem[0].equals("lid") &&
                elem[1].equals(String.valueOf(number))) {
                insertionOrder.remove(i);
                break;
            }
        }
    }
    
    private void reorderSpecialLidsInPlace() {
        for (Lid l : lids) {
            if (!l.shouldBeBeforeCup()) continue;
    
            int lidIndex = -1;
            int cupIndex = -1;
    
            for (int i = 0; i < insertionOrder.size(); i++) {
                String[] e = insertionOrder.get(i);
    
                if (e[0].equals("lid") && e[1].equals(String.valueOf(l.getNumber()))) {
                    lidIndex = i;
                }
    
                Cup partner = l.getPartnerCup();
                if (partner != null &&
                    e[0].equals("cup") &&
                    e[1].equals(String.valueOf(partner.getNumber()))) {
                    cupIndex = i;
                }
            }
    
            if (lidIndex == -1 || cupIndex == -1) continue;
            if (lidIndex < cupIndex) continue;
    
            String[] lidElem = insertionOrder.remove(lidIndex);
            insertionOrder.add(cupIndex, lidElem);
        }
    }
    
    private void prepareCupsBeforeLayout() {
        ArrayList<String[]> snapshot = new ArrayList<>(insertionOrder);
    
        for (String[] elem : snapshot) {
            if (elem[0].equals("cup")) {
                int numero = Integer.parseInt(elem[1]);
                Cup c = findCup(numero);
    
                if (c != null) {
                    c.beforeEnter(this);
                }
            }
        }
    }
    
        public int findCupIndex(int number) {
        for (int i = 0; i < insertionOrder.size(); i++) {
            String[] elem = insertionOrder.get(i);
            if (elem[0].equals("cup") && 
                elem[1].equals(String.valueOf(number))) {
                return i;
            }
        }
        return -1;
    }
    
    public List<Lid> getLidsBetween(Cup container, Cup target) {
        List<Lid> result = new ArrayList<>();
    
        int containerIndex = findCupIndex(container.getNumber());
        int targetIndex = findCupIndex(target.getNumber());
    
        for (int i = containerIndex + 1; i < targetIndex; i++) {
            String[] elem = insertionOrder.get(i);
    
            if (elem[0].equals("lid")) {
                Lid lid = findLid(Integer.parseInt(elem[1]));
                if (lid != null) {
                    result.add(lid);
                }
            }
        }
    
        return result;
    }
    
    public List<Lid> getOutsideBlockingLids(Cup cup, int cupIndex) {
        List<Lid> result = new ArrayList<>();
    
        int previousCupNumber = -1;
    
        // encontrar la cup anterior
        for (int i = cupIndex - 1; i >= 0; i--) {
            String[] elem = insertionOrder.get(i);
    
            if (elem[0].equals("cup")) {
                previousCupNumber = Integer.parseInt(elem[1]);
                break;
            }
        }
    
        if (previousCupNumber == -1) return result;
    
        // buscar lids que están afuera
        for (int i = cupIndex - 1; i >= 0; i--) {
            String[] elem = insertionOrder.get(i);
    
            if (elem[0].equals("cup")) break;
    
            if (elem[0].equals("lid")) {
                int lidNum = Integer.parseInt(elem[1]);
    
                if (lidNum > previousCupNumber) {
                    Lid lid = findLid(lidNum);
                    if (lid != null) {
                        result.add(lid);
                    }
                }
            }
        }
    
        return result;
    }
    
    public void removeLids(List<Lid> lidsToRemove) {
        for (Lid lid : lidsToRemove) {
            removeSpecificLid(lid);
        }
    }
}

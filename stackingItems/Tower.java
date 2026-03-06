import java.util.Stack;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 * Write a description of class Tower here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Tower
{
    private int width;
    private int height;
    private int maxWidth;
    private int maxHeight;
    private int Y = 200;
    private int X = 130;
    private boolean isVisible;
    private Stack<Cup> cups;
    private Stack<Lid> lids; 
    private HashMap<String, Lid> ListLid;
    private boolean isOK;
    private Random random;
    private ArrayList<String[]> insertionOrder;

    // Estado del layout
    private Map<Cup, Lid> topInsideLidByCup;
    private Cup outer;
    private Stack<Cup> insideStack;
    private int outsideSize;
    private int outsideBaseY;
    private int highestCupTopY;    
    private boolean lastOutsideWasLid;
    
    /**
     * Construye una torre vacía con límites máximos de altura y ancho.
     *
     * @param nmaxHeight altura máxima permitida para la torre (suma de alturas externas).
     * @param nmaxWidth  tamaño máximo permitido para una copa (número/diámetro).
     */
    public Tower(int nmaxHeight, int nmaxWidth) {
        cups = new Stack<Cup>();
        lids = new Stack<Lid>();
        ListLid = new HashMap<String, Lid>();
        random = new Random();
        insertionOrder = new ArrayList<String[]>();
    
        width = 0;
        height = 0;
        maxWidth = nmaxWidth;
        maxHeight = nmaxHeight;
        isVisible = false;
        isOK = true;
    
        insideStack = new Stack<Cup>();
        topInsideLidByCup = new HashMap<Cup, Lid>();
    
        outer = null;
        outsideSize = -1;
        outsideBaseY = Y;
        highestCupTopY = Y;
        lastOutsideWasLid = false;
    }
    
    /**
     * Crea una torre con n tazas de tamaños impares: 1, 3, 5, ..., 2n-1.
     * No se incluyen tapas.
     * @param numCups número de tazas a crear
     */
    public Tower(int numCups) {
        this(numCups * 2, numCups * 10);
        for (int i = 1; i <= numCups; i++) {
            int size = (2 * i) - 1;
            cups.push(new Cup(size, randomColor()));
            insertionOrder.add(new String[]{"cup", String.valueOf(size)});
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
     * Agrega una copa al tope de la torre, si cumple restricciones.
     *
     * @param i número/tamaño de la copa a insertar.
     */
    public void pushCup(int i){
        String color = randomColor();
        Cup nueva = new Cup(i, color);

        if (ListLid.get(color) != null) {
            Lid lid = ListLid.get(color);
            if (lid.getNumber() != i){
                JOptionPane.showMessageDialog(null, "No se puede añadir una copa con tamaño distinto a su tapa");
                isOK = false;
                return;
            }
            nueva.addLid(lid);
        }

        makeVisible();
        
        if (i > maxWidth || duplicatedSize(i)) {
            if (this.isVisible){
                JOptionPane.showMessageDialog(null, "No se puede hacer la operacion");
                isOK = false;
                return;
            }
        }
 
        if (0 <= maxHeight && i < maxWidth) {
            cups.push(nueva);
            insertionOrder.add(new String[]{"cup", String.valueOf(i)});
            redraw();
            isOK = true;
        }
        else {
            JOptionPane.showMessageDialog(null, "No se pudo realizar la operacion");
            isOK = false;
        }
    }

    /**
     * Recalcula y actualiza la posición visual de copas y tapas.
     */
    private void redraw() {
        final int CANVAS_WIDTH = 300;
        final int GROSOR = 5;
    
        hideAllElements();
        resetLayoutState();
    
        for (int i = 0; i < insertionOrder.size(); i++) {
            String tipo = insertionOrder.get(i)[0];
            int numero = Integer.parseInt(insertionOrder.get(i)[1]);
    
            if (isCup(tipo)) {
                Cup c = findCup(numero);
                if (c == null) continue;
    
                if (outer == null) {
                    placeFirstCupOutside(c, CANVAS_WIDTH);
                } else {
                    boolean goesOutside = lastOutsideWasLid || (c.getNumber() >= outsideSize);
    
                    if (goesOutside) {
                        placeCupOutside(c, CANVAS_WIDTH);
                    } else {
                        placeCupInside(c);
                    }
                }
            }
            else if (isLid(tipo)) {
                Lid l = findLid(numero);
                if (l == null) continue;
    
                if (outer == null) {
                    placeFirstLidOutside(l, CANVAS_WIDTH);
                } else {
                    Cup container = currentContainer();
    
                    boolean forceOutsideBecauseDoesNotFit =
                        (container != null && l.getNumber() >= container.getNumber());
    
                    boolean goesOutside =
                        forceOutsideBecauseDoesNotFit || (l.getNumber() >= outsideSize);
    
                    if (goesOutside) {
                        placeLidOutside(l, CANVAS_WIDTH);
                    } else {
                        placeLidInside(l, GROSOR);
                    }
                }
            }
        }
    
        showAllElementsInInsertionOrder();
        isOK = true;
    }
        
    private void hideAllElements() {
        for (Cup c : cups) c.makeInvisible();
        for (Lid l : lids) l.makeInvisible();
    }
    
    private void resetLayoutState() {
        outer = null;
        insideStack.clear();
        topInsideLidByCup.clear();
    
        outsideSize = -1;
        outsideBaseY = Y;
        highestCupTopY = Y;
        lastOutsideWasLid = false;
    }
    
    private void showAllElementsInInsertionOrder() {
        for (String[] item : insertionOrder) {
            int number = Integer.parseInt(item[1]);
    
            if (isCup(item[0])) {
                Cup c = findCup(number);
                if (c != null) c.makeVisible();
            } else if (isLid(item[0])) {
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
    
    private void updateHighestCupTop(Cup c) {
        int top = c.getYpo() - c.getHeight();
        highestCupTopY = Math.min(highestCupTopY, top);
    }

    private Cup currentContainer() {
        return (!insideStack.isEmpty()) ? insideStack.peek() : outer;
    }
    
    private void placeFirstCupOutside(Cup c, int canvasWidth) {
        int x = (canvasWidth - c.getWidth()) / 2;
        c.setPosition(x, Y);
        c.setInside(false);
    
        outer = c;
        insideStack.clear();
    
        outsideSize = c.getNumber();
        outsideBaseY = c.getYpo() - c.getHeight();
        lastOutsideWasLid = false;
    
        updateHighestCupTop(c);
    }
   
    private void placeCupOutside(Cup c, int canvasWidth) {
        int x = (canvasWidth - c.getWidth()) / 2;
        int baseY = highestCupTopY;
    
        c.setPosition(x, baseY);
        c.setInside(false);
    
        outer = c;
        insideStack.clear();
    
        outsideSize = c.getNumber();
        outsideBaseY = c.getYpo() - c.getHeight();
        lastOutsideWasLid = false;
    
        updateHighestCupTop(c);
    }
    
    private void placeCupInside(Cup c) {
        Cup support = null;
    
        while (!insideStack.isEmpty() && c.getNumber() >= insideStack.peek().getNumber()) {
            support = insideStack.pop();
        }
    
        Cup container = currentContainer();
    
        if (container == null) return;
    
        if (support == null) {
            placeInside(c, container);
        } else {
            placeAboveInContainer(c, support, container);
        }
    
        c.setInside(true);
        insideStack.push(c);
    
        updateHighestCupTop(c);
    }
    
    private void placeInside(Cup c, Cup container) {
        if (c == null || container == null) return;
    
        int grosor = 5;
    
        int innerX = container.getXpo() + grosor;
        int innerWidth = container.getWidth() - (2 * grosor);
        int x = innerX + (innerWidth - c.getWidth()) / 2;
    
        Lid topInsideLid = topInsideLidByCup.get(container);
    
        int y;
        if (topInsideLid == null) {
            y = container.getYpo() - grosor;
        } else {
            y = topInsideLid.getYpo() - topInsideLid.getHeight();
        }
    
        c.setPosition(x, y);
    }
    
    private void placeAboveInContainer(Cup c, Cup support, Cup container) {
        if (c == null || support == null || container == null) return;
    
        int grosor = 5;
    
        int innerX = container.getXpo() + grosor;
        int innerWidth = container.getWidth() - (2 * grosor);
        int x = innerX + (innerWidth - c.getWidth()) / 2;
    
        int y = support.getYpo() - support.getHeight();
    
        Lid topInsideLid = topInsideLidByCup.get(container);
        if (topInsideLid != null) {
            int lidTop = topInsideLid.getYpo() - topInsideLid.getHeight();
            y = Math.min(y, lidTop);
        }
    
        c.setPosition(x, y);
    }
    
    private void placeLidOutside(Lid l, int canvasWidth) {
        int x = (canvasWidth - l.getWidth()) / 2;
        int baseY = Math.min(outsideBaseY, highestCupTopY);
    
        l.setPosition(x, baseY);
        l.setInside(false);
    
        insideStack.clear();
    
        outsideSize = l.getNumber();
        outsideBaseY = l.getYpo() - l.getHeight();
        lastOutsideWasLid = true;
    
        updateHighestTopWithLid(l);
    }
    
    private void placeFirstLidOutside(Lid l, int canvasWidth) {
        int x = (canvasWidth - l.getWidth()) / 2;
        l.setPosition(x, Y);
        l.setInside(false);
    
        insideStack.clear();
    
        outsideSize = l.getNumber();
        outsideBaseY = l.getYpo() - l.getHeight();
        lastOutsideWasLid = true;
    
        updateHighestTopWithLid(l);
    }

    private void placeLidInside(Lid l, int grosor) {
        Cup container = currentContainer();
        if (container == null) return;
    
        int innerX = container.getXpo() + grosor;
        int innerW = container.getWidth() - 2 * grosor;
        int x = innerX + (innerW - l.getWidth()) / 2;
    
        Lid topInside = topInsideLidByCup.get(container);
    
        int y;
        if (topInside == null) {
            y = container.getYpo() - grosor;
        } else {
            y = topInside.getYpo() - topInside.getHeight();
        }
    
        l.setPosition(x, y);
        l.setInside(true);
    
        topInsideLidByCup.put(container, l);
    }
    
    private void updateHighestTopWithLid(Lid l) {
        int top = l.getYpo() - l.getHeight();
        highestCupTopY = Math.min(highestCupTopY, top);
    }
        
    /**
     * Verifica si ya existe una copa con el mismo tamaño/número.
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
     * Remueve y retorna la copa del tope.
     */
    public Cup popCup(){
        if (!cups.isEmpty()) {
            Cup removida = cups.pop();
            removida.makeInvisible();
            insertionOrder.removeIf(e -> e[0].equals("cup") &&
                e[1].equals(String.valueOf(removida.getNumber())));
            redraw();
            isOK = true;
            return removida;
        }
        isOK = false;
        return null;
    }
    
    /**
     * Remueve una copa por número.
     */
    public void removeCup(int number) {
        if (cups.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No existe ninguna copa");
            return;
        }
    
        Stack<Cup> temp = new Stack<Cup>();
        boolean found = false;
    
        while (!cups.isEmpty()) {
            Cup c = cups.pop();
            if (!found && c.getNumber() == number) {
                found = true;
                c.makeInvisible();
                insertionOrder.removeIf(e -> e[0].equals("cup") &&
                    e[1].equals(String.valueOf(number)));
            } else {
                temp.push(c);
            }
        }
        
        cups.clear();
       
        if (!found) {
            JOptionPane.showMessageDialog(null, "No existe la copa indicada");
        }
    
        while (!temp.isEmpty()) {
            cups.push(temp.pop());
        }
    
        redraw();
    }
    
    /**
     * Agrega una tapa.
     */
    public void pushLid(int i, String color) {
        Lid nueva = new Lid(i, color);
    
        if (i > maxWidth || duplicatedLidSize(i)) {
            JOptionPane.showMessageDialog(null, "No se puede hacer la operacion con la tapa");
            isOK = false;
            return;
        }
    
        for (Cup c : cups) {
            if (c.getColor().equals(color)) {
                if (c.getNumber() != i) {
                    JOptionPane.showMessageDialog(null, "No se puede añadir una tapa con tamaño distinto a su copa");
                    isOK = false;
                    return;
                }
                c.addLid(nueva);
            }
        }
    
        ListLid.put(color, nueva);
        lids.push(nueva);
        insertionOrder.add(new String[]{"lid", String.valueOf(i)});
        makeVisible();
        redraw();
        isOK = true;
    }
    
    /**
     * Remueve y retorna la tapa del tope.
     */
    public Lid popLid() {
        if (!lids.isEmpty()) {
            Lid removida = lids.pop();
            removida.makeInvisible();
            insertionOrder.removeIf(e -> e[0].equals("lid") &&
                e[1].equals(String.valueOf(removida.getNumber())));
            redraw();
            isOK = true;
            return removida;
        }
        isOK = false;
        return null;
    }
    
    private boolean duplicatedLidSize(int newSize) {
        for (Lid l : lids) {
            if (l.getNumber() == newSize) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Remueve una tapa por número.
     */
    public void removeLid(int number){
        if (lids.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No existe ninguna tapa");
            return;
        }
    
        Stack<Lid> temp = new Stack<Lid>();
        boolean found = false;
    
        while (!lids.isEmpty()) {
            Lid l = lids.pop();
            if (!found && l.getNumber() == number) {
                found = true;
                l.makeInvisible();
                ListLid.remove(l.getColor());
                insertionOrder.removeIf(e -> e[0].equals("lid") &&
                    e[1].equals(String.valueOf(number)));
            } else {
                temp.push(l);
            }
        }
    
        if (!found) {
            JOptionPane.showMessageDialog(null, "No existe la tapa indicada");
        }
    
        while (!temp.isEmpty()) {
            lids.push(temp.pop());
        }
    
        redraw();
    }
    
    /**
     * Ordena la torre de mayor a menor.
     */
    public void orderTower() {
        ArrayList<Integer> sizes = new ArrayList<Integer>();
        for (Cup c : cups) {
            sizes.add(c.getNumber());
            c.makeInvisible();
        }

        Collections.sort(sizes, Collections.reverseOrder());
        cups.clear();
        insertionOrder.clear();

        for (Integer size : sizes) {
            pushCup(size);
        }
        
        redraw();
    }
    
    /**
     * Invierte el orden del stack de copas.
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
    
    /**
     * Calcula la altura total efectiva de la torre.
     */
    public int Height(){
        int total = 0;
        for (Cup c : cups){
            if (!c.isInside()) {
                total += c.getHeight();
            }
        }
        return total;
    }
    
    /**
     * Marca la torre como visible.
     */
    public void makeVisible() {
        isVisible = true;
    }
    
    /**
     * Hace invisible la torre.
     */
    public void makeInvisible() {
        isVisible = false;
        for (Cup c: cups) c.makeInvisible();
        for (Lid l: lids) l.makeInvisible();
    }
    
    /**
     * Verifica si la torre es visible.
     */
    public boolean isVisible() {
        return isVisible;
    }
    
    private Cup findCup(int number) {
        for (Cup c : cups) {
            if (c.getNumber() == number) return c;
        }
        return null;
    }
    
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
     * Termina el simulador.
     */
    public void exit(){
        for (Cup c : cups) c.makeInvisible();
        for (Lid l : lids) l.makeInvisible();
        cups.clear();
        lids.clear();
        insertionOrder.clear();
        isVisible = false;
    }

    public int getInsertionOrderSize(){
        return insertionOrder.size();
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
    
    public void cover() {
        for (int i = 0; i < insertionOrder.size() - 1; i++) {
            String[] actual = insertionOrder.get(i);
            String[] siguiente = insertionOrder.get(i + 1);
    
            boolean actualEsCup = actual[0].equals("cup");
            boolean siguienteEsLid = siguiente[0].equals("lid");
            boolean mismoNumero = actual[1].equals(siguiente[1]);
    
            if (actualEsCup && siguienteEsLid && mismoNumero) {
                for (Cup c : cups) {
                    if (c.getNumber() == Integer.parseInt(actual[1])) {
                        for (Lid l : lids) {
                            if (l.getNumber() == c.getNumber()) {
                                c.addLid(l);
                            }
                        }
                    }
                }
            }
        }
        redraw();
        isOK = true;
    }
    
    /**
     * Intercambia la posición de dos objetos en la torre.
     */
    public void swap(String[] o1, String[] o2) {
        int idx1 = -1, idx2 = -1;
    
        for (int i = 0; i < insertionOrder.size(); i++) {
            String[] item = insertionOrder.get(i);
            if (item[0].equals(o1[0]) && item[1].equals(o1[1])) idx1 = i;
            if (item[0].equals(o2[0]) && item[1].equals(o2[1])) idx2 = i;
        }
    
        if (idx1 == -1 || idx2 == -1) {
            if (isVisible) JOptionPane.showMessageDialog(null, "Objeto no encontrado");
            isOK = false;
            return;
        }
    
        Collections.swap(insertionOrder, idx1, idx2);
        redraw();
        isOK = true;
    }
}
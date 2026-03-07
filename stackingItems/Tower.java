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
        if (numCups!= 0){
            for (int i = 1; i <= numCups; i++) {
                int size = (2 * i) - 1;  
                cups.push(new Cup(size, randomColor()));
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
            JOptionPane.showMessageDialog(null, "No se puede hacer la operacion");
            isOK = false;
            return;
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
    
                    boolean forceOutsideBecauseDoesNotFit =(container != null && l.getNumber() >= container.getNumber());
    
                    boolean goesOutside =forceOutsideBecauseDoesNotFit || (l.getNumber() >= outsideSize);
    
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
    
        outsideSize = -1;
        outsideBaseY = Y;
        highestCupTopY = Y;
        lastOutsideWasLid = false;
    }
    
    /**
     * Hace visibles las copas y tapas siguiendo el orden definido
     * en insertionOrder.
     */
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
     * Ubica la primera copa externa de la torre, centrada horizontalmente.
     *
     * @param c copa a posicionar.
     * @param canvasWidth ancho del canvas de dibujo.
     */
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

    /**
     * Ubica una copa externamente sobre la parte más alta ocupada hasta el momento.
     *
     * @param c copa a posicionar.
     * @param canvasWidth ancho del canvas.
     */
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
    
    /**
     * Ubica una copa dentro del contenedor actual.
     *
     * Si existe una copa de soporte apropiada, la nueva copa se coloca sobre ella;
     * en caso contrario, se coloca directamente dentro del contenedor.
     *
     * @param c copa a posicionar.
     */
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
    
    /**
     * Ubica una copa directamente dentro de otra copa contenedora.
     *
     * La copa se centra horizontalmente dentro del espacio interno del contenedor.
     * En el eje vertical se apoya sobre el borde interno del contenedor o,
     * si ya existe una tapa dentro de este, sobre la parte superior de esa tapa.
     *
     * @param c copa que se desea posicionar.
     * @param container copa que actúa como contenedor.
     */
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
    
    /**
     * Ubica una copa dentro de un contenedor, apoyándola sobre otra copa interna.
     *
     * La copa se centra horizontalmente dentro del contenedor y se coloca
     * verticalmente encima de la copa de soporte. Si existe una tapa interna
     * más alta dentro del mismo contenedor, se respeta esa posición límite.
     *
     * @param c copa que se desea posicionar.
     * @param support copa interna que sirve de soporte.
     * @param container copa contenedora principal.
     */
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
    
    /**
     * Ubica una tapa externamente en la torre.
     *
     * @param l tapa a posicionar.
     * @param canvasWidth ancho del canvas.
     */
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
    
    /**
     * Ubica la primera tapa externa de la torre.
     *
     * @param l tapa a posicionar.
     * @param canvasWidth ancho del canvas.
     */
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

    /**
     * Ubica una tapa dentro de la copa contenedora actual.
     *
     * @param l tapa a posicionar.
     * @param grosor grosor usado como margen interno de la copa.
     */
    private void placeLidInside(Lid l, int grosor) {
        Cup container = currentContainer();
        if (container == null) return;
    
        int innerX = container.getXpo() + grosor;
        int innerW = container.getWidth() - 2 * grosor;
        int x = innerX + (innerW - l.getWidth()) / 2;
    
        Lid topInside = topInsideLidByCup.get(container);
    
        int y;
        if (topInside == null) {
            y = container.getYpo() - container.getHeight() + l.getHeight() + grosor;
        } else {
            y = topInside.getYpo() - topInside.getHeight();
        }
    
        l.setPosition(x, y);
        l.setInside(true);
    
        topInsideLidByCup.put(container, l);
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
        ArrayList<Integer> sizes = new ArrayList<Integer>();
        ArrayList<String> colors = new ArrayList<String>();
        for (Cup c : cups) {
            sizes.add(c.getNumber());
            colors.add(c.getColor());
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
        for (Cup c: cups) c.makeInvisible();
        for (Lid l: lids) l.makeInvisible();
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
                        insertionOrder.add(i + 1, posibleLid);
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
}
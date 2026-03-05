import java.util.Stack;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import java.util.Collections;
import java.util.*;
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
    private ArrayList<String[]> insertionOrder = new ArrayList<>();
    private Map<Cup, Lid> topInsideLidByCup = new HashMap<>();
    
    /**
     * Construye una torre vacía con límites máximos de altura y ancho.
     *
     * @param nmaxHeight altura máxima permitida para la torre (suma de alturas externas).
     * @param nmaxWidth  tamaño máximo permitido para una copa (número/diámetro).
     */
    public Tower(int nmaxHeight,int nmaxWidth)
    {
        cups = new Stack<Cup>();
        ListLid = new HashMap<>();
        lids = new Stack<Lid>();
        insertionOrder = new ArrayList<String[]>();
        Map<Cup, Lid> topInsideLidByCup = new HashMap<>();
        this.maxHeight = nmaxHeight;
        this.maxWidth = nmaxWidth;

        isVisible = false;
        isOK = true;
    }
    
    
    
    /**
     * Crea una torre con n tazas de tamaños impares: 1, 3, 5, ..., 2n-1.
     * No se incluyen tapas.
     * @param numCups número de tazas a crear
     */
    public Tower(int numCups) {
        this(numCups * 2, numCups * 10);
        for (int i = 1; i <= numCups; i++) {
            int size = (2 * i) - 1; // 1, 3, 5, 7...
            cups.push(new Cup(size, randomColor()));
            insertionOrder.add(new String[]{"cup", String.valueOf(size)});
        }
        makeVisible();
        redraw();
    }
    
    /**
     * Genera y retorna un color aleatorio de una lista predefinida.
     * Crea un objeto Random y elige un color entre:
     * magenta, green, yellow, blue, black, red.
     *
     * @return un String con el nombre del color aleatorio.
     */
    private String randomColor(){
        random = new Random ();
        List<String> lista = List.of("magenta", "green", "yellow","blue","black","red","orange","cyan","pink","grey");
        return  lista.get(random.nextInt(lista.size()));
    }
    
    /**
     * Agrega una copa al tope de la torre, si cumple restricciones.
     * Reglas:
     * - No permite tamaños duplicados.
     * - No permite tamaños mayores al máximo (maxWidth).
     * - No permite exceder la altura máxima (maxHeight) sumando alturas externas.
     *
     * Efectos:
     * - Hace visible la torre.
     * - Muestra mensajes por JOptionPane si la operación falla.
     * - Redibuja la torre si se inserta correctamente.
     *
     * @param i número/tamaño de la copa a insertar.
     */
    public void pushCup(int i){
        String color = randomColor();
        Cup nueva = new Cup(i,color);
        if (ListLid.get(color) != null) {
            Lid lid = ListLid.get(color);
            if (lid.getNumber() != i){
              JOptionPane.showMessageDialog(null , "No se puede añadir una copa con tamaño distinto a su tapa");  
            }
            nueva.addLid(ListLid.get(color));
        }
        makeVisible();
        
        if (i > maxWidth || duplicatedSize(i)) {
            if (this.isVisible){
                JOptionPane.showMessageDialog(null , "No se puede hacer la operacion");
                isOK = false;
                return;
            }
        }
 
        if (Height() + nueva.getHeight() <= maxHeight && i < maxWidth) {
            cups.push(nueva);
            insertionOrder.add(new String[]{"cup", String.valueOf(i)}); // agregar esto
            redraw();
            isOK = true;
        }
        else {
            JOptionPane.showMessageDialog(null , "No se pudo realizar la operacion");
            isOK = false;
        }
    }          

    /**
     * Recalcula y actualiza la posición visual de todas las copas según la lógica de apilamiento:
     * - Copas más grandes pueden quedar por fuera ("outside").
     * - Copas más pequeñas pueden ir dentro ("inside") y se posicionan con un ajuste distinto.
     *
     * Efectos:
     * - Hace invisible cada copa antes de reposicionarla.
     * - Reposiciona, define inside/outside, y vuelve visibles las copas.
     * - Finalmente intenta dibujar/posicionar la tapa sobre la copa más alta.
     */
    private void redraw() {

        final int CANVAS_WIDTH = 300;
        final int GROSOR = 5;
    
        for (Cup c : cups) c.makeInvisible();
        for (Lid l : lids) l.makeInvisible();
    
        Cup outer = null;
        Stack<Cup> insideStack = new Stack<>();
    
        Map<Cup, Lid> topInsideLidByCup = new HashMap<>();
    
        // ✅ “manda afuera” por tamaño (cup o lid)
        int outsideTopSize = -1;
    
        // ✅ techo visual REAL (incluye piezas inside y outside)
        int globalTopY = Y;  // se irá volviendo más pequeño (más arriba)
    
        int baseStart = Y;
    
        for (int i = 0; i < insertionOrder.size(); i++) {
    
            String tipo = insertionOrder.get(i)[0];
            int numero = Integer.parseInt(insertionOrder.get(i)[1]);
    
            if (tipo.equals("cup")) {
    
                Cup c = findCup(numero);
                if (c == null) continue;
    
                if (outer == null) {
                    int x = (CANVAS_WIDTH - c.getWidth()) / 2;
                    c.setPosition(x, baseStart);
                    c.setInside(false);
    
                    outer = c;
                    insideStack.clear();
    
                    // actualizar “manda afuera”
                    outsideTopSize = c.getNumber();
    
                    // actualizar techo visual
                    globalTopY = Math.min(globalTopY, c.getYpo() - c.getHeight());
    
                    c.makeVisible();
                    continue;
                }
    
                boolean goesOutside = c.getNumber() >= outsideTopSize;
    
                if (goesOutside) {
                    int x = (CANVAS_WIDTH - c.getWidth()) / 2;
    
                    // ✅ afuera SIEMPRE va encima del techo visual real
                    c.setPosition(x, globalTopY);
                    c.setInside(false);
    
                    outer = c;
                    insideStack.clear();
    
                    // actualizar “manda afuera”
                    outsideTopSize = c.getNumber();
    
                    // actualizar techo visual
                    globalTopY = Math.min(globalTopY, c.getYpo() - c.getHeight());
    
                    c.makeVisible();
                } else {
                    // --- INSIDE ---
                    Cup support = null;
    
                    while (!insideStack.isEmpty() && c.getNumber() >= insideStack.peek().getNumber()) {
                        support = insideStack.pop();
                    }
    
                    Cup container = insideStack.isEmpty() ? outer : insideStack.peek();
    
                    if (support == null) {
                        placeInside(c, container);
                    } else {
                        placeAboveInContainer(c, support, container);
                    }
    
                    c.setInside(true);
                    insideStack.push(c);
    
                    // ✅ aunque sea inside, igual puede subir el techo visual
                    globalTopY = Math.min(globalTopY, c.getYpo() - c.getHeight());
    
                    c.makeVisible();
                }
            }
    
            else if (tipo.equals("lid")) {
    
                Lid l = findLid(numero);
                if (l == null) continue;
    
                Cup container = (!insideStack.isEmpty()) ? insideStack.peek() : outer;
    
                if (container == null) {
                    int x = (CANVAS_WIDTH - l.getWidth()) / 2;
                    l.setPosition(x, Y);
                    l.setInside(false);
    
                    outsideTopSize = l.getNumber();
                    globalTopY = Math.min(globalTopY, l.getYpo() - l.getHeight());
    
                    l.makeVisible();
                    continue;
                }
    
                boolean goesInside = l.getNumber() < container.getNumber();
    
                if (goesInside) {
                    int innerX = container.getXpo() + GROSOR;
                    int innerW = container.getWidth() - 2 * GROSOR;
                    int x = innerX + (innerW - l.getWidth()) / 2;
    
                    Lid topInside = topInsideLidByCup.get(container);
    
                    int y;
                    if (topInside == null) y = container.getYpo() - GROSOR;
                    else y = topInside.getYpo() - topInside.getHeight();
    
                    l.setPosition(x, y);
                    l.setInside(true);
    
                    topInsideLidByCup.put(container, l);
    
                    // ✅ lid inside también puede subir techo visual
                    globalTopY = Math.min(globalTopY, l.getYpo() - l.getHeight());
    
                    l.makeVisible();
                }
                else {
                    // --- OUTSIDE ---
                    // ✅ regla de tamaño: si es >= outsideTopSize, “manda afuera”
                    if (l.getNumber() >= outsideTopSize) outsideTopSize = l.getNumber();
    
                    // centrar sobre la cup exterior actual (outer)
                    int x = (outer != null)
                            ? outer.getXpo() + (outer.getWidth() - l.getWidth()) / 2
                            : (CANVAS_WIDTH - l.getWidth()) / 2;
    
                    // ✅ afuera va encima del techo visual real (incluye inside)
                    l.setPosition(x, globalTopY);
                    l.setInside(false);
    
                    globalTopY = Math.min(globalTopY, l.getYpo() - l.getHeight());
    
                    l.makeVisible();
                }
            }
        }
    
        isOK = true;
    }
    

    private void placeAboveInContainer(Cup upper, Cup lower, Cup container) {
        int x = container.getXpo() + (container.getWidth() - upper.getWidth()) / 2;
        int y = lower.getYpo() - lower.getHeight();   // ✅ CORRECTO
        upper.setPosition(x, y);
        upper.setInside(true);
    }
    
    private void placeInside(Cup inner, Cup outer) {
        int x = outer.getXpo() + (outer.getWidth() - inner.getWidth()) / 2;
        int y = outer.getYpo();                 // misma base
        inner.setPosition(x, y - 5);
        inner.setInside(true);
    }
    
    private void placeAboveCup(Cup upper, Cup lower) {
        int x = lower.getXpo() + (lower.getWidth() - upper.getWidth()) / 2;
        int y = lower.getYpo() - lower.getHeight();   // ✅ CORRECTO
        upper.setPosition(x, y);
        upper.setInside(false);
    }
    
    private void placeAboveLid(Lid lid, Cup orLidRef, int refX, int refY) {
        // Para lids, solo las ponemos arriba del "techo" actual de la torre.
         // refX/refY son la esquina izquierda y la base actual del tope.
        lid.setPosition(refX, refY);
        lid.setInside(false);
    }
    
    private int getTechoPiezaMenor(int numero, HashMap<Integer, Integer> techo) {
        int mejorNumero = -1;
        int mejorTecho = Y;
    
        for (Map.Entry<Integer, Integer> e : techo.entrySet()) {
            int n = e.getKey();
            if (n < numero && n > mejorNumero) {
                mejorNumero = n;
                mejorTecho = e.getValue();
            }
        }
        return mejorTecho;
    }
    
    /**
     * Verifica si ya existe una copa con el mismo tamaño/número dentro de la torre.
     *
     * @param newSize tamaño/número a validar.
     * @return true si ya existe una copa con ese número, false si no.
     */
    private boolean duplicatedSize(int newSize){
        boolean estado = false; 
        for (int i = 0; i < cups.size(); i++) {
            Cup c = cups.get(i);
            if (newSize == c.getNumber()){
                 estado = true;
            }
        }
        return estado;
    }
    
    /**
     * Remueve y retorna la copa que está en el tope del stack.
     *
     * Efectos:
     * - Oculta visualmente la copa removida.
     * - Redibuja la torre después de remover.
     * - Actualiza el estado isOK.
     *
     * @return la copa removida si existía; null si la torre está vacía.
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
     * Remueve la primera copa encontrada (desde el tope hacia abajo) cuyo número coincida.
     * Si la torre está vacía muestra un mensaje.
     * Si no encuentra la copa indicada, muestra un mensaje.
     *
     * Efectos:
     * - Oculta visualmente la copa removida (si se encuentra).
     * - Reconstruye el stack manteniendo el orden relativo de las demás copas.
     * - Redibuja la torre al final.
     *
     * @param number número/tamaño de la copa que se desea eliminar.
     */
    public void removeCup(int number) {
        if (cups.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No existe ninguna copa");
            return;
        }
    
        Stack<Cup> temp = new Stack<>();
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
     * Agrega una tapa sobre la copa del tope.
     * La implementación actual mantiene solo UNA tapa:
     * limpia el stack de tapas y agrega la nueva.
     *
     * Efectos:
     * - Si hay copas, crea una tapa asociada al número de la copa superior.
     * - Redibuja para ubicar la tapa correctamente.
     * - Si no hay copas, marca isOK = false.
     *
     * @param color color de la tapa a crear.
     */
    public void pushLid(int i,String color) {
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
     * Remueve y retorna la tapa del tope del stack de tapas.
     *
     * @return la tapa removida si existía; null si no hay tapas.
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
     * Remueve una tapa del stack
     */
    public void removeLid(int number){
        if (lids.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No existe ninguna tapa");
            return;
        }
    
        Stack<Lid> temp = new Stack<>();
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
     * Ordena la torre de mayor a menor según el número/tamaño de cada copa.
     * Implementación:
     * - Extrae los números de las copas a una lista.
     * - Oculta visualmente las copas existentes.
     * - Ordena descendentemente.
     * - Limpia el stack y reconstruye usando pushCup.
     *
     * Efectos secundarios importantes:
     * - Al reconstruir con pushCup, se crean copas nuevas y el color se asigna aleatoriamente.
     * - Se ejecutan validaciones de pushCup.
     * - Se redibuja varias veces.
     */
    public void orderTower() {
        
        ArrayList<Integer> sizes = new ArrayList<>();
        for (Cup c : cups) {
            sizes.add(c.getNumber());
            c.makeInvisible();
        }
        Collections.sort(sizes, Collections.reverseOrder());
        cups.clear();
        for (Integer size : sizes) {
            pushCup(size);
        }
        
        redraw();
    }
    
    /**
     * Invierte el orden del stack de copas (tope pasa a ser base y viceversa).
     *
     * Efectos:
     * - Reorganiza las referencias de copas en un stack temporal.
     * - Redibuja la torre al final.
     * - Marca isOK = true.
     */
    public void reverseTower() {
        Stack<Cup> temp = new Stack<>();
    
        while (!cups.isEmpty()) {
            temp.push(cups.pop());
        }
        
        Collections.reverse(insertionOrder); 
        cups = temp;
        redraw();
        isOK = true;
    }
    
    /**
     * Calcula la altura total efectiva de la torre sumando solo las copas "externas".
     * Si una copa está marcada como inside, no se suma a la altura total.
     *
     * @return altura total acumulada de copas externas.
     */
    public int Height(){
        int total = 0;
        for (Cup c : cups){
            if (c.isInside()) {
            }
            else {
                total += c.getHeight();
            }
        }
        return total;
    }

    
    /**
     * Marca la torre como visible (bandera lógica).
     * Nota: no dibuja automáticamente, solo cambia el estado.
     */
    public void makeVisible()
    {
        isVisible = true;
    }
    
    /**
     * Hace invisible la torre
     */
    public void makeInvisible()
    {
        isVisible = false;
    }
    
    /**
     * Verifica si la torre es visible
     */
    public boolean isVisible()
    {
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
    
    /**
     * Termina el simulador
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
     * Verifica si la última operación fue exitosa
     */
    public boolean isOk()
    {
        return isOK;
    }
    
    /**
     * Retorna el tamaño del stack de tazas
     */
    public int getCupsSize()
    {
        return cups.size();
    }
    
    /**
     * Retorna el tamaño del stack de tapas
     */
    public int getLidsSize()
    {
        return lids.size();
    }
    
    /**
     * Dibuja una "regla" vertical de referencia en el canvas usando rectángulos pequeños.
     * Crea múltiples objetos Rectangle y los hace visibles.
     *
     * Nota: El bucle recorre hasta maxHeight y ubica cada marca en i*10.
     * Dependiendo del valor de maxHeight, puede dibujar muchos rectángulos.
     */
    public void drawRule(){
        for (int i=0;i<=maxHeight;i = i+1){
            Rectangle r= new Rectangle();
            r.changeSize(2,10);
            r.changeP(0,i*10);
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
     * Los objetos se identifican por tipo y número: {"cup","4"} o {"lid","4"}.
     * @param o1 identificador del primer objeto [tipo, número]
     * @param o2 identificador del segundo objeto [tipo, número]
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
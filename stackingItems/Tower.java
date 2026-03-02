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

    
    
    private Lid getHighestLid(Lid actualAlta, Lid lidActual) {
        if (actualAlta == null) return lidActual;
        if (lidActual.getYpo() - lidActual.getHeight() * 5 < actualAlta.getYpo() - actualAlta.getHeight() * 5) {
            return lidActual;
        }
        return actualAlta;
    }
    
    private int posicionarLid(Lid l, Lid anterior, Lid masExterno, Lid masAlto) {
        int yActual;
    
        if (l.getNumber() > masExterno.getNumber()) {
            yActual = masAlto.getYpo() - masAlto.getHeight() * 5;  // igual que posicionarCup
            l.moveTo(X, yActual);
    
        } else if (l.getNumber() > anterior.getNumber()) {
            yActual = anterior.getYpo() - anterior.getHeight() * 5;
            l.moveTo(X, yActual);
    
        } else {
            yActual = anterior.getYpo() - 7;
            l.moveTo(X, yActual);
        }
    
        return yActual;
    }
    
    /**
     * Posiciona y muestra todas las tapas continuando desde la copa más alta,
     * como si fueran una extensión de la misma torre.
     *
     * @param cup copa más alta — punto de partida para apilar las lids.
     */
    private void drawLid(Cup cup) {
        if (lids.isEmpty()) return;
    
        int yActual;
        if (cup == null) {
            yActual = Y;         } else {
            yActual = cup.getYpo() - cup.getHeight() * 5 + 5;
        }
    
        Lid anterior = null;
        Lid masExterno = null;
        Lid masAlto = null;
    
        for (int i = 0; i < lids.size(); i++) {
            Lid l = prepararLid(i);
    
            if (anterior == null) {
                masExterno = posicionarPrimeraLid(l, yActual);
                masAlto = l;
                anterior = l;
                continue;
            }
    
            yActual = posicionarLid(l, anterior, masExterno, masAlto);
            masExterno = actualizarMasExternoLid(l, masExterno);
            masAlto = getHighestLid(masAlto, l);
    
            l.makeVisible();
            anterior = l;
        }
    }
    
    /**
     * Compara dos copas y retorna la que está más alta en pantalla (menor Y efectivo).
     * Se usa durante el redibujado para determinar cuál copa queda en la posición
     * superior entre las candidatas.
     *
     * @param actualAlta copa actualmente considerada como la más alta (puede ser null).
     * @param copaActual copa candidata a comparar.
     * @return la copa que queda más alta.
     */
    private Cup getHighest(Cup actualAlta, Cup copaActual) {

        if (actualAlta == null) {
            return copaActual;
        }
    
        if (copaActual.getYpo() - copaActual.getHeight() * 5 < actualAlta.getYpo() - actualAlta.getHeight() * 5 ) {
            return copaActual;
        }
    
        return actualAlta;
    }

    private Cup prepararCup(int i) {
        Cup c = cups.get(i);
        c.makeInvisible();
        return c;
    }
    
    private Cup posicionarPrimera(Cup c, int yActual) {
        c.setPosition(X, yActual);
        c.setInside(false);
        c.makeVisible();
        return c;
    }
    
    private int posicionarCup(Cup c,Cup anterior,Cup masExterno,Cup masAlto) {

        int yActual;
    
        if (c.getNumber() > masExterno.getNumber()) {
    
            yActual = masAlto.getYpo() - masAlto.getHeight() * 5;
            c.setPosition(X, yActual);
            c.setInside(false);
    
        } else if (c.getNumber() > anterior.getNumber()) {
    
            yActual = anterior.getYpo() - anterior.getHeight() * 5;
            c.setPosition(X, yActual);
            c.setInside(false);
    
        } else {
    
            int baseInteriorAnterior = anterior.getYpo();
            yActual = baseInteriorAnterior - 7;
    
            c.setInside(true);
            c.setPosition(X, yActual);
        }
    
        return yActual;
    }
    
    private Cup actualizarMasExterno(Cup c, Cup masExterno) {
        if (c.getNumber() > masExterno.getNumber()) {
            return c;
        }
        return masExterno;
    }
    
    /**
     * Retorna el Y más bajo ocupado por las lids (el mayor yPosition).
     * Si no hay lids, retorna Y base de la torre.
     */
    private int getBottomOfLids() {
        int maxY = Integer.MIN_VALUE;
        for (Lid l : lids) {
            if (l.getYpo() > maxY) {
                maxY = l.getYpo();
            }
        }
        return maxY + 5; // +5 de margen para no solaparse
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
        int yActual = Y;
        
        if (!lids.isEmpty() && cups.isEmpty() ) {
            Lid ultima = lids.peek();
            yActual = ultima.getYpo() - 15;
        }
        
        Cup anterior = null;
        Cup masExterno = null;
        Cup masAlto = null;
    
        for (int i = 0; i < cups.size(); i++) {
    
            Cup c = prepararCup(i);
    
            if (anterior == null) {
                masExterno = posicionarPrimera(c, yActual);
                masAlto = c;
                anterior = c;
                continue;
            }
    
            yActual = posicionarCup(c,anterior,masExterno,masAlto);
    
            masExterno = actualizarMasExterno(c, masExterno);
            masAlto = getHighest(masAlto, c);
    
            c.makeVisible();
            anterior = c;
        }
    
        if (cups.isEmpty()) {
                drawLid(null);
        }
    }
        
    /**
     * Prepara una tapa haciéndola invisible antes de reposicionarla.
     */
    private Lid prepararLid(int i) {
        Lid l = lids.get(i);
        l.makeInvisible();
        return l;
    }
    
    /**
     * Posiciona la primera tapa directamente sobre la copa más alta.
     */
    private Lid posicionarPrimeraLid(Lid l, int yActual) {
        l.moveTo(X, yActual);
        l.makeVisible();
        return l;
    }
    
    /**
     * Posiciona una tapa según su tamaño relativo a la anterior y la más externa.
     *
     * @return el nuevo yActual resultante.
     */
    private int posicionarLid(Lid l, Lid anterior, Lid masExterno) {
        int yActual;
    
        if (l.getNumber() > masExterno.getNumber()) {
            yActual = masExterno.getYpo() - masExterno.getHeight() * 5;
            l.moveTo(X, yActual);
    
        } else if (l.getNumber() > anterior.getNumber()) {
            yActual = anterior.getYpo() - anterior.getHeight() * 5;
            l.moveTo(X, yActual);
    
        } else {
            yActual = anterior.getYpo() - 7;
            l.moveTo(X, yActual);
        }
    
        return yActual;
    }

    /**
     * Actualiza cuál es la tapa más externa según el tamaño.
     */
    private Lid actualizarMasExternoLid(Lid l, Lid masExterno) {
        if (l.getNumber() > masExterno.getNumber()) {
            return l;
        }
        return masExterno;
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
    public Cup popCup()
    {
        if (!cups.isEmpty()) {

            Cup removida = cups.pop();
            removida.makeInvisible();

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
    
        // Buscar si hay una copa del mismo color para asociar la tapa
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
        makeVisible();
        redraw();
        isOK = true;

    }
    
    /**
     * Remueve y retorna la tapa del tope del stack de tapas.
     *
     * @return la tapa removida si existía; null si no hay tapas.
     */
    public Lid popLid(){
            if (!lids.isEmpty()) {
            Lid removida = lids.pop();
            removida.makeInvisible();
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
                ListLid.remove(l.getColor()); // limpiar del mapa también
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
}
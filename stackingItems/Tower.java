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
    private boolean isOK;
    private Random random;
    
    
    /**
     * Construye una torre vacía con límites máximos de altura y ancho.
     *
     * @param nmaxHeight altura máxima permitida para la torre (suma de alturas externas).
     * @param nmaxWidth  tamaño máximo permitido para una copa (número/diámetro).
     */
    public Tower(int nmaxHeight,int nmaxWidth)
    {
        cups = new Stack<Cup>();
        lids = new Stack<Lid>();

        this.maxHeight = nmaxHeight;
        this.maxWidth = nmaxWidth;

        isVisible = false;
        isOK = true;
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
        List<String> lista = List.of("magenta", "green", "yellow","blue","black","red");
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
            redraw();
            isOK = true;
        }
        else {
            JOptionPane.showMessageDialog(null , "No se pudo realizar la operacion");
            isOK = false;
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
    
        drawLid(masAlto);
    }
        
    /**
     * Posiciona y muestra la tapa actual (si existe) sobre la copa indicada.
     * Si no hay tapas en el stack, no hace nada.
     * Si la copa es null, oculta la tapa.
     *
     * @param cup copa sobre la cual se posicionará la tapa (normalmente la más alta).
     */
    private void drawLid(Cup cup) {
        if (lids.isEmpty()) return;
    
        Lid lid = lids.peek();
    
        if (cup == null) {
            lid.makeInvisible();
            return;
        }
    
        int xLid = cup.getXpo();
        int yLid = cup.getYpo() ; // ajuste fino (altura de la tapa)
    
        lid.moveTo(xLid, yLid);
        lid.makeVisible();
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
    public void pushLid(String color) {
        if (!cups.isEmpty()) {
            Cup top = cups.peek();
    
            Lid nueva = new Lid(top.getNumber(), color); 
    
            lids.clear();
            lids.push(nueva);
    
            isOK = true;
            redraw(); 
        } else {
            isOK = false;
        }
    }
    
    /**
     * Remueve y retorna la tapa del tope del stack de tapas.
     *
     * @return la tapa removida si existía; null si no hay tapas.
     */
    public Lid popLid()
    {
        if (!lids.isEmpty()) {
            isOK = true;
            return lids.pop();
         } else {
            isOK = false;
            return null;
        }
    }
    
    /**
     * Remueve una tapa del stack
     */
    public void removeLid(Lid lid)
    {
        if (lids.remove(lid)) {
            isOK = true;
        } else {
            isOK = false;
        }
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
    public void exit()
    {
        for (Cup c:cups){
            c.makeInvisible();
        }
        for (Lid l:lids){
            l.makeInvisible();
        }
        cups.clear();
        lids.clear();
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
}
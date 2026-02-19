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
     * Constructor for objects of class Tower
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

    private String randomColor(){
        random = new Random ();
        List<String> lista = List.of("magenta", "green", "yellow","blue","black","red");
        return  lista.get(random.nextInt(lista.size()));
    }
    
    /**
     * Agrega una taza al tope de la torre
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
 
        if (getHeight() + nueva.getHeight() <= maxHeight && i < maxWidth) {
            cups.push(nueva);
            redraw();
            isOK = true;
        }
        else {
            JOptionPane.showMessageDialog(null , "No se pudo realizar la operacion");
            isOK = false;
        }
    }          

    private Cup getHighest(Cup actualAlta, Cup copaActual) {

        if (actualAlta == null) {
            return copaActual;
        }
    
        if (copaActual.getYpo() - copaActual.getHeight() * 5 < actualAlta.getYpo() - actualAlta.getHeight() * 5 ) {
            return copaActual;
        }
    
        return actualAlta;
    }

    private void redraw() {
        int yActual = Y;
        Cup anterior = null;
        Cup masExterno = null;
        Cup masAlto = null; 
         
        for (int i = 0;i < cups.size();i++ ){
            Cup c = cups.get(i);
            c.makeInvisible();
            if (anterior == null) {
                c.setPosition(X, yActual);
                c.setInside(false);
                masExterno = c; 
                masAlto = c;
                anterior = c;
                c.makeVisible();
                continue;
            }
            
            if (c.getNumber() > masExterno.getNumber()){
                yActual = masAlto.getYpo() - masAlto.getHeight() * 5;
                c.setPosition(X, yActual);
                c.setInside(false);
                masExterno = c;
                masAlto = getHighest(masAlto, c);
            } else{
                if (c.getNumber() > anterior.getNumber() ) {
                    
                    yActual = anterior.getYpo() - anterior.getHeight() * 5;
                    c.setInside(false);
                    c.setPosition(X, yActual);
                    masAlto = getHighest(masAlto, c);
                } else {
                    
                    int baseInteriorAnterior = anterior.getYpo() ;
                    yActual = baseInteriorAnterior - 7;
                     c.setInside(true);
                    c.setPosition(X, yActual);
                }
            }
            c.makeVisible();
            anterior = c;
        }
        drawLid(masAlto);
        }
        

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
     * Remueve y retorna la taza del tope
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
     * Remueve una taza específica por número
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
            } else {
                temp.push(c);
            }
        }
    
       
        if (!found) {
            JOptionPane.showMessageDialog(null, "No existe la copa indicada");
        }
    
        
        while (!temp.isEmpty()) {
            cups.push(temp.pop());
        }
    
        redraw();
    }
    
    /**
     * Agrega una tapa al tope de la torre
     */
    public void pushLid(String color) {
        if (!cups.isEmpty()) {
            Cup top = cups.peek();
    
            Lid nueva = new Lid(top.getNumber(), color); // ✅ NO getWidth()
    
            lids.clear();
            lids.push(nueva);
    
            isOK = true;
            redraw(); // ✅ para que se posicione con las copas
        } else {
            isOK = false;
        }
    }
    
    /**
     * Remueve y retorna la tapa del tope
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
    
    public void removeLid(Cup removedCup){
        Lid tapaDeLaTaza = removedCup.getTapa();
        removeLid(tapaDeLaTaza);
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
     * Ordena la torre de mayor a menor altura
     */
    public void rebuildTower() {
        
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
     * Invierte el orden de la torre
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
     * Retorna la altura total de elementos apilados
     */
    public int getHeight(){

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
     * Retorna array con números de tazas tapadas
     */
    public int[] lidedCups()
    {
        ArrayList<Integer> covered = new ArrayList<Integer>();
        
        for (Cup c : cups) {
            if (c.cubierto()) {
                covered.add(c.getNumber());
            }
        }
        int[] result = new int[covered.size()];
        for (int i = 0; i < covered.size(); i++) {
            result[i] = covered.get(i);
        }
        return result;
    }

    
    /**
     * Hace visible la torre
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
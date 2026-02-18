import java.util.Stack;
import java.util.ArrayList;

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
    private int minHeight;
    private int maxHeight;
    private int Y = 200;
    private int X = 130;
    private boolean isVisible;
    private Stack<Cup> cups;
    private Stack<Lid> lids;
    private boolean isOK;
    
    /**
     * Constructor for objects of class Tower
     */
    public Tower(int nmaxHeight)
    {
        cups = new Stack<Cup>();
        lids = new Stack<Lid>();

        this.maxHeight = nmaxHeight;

        isVisible = false;
        isOK = true;
    }
    
    
    /**
     * Agrega una taza al tope de la torre
     */
    public void pushCup(int i,String color){
        Cup nueva = new Cup(i, color);

        if (getHeight() + nueva.getHeight() <= maxHeight) {
            cups.push(nueva);
            redraw();
            isOK = true;
        }
        else {
            isOK = false;
        }
    }
    
    private void redraw(){
        int yActual = Y;
        Cup anterior = null;
        
        for (int i = 0; i < cups.size(); i++) {
            Cup c = cups.get(i);
            c.makeInvisible();
        
            if (anterior == null) {
                c.setPosition(X, yActual);
            } else {
                int grosor = 7;
        
                if (c.getNumber() > anterior.getNumber()) {
                    // más grande: se apila encima de la anterior
                    yActual = anterior.getYpo() - anterior.getHeight() * 5;
                    c.setPosition(X, yActual);
                } else {
                    // más pequeña: dentro, tocando la base interior de la anterior
                    int baseInteriorAnterior = anterior.getYpo() ;
                    yActual = baseInteriorAnterior - 7;
                    c.setPosition(X, yActual);
                }
            }
        
            c.makeVisible();
            anterior = c;
        }
            
        if (!lids.isEmpty()) {
                Lid topLid = lids.peek();
                topLid.makeInvisible();
    
                int alturaTotal = getHeight();
                int yTapa = Y - alturaTotal;
    
                topLid.setPosition(X, yTapa + 15);

                if (isVisible) {
                    topLid.makeVisible();
                }
        }
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
    public void removeCup(int i)
    {
        Stack<Cup> temp = new Stack<Cup>();
        Cup removedCup = null;
        boolean found = false;
        boolean isEmpty=cups.isEmpty();
        while (!isEmpty) {
            Cup c = cups.pop();
            int number=c.getNumber(); 
            if (number != i) {
                temp.push(c);
            } else {
                found = true;
                removedCup=c;
            }
        }
        boolean tIsEmpty=temp.isEmpty();
        while (!tIsEmpty) {
            cups.push(temp.pop());
        } 
        if (found && removedCup != null) {
           removeLid(removedCup);
        }
        isOK = found;
    }
    
    /**
     * Agrega una tapa al tope de la torre
     */
    public void pushLid(String color)
    {
        if (!cups.isEmpty()) {

            Cup top = cups.peek();
            Lid nueva = new Lid(top.getWidth(), color);
            lids.clear(); // Solo permitimos 1 tapa arriba
            lids.push(nueva);
            nueva.draw();
            isOK = true;
        }
        else {
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
    public void orderTower()
    {
        ArrayList<Cup> temp = new ArrayList<Cup>(cups);
        for (int i = 0; i < temp.size(); i++) {
            for (int j = 0; j < temp.size() - 1; j++) {
                if (temp.get(j).getNumber() < temp.get(j + 1).getNumber()) {
                    Cup aux = temp.get(j);
                    temp.set(j, temp.get(j + 1));
                    temp.set(j + 1, aux);
                }
            }
        }
        cups.clear();
        for (Cup c : temp) {
            cups.push(c);
        }
        System.out.println(cups);
        isOK = true;
    }
    
    /**
     * Invierte el orden de la torre
     */
    public void reverseTower()
    {
        Stack<Cup> temp = new Stack<Cup>();
        while (!cups.isEmpty()) {
            temp.push(cups.pop());
        }
        cups = temp;
        isOK = true;
    }
    
    /**
     * Retorna la altura total de elementos apilados
     */
    public int getHeight()
    {
        int totalHeight = 0;
        for (Cup c : cups) {
            totalHeight += c.getHeight();
        }        
        return totalHeight;
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
     * Retorna matriz con tipo y número de elementos
     */
    //public String[][] stackingItems()
    //{
        
    //}
    
    /**
     * Hace visible la torre
     */
    public void makeVisible()
    {
        isVisible = true;
        redraw();
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
import java.util.*;

/**
 * Write a description of class Cup here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Cup
{
    // instance variables - replace the example below with your own
    private int number;
    private int height;
    private int max;
    private int min;
    private int posx;
    private int posy;
    private String estado;
    private String color;
    private boolean isVisible;
    private Lid tapa;
    public Rectangle body;
    public Rectangle interior;
    
    /**
     * Constructor for objects of class Cup
     */
    public Cup(int tamano)
    {
        body = new Rectangle();
        interior = new Rectangle();
        interior.setPosition(150-(tamano/2));
        body.setPosition(150-(tamano/2));
        body.changeSize(tamano, tamano);
        body.changeColor("red");
        estado = "noCovered";
        
        int tamanoInterior = (int)(tamano * 0.8);
        int offset = (tamano - tamanoInterior) / 2; 

        interior.changeSize(tamanoInterior + 5, tamanoInterior );  
        interior.changeColor("white");
        interior.moveHorizontal(offset);
     
    }
    
    public int getMin() {
        return min;
    }
    
    public int getMax() {
        return max;
    }

    public boolean cubierto() {
        return estado=="Covered";
    }
    
    public int getHeight(){
        return height;
    }
    
    public void draw()
    {
        body.makeVisible();
        interior.makeVisible();
        isVisible = true;
    }
    
    public void erase()
    {
        body.makeInvisible();
        interior.makeInvisible();
        isVisible = false;
    }
    
    public int getNumber()
    {
        return number;
    }
    
     public void setState(String nstate) {
        estado = nstate;
    }
    
      private String randomColor(){
        String[] colors = {"red","black","blue","yellow","green","magenta"};
        Random random = new Random();
        int index = random.nextInt(colors.length);
        return colors[index];
    }
    
    public Lid getCover() {
        return tapa;
    }
    
}
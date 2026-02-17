
/**
 * Write a description of class Lid here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Lid
{
    // instance variables - replace the example below with your own
    private int number;
    private int width;
    private String color;
    private static int height = 5;
    private int posx;
    private int posy;
    private String state;
    private boolean isVisible;
    private Cup cup;
    private Rectangle cuerpo;
    private static final int PIXEL_POR_CM = 5;
    /**
     * Constructor for objects of class Lid
     */
    public Lid(int nwidth, String ncolor)
    {
        width = calcularHeight(nwidth);
        posx = 150-(nwidth/2);
        posy = 150-(nwidth/2);
        state = "normal";
        isVisible = false;
        color = ncolor;
    }

    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void draw(){
        cuerpo=new Rectangle();
        cuerpo.setPosition(150-(width/2));
        cuerpo.changeSize(height,width);
        cuerpo.changeColor(color);
        cuerpo.makeVisible();
    }
    
    public int calcularHeight(int number) {
        return ((2 * number) - 1) * PIXEL_POR_CM;
    }
    
    public void erase(){
        cuerpo.makeInvisible();
        isVisible = false;
    }
    
    public int getNumber()
    {
        return number;
    }
        
}
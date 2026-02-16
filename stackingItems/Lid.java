
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
    private static int height = 1;
    private int posx;
    private int posy;
    private String state;
    private boolean isVisible;
    private Cup cup;
    private Rectangle cuerpo;
    /**
     * Constructor for objects of class Lid
     */
    public Lid(int nwidth, String ncolor, Cup ncup)
    {
        width = nwidth;
        posx = 0;
        posy = 0;
        state = "normal";
        isVisible = false;
        color = ncolor;
        cup=ncup;
    }

    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void draw(){
        cuerpo=new Rectangle();
        cuerpo.changeColor(color);
        cuerpo.changeSize(height*10,width);
        posx=cup.body.getXpo();
        posy=cup.body.getYpo();
        cuerpo.changeP(posy,posx);
        cuerpo.makeVisible();
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
import java.util.*;

/**
 * Write a description of class Cup here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Cup{
    // instance variables - replace the example below with your own
    private int number;
    private int height;
    private int width;
    private int xPosition;
    private int yPosition;
    private String color;
    private boolean isVisible;
    private boolean hasLid;
    private Lid tapa;
    private static final int PIXEL_POR_CM = 5;
    
    public Rectangle body;
    public Rectangle interior;
    

    /**
     * Constructor for objects of class Cup
     */
    public Cup(int number,String color) {
        this.number = number;
        this.height = calcularHeight(number);
        this.color = color;
        this.xPosition = 130; 
        this.yPosition = 200;
        this.width = calcularHeight(number);
        this.isVisible = false;
        
        this.hasLid = false; //esto es para indicar que por defecto ninguna copa viene con tapa (Lid)
    }
    
    public int calcularHeight(int number) {
        return ((2 * number) - 1) * PIXEL_POR_CM;
    }
    
    public void makeVisible() {
        isVisible = true;
        draw();
    }
    
    public void makeInvisible() {
        erase();
        isVisible = false;
    }
    
    public void moverVertical(int distancia) {
        erase();
        yPosition += distancia;
        draw();
    }
    
    public void moveTo(int x, int y) {
        erase();
        xPosition = x;
        yPosition = y;
        draw();
    }
    
    public int getNumber() {
        return number;
    }
    
    public int getHeight() {
        return height/5;
    }
    
    public void setPosition(int x, int y) {
        xPosition = x;
        yPosition = y;
    }
    
    public void gotALid() {
        hasLid = true;
    }
    
    public void draw() {
        if (isVisible) {
            int grosor = 7;

            int esquinaX = xPosition - width / 2;
            int esquinaY = yPosition - height;

            body = new Rectangle();
            body.changeSize(height, width);
            body.changeColor(color);
            body.moveHorizontal(esquinaX - 70);
            body.moveVertical(esquinaY);
            body.makeVisible();
            
            interior = new Rectangle();
            interior.changeSize(height - grosor, width - 2 * grosor);
            interior.changeColor("white");
            interior.moveHorizontal((esquinaX + grosor) - 70);
            interior.moveVertical(esquinaY);
            interior.makeVisible();
        }
    }
    
    public void erase() {
        if (body != null) body.makeInvisible();
        if (interior != null) interior.makeInvisible();
    }
    
    public Lid getTapa(){
        return tapa;
    }
    
    public boolean cubierto(){
        return hasLid;
    }
    
    public int getXpo(){
        return xPosition;
    }
    
    public int getYpo(){
        return yPosition;
    }
    
    public int getWidth(){
        return width;
    }
}
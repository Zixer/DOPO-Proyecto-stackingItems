import java.awt.*;
/**
 * Write a description of class Lid here.
 * 
 * @author Yazid Sánchez - Sergio Ramírez
 * @version 1.0
 */
public class Lid
{
    private int number;
    private int height;
    private int width;
    private int xPosition;
    private int yPosition;
    private String color;
    private boolean isVisible;
    private static final int PIXEL_POR_CM = 5;
    private Rectangle lid;
    /**
     * Constructor for objects of class Lid
     */
    public Lid(int number, String color) {
        this.number = number;
        this.color = color;
        this.width = ((2 * number) - 1) * PIXEL_POR_CM;

        this.height = PIXEL_POR_CM; 
        
        this.xPosition = 130; 
        this.yPosition = 200;
        this.isVisible = false;
    }
    
    /**
     * Hace visible la tapa en pantalla.
     */
    public void makeVisible() {
        isVisible = true;
        draw();
    }
    
    /**
     * Hace invisible la tapa en pantalla.
     */
    public void makeInvisible() {
        erase();
        isVisible = false;
    }
    
    /**
     * Mueve la tapa verticalmente
     * 
     * @param distancia distancia en píxeles
     */
    public void moveVertical(int distancia) {
        erase();
        yPosition += distancia;
        draw();
    }
    
    /**
     * Mueve la tapa a una posición absoluta específica
     * 
     * @param x Nueva posición horizontal
     * @param y Nueva posición vertical
     */
    public void moveTo(int x, int y) {
        erase();
        xPosition = x;
        yPosition = y;
        draw();
    }
    
    /**
     * Devuelve el número 
     * 
     * @return Número identificador
     */
    public int getNumber() {
        return number;
    }
    
    public int getSize() {
        return number;
    }
    
    /**
     * Establece una nueva posición sin redibujar de manera inmediata
     * @param x Nueva posición horizontal
     * @param y Nueva posición vertical
     */
    public void setPosition(int x, int y) {
        xPosition = x;
        yPosition = y;
    }
    
    /**
     * Dibuja la tapa en pantalla utilizando un rectángulo.   
    */
    public void draw() {
        if (isVisible) {
            int esquinaX = xPosition - width / 2;
            int esquinaY = yPosition - height * 2 * (number); 
            
            lid = new Rectangle();
            lid.changeSize(height, width);
            lid.changeColor(color);
            lid.moveHorizontal(esquinaX - 70);
            lid.moveVertical(esquinaY);
            lid.makeVisible();
        }
    }
    
    /**
     * Elimina la tapa en pantalla
     */
    public void erase() {
        if (lid != null) lid.makeInvisible();
    }
}
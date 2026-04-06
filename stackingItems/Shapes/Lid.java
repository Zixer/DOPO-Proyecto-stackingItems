package Shapes;
import java.awt.*;

public abstract class Lid
{
    private int number;
    private int height;
    private int width;
    private int xPosition;
    private int yPosition;
    private String color;
    private boolean isVisible;
    private static final int cm = 5;
    private Rectangle lid;
    private boolean inside;
    /**
     * Constructor for objects of class Lid
     */
    public Lid(int number, String color) {
        this.number = number;
        this.color = color;
        this.width = ((2 * number) - 1) * cm;
        this.height = 4; 
        this.xPosition = (150 - (((2 * width) - 1) * 5)/2) ; 
        this.yPosition = (150 - (((2 * height) - 1) * 5)/2);
        this.isVisible = false;
    }

    public int getHeight() {
        return height;
    }
    
    public int getXpo(){
        return xPosition;
    }
    
    public int getYpo(){
        return yPosition;
    }
    
    /**
     * Hace visible la tapa en pantalla.
     */
    public void makeVisible() {
        isVisible = true;
        draw();
    }

    public void setInside(boolean inside) {
        this.inside = inside;
    }
    
    public boolean isInside() {
        return this.inside;
    }
    
    
    /**
     * Hace invisible la tapa en pantalla.
     */
    public void makeInvisible() {
        erase();
        isVisible = false;
    }
    
    public String getColor(){
        return this.color;
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
     * Dibuja la tapa sobre el canvas utilizando un rectángulo.
     *
     * Si el objeto está marcado como visible, crea un nuevo Rectangle
     * que representa la tapa y lo posiciona horizontalmente centrado
     * dentro del canvas (300 px de ancho).
     *
     * La posición vertical depende del valor actual de yPosition,
     * permitiendo que la tapa siga la altura de la torre.
     *
     * Características:
     * - La tapa siempre queda centrada horizontalmente en el canvas.
     * - Solo cambia su posición vertical según la torre.
     * - Se crea un nuevo objeto gráfico cada vez que se dibuja.
     */
    public void draw() {
        if (!isVisible) return;
    
        erase();
    
        lid = new Rectangle();
        lid.changeSize(height, width);
        lid.changeColor(color);
        lid.changeP(xPosition, yPosition - height);
        lid.makeVisible();
    }
    
    public int getWidth() {
        return width;
    }
    
    /**
     * Elimina la tapa en pantalla
     */
    public void erase() {
        if (lid != null) lid.makeInvisible();
    }
    
    public boolean isVisible(){
        return isVisible;
    }
    
    public void placeOutside(int canvasWidth, int baseY) {
        int x = (canvasWidth - getWidth()) / 2;
        setPosition(x, baseY);
        setInside(false);
    }
    
    public void placeInside(Cup container, Lid topInside, int grosor) {
        if (container == null) return;
    
        int innerX = container.getXpo() + grosor;
        int innerWidth = container.getWidth() - (2 * grosor);
        int x = innerX + (innerWidth - getWidth()) / 2;
    
        int y;
        if (topInside == null) {
            y = container.getYpo() - grosor;
        } else {
            y = topInside.getYpo() - topInside.getHeight();
        }
    
        setPosition(x, y);
        setInside(true);
    }    
    
    public void placeOnCup(Cup cup) {
        if (cup == null) return;
        int x = cup.getXpo();
        int y = cup.getYpo() - cup.getHeight();
        setPosition(x, y);
        setInside(cup.isInside());
    }
}
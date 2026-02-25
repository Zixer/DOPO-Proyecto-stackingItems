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
    private static final int cm = 5;
    public boolean inside;
    public Rectangle body;
    public Rectangle interior;
    

    /**
     * Constructor for objects of class Cup
     */
    public Cup(int number,String color) {
        this.number = number;
        this.height = calcularHeight(number);
        this.color = color;
        this.xPosition = (150 - (((2 * height) - 1) * 5)/2); 
        this.yPosition = (150 - (((2 * height) - 1) * 5)/2);
        this.width = calcularHeight(number);
        this.isVisible = false;
        
        this.hasLid = false; //esto es para indicar que por defecto ninguna copa viene con tapa (Lid)
    }
    
    /**
     * calcula la altura teniendo en cuenta los "centimetros"
     */
    public int calcularHeight(int number) {
        return ((2 * number) - 1) * cm;
    }
    
    public boolean isInside() {
        return inside;
    }

    public void setInside(boolean value) {
        inside = value;
    }
    
    public void makeVisible() {
        isVisible = true;
        draw();
    }
    
    public void makeInvisible() {
        erase();
        isVisible = false;
    }
    
    /**
     * Mueve el objeto verticalmente una distancia relativa respecto
     * a su posición actual.
     *
     * El objeto se borra del canvas, se actualiza la posición vertical
     * sumando la distancia indicada y luego se vuelve a dibujar.
     *
     * @param distancia cantidad de píxeles a mover:
     *                  valores positivos → hacia abajo
     *                  valores negativos → hacia arriba
     */
    public void moverVertical(int distancia) {
        erase();
        yPosition += distancia;
        draw();
    }
    
    /**
     * Mueve el objeto directamente a una posición absoluta dentro del canvas.
     *
     * El objeto se borra del canvas, se asignan las nuevas coordenadas
     * X e Y y luego se vuelve a dibujar en la nueva ubicación.
     *
     * @param x nueva posición horizontal absoluta.
     * @param y nueva posición vertical absoluta.
     */
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
    
    /**
     * Establece directamente la posición del objeto sin redibujarlo.
     *
     * Actualiza las coordenadas internas X y Y del objeto,
     * pero NO realiza operaciones gráficas como borrar o dibujar.
     * El cambio solo será visible cuando se invoque posteriormente draw().
     *
     * @param x nueva posición horizontal absoluta.
     * @param y nueva posición vertical absoluta.
     */
    public void setPosition(int x, int y) {
        xPosition = x;
        yPosition = y;
    }
    
    public void gotALid() {
        hasLid = true;
    }
    
    /**
     * Dibuja visualmente la copa en el canvas si el objeto está marcado como visible.
     *
     * El método calcula la esquina superior izquierda a partir de la posición central
     * (xPosition, yPosition) y crea dos rectángulos:
     *
     * - body: representa el exterior de la copa con el color asignado.
     * - interior: representa el interior blanco de la copa, ligeramente más pequeño
     *   para simular el grosor de las paredes.
     *
     * El valor "grosor" define el espesor visual del borde.
     *
     * Nota:
     * - Se crean nuevos objetos Rectangle cada vez que se dibuja.
     * - El desplazamiento horizontal (-70) corresponde a un ajuste manual
     *   para alinear la copa dentro del canvas.
     * - El objeto solo se dibuja si isVisible == true.
     */
    public void draw() {
        if (isVisible) {
            int grosor = 7;

            int esquinaX = xPosition - width / 2;
            int esquinaY = yPosition - height;

            body = new Rectangle();
            body.changeSize(height, width);
            body.changeColor(color);
            body.moveHorizontal(esquinaX - 50);
            body.moveVertical(esquinaY);
            body.makeVisible();
            
            interior = new Rectangle();
            interior.changeSize(height - grosor, width - 2 * grosor);
            interior.changeColor("white");
            interior.moveHorizontal((esquinaX + grosor) - 50);
            interior.moveVertical(esquinaY);
            interior.makeVisible();
        }
    }
    
    /**
     * Oculta visualmente la copa del canvas.
     *
     * El método verifica si los rectángulos gráficos que representan
     * el exterior (body) y el interior (interior) existen y, en caso afirmativo,
     * los hace invisibles.
     */
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
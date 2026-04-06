package Shapes;
import tower.*;
import java.util.*;

/**
 * Write a description of class Cup here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public abstract class Cup{
    // instance variables - replace the example below with your own
    private int number;
    private int height;
    private int width;
    private int xPosition;
    private int yPosition;
    private String color;
    private boolean isVisible;
    private static final int cm = 5;
    public boolean inside;
    public Rectangle body;
    public Rectangle interior;
    public Lid lid;
    

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
        this.lid =null;
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

    public void addLid(Lid lid){
        this.lid = lid;
    }
    
    public Lid getLid(){
        return this.lid;    
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
        return this.height;
    }
    
    public int realHeight(){
        int number =0 ;
        number = height/5;
        number = number +1;
        number = number/2;
        return number;
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
    
    public String getColor(){
        return this.color;
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
        if (!isVisible) return;
    
        erase();
    
        int grosor = 5;
    
        int esquinaX = xPosition;
        int esquinaY = yPosition - height;
    
        body = new Rectangle();
        body.changeSize(height, width);
        body.changeColor(color);
        body.changeP(esquinaX, esquinaY);
        body.makeVisible();
    
        interior = new Rectangle();
        interior.changeSize(height - grosor, width - 2 * grosor);
        interior.changeColor("white");
        interior.changeP(esquinaX + grosor, esquinaY);
        interior.makeVisible();
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
    
    public int getXpo(){
        return xPosition;
    }
    
    public int getYpo(){
        return yPosition;
    }
    
    public int getWidth(){
        return width;
    }
    
    public void placeOutside(int canvasWidth, int baseY) {
        int x = (canvasWidth - getWidth()) / 2;
        setPosition(x, baseY);
        setInside(false);
    }
        
    public void placeInside(Cup container, Lid topInsideLid, int grosor) {
        if (container == null) return;
        int innerX = container.getXpo() + grosor;
        int innerWidth = container.getWidth() - (2 * grosor);
        int x = innerX + (innerWidth - getWidth()) / 2;
        int y;
        if (topInsideLid == null) {
            y = container.getYpo() - grosor;
        } else {
            y = topInsideLid.getYpo() - topInsideLid.getHeight();
        }
        setPosition(x, y);
        setInside(true);
    }
    
        public void placeAbove(Cup support, Cup container, Lid lidOnSupport, int grosor) {
        if (support == null || container == null) return;
        int innerX = container.getXpo() + grosor;
        int innerWidth = container.getWidth() - (2 * grosor);
        int x = innerX + (innerWidth - getWidth()) / 2;
        int y;
        if (lidOnSupport == null) {
            y = support.getYpo() - support.getHeight();
        } else {
            y = lidOnSupport.getYpo() - lidOnSupport.getHeight();
        }
        setPosition(x, y);
        setInside(true);
    }
    
    public void beforeEnter(Tower tower){
    }
    
    public boolean canBeRemoved() {
        return true;
    }
    
    public void lock() {
    }
}
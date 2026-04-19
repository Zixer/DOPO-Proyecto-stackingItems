package tower;
import Shapes.*;


public  class Lid
{
    private int number;
    private int height;
    private int width;
    private int xPosition;
    private int yPosition;
    private String color;
    private boolean isVisible;
    private static final int cm = 5;
    protected Rectangle lid;
    private boolean inside;
    private Cup partnerCup;
    
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
    
    public int getwidth() {
        return width;
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
    
    public void setPartnerCup(Cup cup) {
        this.partnerCup = cup;
    }
    
    public Cup getPartnerCup() {
        return this.partnerCup;
    }
    
    public boolean hasPartnerCup() {
        return partnerCup != null;
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
    
    /**
     * Posiciona la tapa fuera de la torre.
     */
    public void placeOutside(int canvasWidth, int baseY) {
        int x = (canvasWidth - getWidth()) / 2;
        setPosition(x, baseY);
        setInside(false);
    }
    
    /**
     * Posiciona la tapa dentro de una copa.
     */
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
    
    /**
     * Posiciona la tapa sobre una copa interna.
     */
    public void placeAboveCup(Cup support, Cup container, Lid lidOnSupport, int grosor) {
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
    
    /**
     * Posiciona la tapa sobre otra tapa.
     */
    public void placeAboveLid(Lid support) {
        if (support == null) return;
        int x = support.getXpo() + (support.getWidth() - getWidth()) / 2;
        int y = support.getYpo() - support.getHeight();
        setPosition(x, y);
        setInside(false);
    }
    
    /**
     * Posiciona la tapa directamente sobre su copa asociada.
     */
    public void placeOnCup(Cup cup, Lid lidOnCup) {
        if (cup == null) return;    
        int x = cup.getXpo() + (cup.getWidth() - getWidth()) / 2;
        int y;
        if (lidOnCup == null) {
            y = cup.getYpo() - cup.getHeight();
        } else {
            y = lidOnCup.getYpo() - lidOnCup.getHeight();
        }
        setPosition(x, y);
        setInside(true);
    }
    
    public boolean canEnter(Tower tower) {
        return true;
    }

    public boolean canExit(Tower tower) {
        return true;
    }

    public boolean shouldBeBeforeCup() {
        return false;
    }
    
    public void reorderInTower(Tower tower) {
        // comportamiento por defecto: no hace nada
    }
    
    public boolean actsAsBaseForPartner() {
        return false;
    }
    
    /**
     * Rompe la relación entre la tapa y su copa asociada.
     */
    public void detachFromPartner() {
        if (partnerCup != null) {
            Cup temp = partnerCup;
            partnerCup = null;
            if (temp.getLid() == this) {
                temp.detachLid();
            }
        }
    }
    
    /**
     * Decide si la tapa debe colocarse fuera de la torre.
     */
    public boolean shouldGoOutside(boolean lastOutsideWasLid, Cup container, int outsideSize) {
        return lastOutsideWasLid || container == null || this.getNumber() >= outsideSize;
    }
    
    /**
     * Posiciona la tapa como primer elemento.
     */
    public void placeAsFirst(int canvasWidth, int highestCupTopY, Lid topOutsideLid, boolean lastOutsideWasLid) {
        if (lastOutsideWasLid && topOutsideLid != null) {
            placeAboveLid(topOutsideLid);
        } else {
            placeOutside(canvasWidth, highestCupTopY);
        }
        setInside(false);
    }
    
    /**
     * Posiciona la tapa como elemento exterior.
     */
    public void placeAsOutside(int canvasWidth, int highestCupTopY, Lid topOutsideLid) {
        if (topOutsideLid == null) {
            placeOutside(canvasWidth, highestCupTopY);
        } else {
            placeAboveLid(topOutsideLid);
        }
        setInside(false);
    }
    
    /**
     * Posiciona la tapa dentro de la torre.
     */
    public void placeAsInside(Cup container,Cup support,Lid lidOnContainer,Lid lidOnSupport,int grosor) {
        if (container == null) return;
    
        if (container.getNumber() == this.getNumber()) {
            placeOnCup(container, lidOnContainer);
        } else if (support == null) {
            placeInside(container, lidOnContainer, grosor);
        } else {
            placeAboveCup(support, container, lidOnSupport, grosor);
        }
    
        setInside(true);
    }
}

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
    private int xPosition;
    private int yPosition;
    private String color;
    private Rectangle body;
    private boolean isVisible;
    /**
     * Constructor for objects of class Lid
     */
    public Lid(int number, int xPosition, int yPosition)
    {
        this.number = number;
        body = new Rectangle();
        body.changeColor("red");
        
        body.changeSize(10, 40); 
        
        body.moveHorizontal(xPosition);
        body.moveVertical(yPosition);
        
        isVisible = false;
    }

    public void draw()
    {
        body.makeVisible();
        isVisible = true;
    }
    
    public void erase()
    {
        body.makeInvisible();
        isVisible = false;
    }
    
    public int getNumber()
    {
        return number;
    }
        
    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public int sampleMethod(int y)
    {
        // put your code here
        return 0;
    }
}
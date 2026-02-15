import java.util.ArrayList;

/**
 * Write a description of class Tower here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Tower
{
    // instance variables - replace the example below with your own
    private ArrayList<Cup> cups;
    private ArrayList<Lid> lids;
    private Rectangle floor;    
    
    private int maxHeight;
    private boolean lastOperationOk;
    
    private int baseX;
    private int baseY;
    private int elementHeight;
    /**
     * Constructor for objects of class Tower
     */
    public Tower(int maxHeight)
    {
        this.maxHeight = maxHeight;
        
        cups = new ArrayList<>();
        lids = new ArrayList<>();
        
        lastOperationOk = true;
        
        baseX = 200;
        baseY = 300;
        elementHeight = 20;
        
        // Crear piso
        floor = new Rectangle();
        floor.changeColor("black");
        floor.changeSize(10, 120);
        floor.moveHorizontal(baseX - 40);
        floor.moveVertical(baseY + 5);
        floor.makeVisible();
    }

    public void pushCup(int number)
    {

        for(Cup c : cups)
        {
            if(c.getNumber() == number)
            {
                lastOperationOk = false;
                return;
            }
        }
        
        if(cups.size() + lids.size() >= maxHeight)
        {
            lastOperationOk = false;
            return;
        }
        
        int currentHeight = cups.size() + lids.size();
        int yPosition = baseY - (currentHeight * elementHeight);
        
        Cup newCup = new Cup(number, baseX, yPosition);
        cups.add(newCup);
        newCup.draw();
        lastOperationOk = true;
    }
    
    public void popCup()
    {
        if(cups.isEmpty())
        {
            lastOperationOk = false;
            return;
        }
    
        Cup topCup = cups.remove(cups.size() - 1);
        topCup.erase();
        
        lastOperationOk = true;
    }

    public void pushLid(int number)
    {

        for(Lid l : lids)
        {
            if(l.getNumber() == number)
            {
                lastOperationOk = false;
                return;
            }
        }
        
        if(cups.size() + lids.size() >= maxHeight)
        {
            lastOperationOk = false;
            return;
        }
        
        int currentHeight = cups.size() + lids.size();
        int yPosition = baseY - (currentHeight * elementHeight);
        
        Lid newLid = new Lid(number, baseX, yPosition);
        lids.add(newLid);
        newLid.draw();
        lastOperationOk = true;
    }
    
    public void popLid()
    {
        if(lids.isEmpty())
        {
            lastOperationOk = false;
            return;
        }
        
        Lid topLid = lids.remove(lids.size() - 1);
        topLid.erase();
        
        lastOperationOk = true;
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
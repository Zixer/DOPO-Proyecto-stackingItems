package Test;
import Shapes.*;
import tower.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The test class towerTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class towerTest
{
    /**
     * Default constructor for test class towerTest
     */
    public towerTest()
    {
    }


    @Test
    public void shouldCreateTowerWithOddSizedCups() {
        Tower t = new Tower(4); 

        assertEquals(4, t.getCupsSize());
        assertEquals(0, t.getLidsSize());
        assertTrue(t.isVisible());
        assertTrue(t.isOk());
        
        t.makeInvisible();
    }

    @Test
    public void shouldCoverCupWhenMatchingLidComesRightAfter() {
        Tower t = new Tower(20, 20);

        t.pushCup(5);
        t.pushLid(5);
        t.cover();

        Cup c = t.popCup();

        assertNotNull(c);
        assertNotNull(c.getLid());
        assertEquals(5, c.getLid().getNumber());
        assertTrue(t.isOk());
        t.makeInvisible();
    }

    @Test
    public void shouldSwapTwoObjectsInInsertionOrder() {
        Tower t = new Tower(30, 30);

        t.pushCup(5);
        t.pushCup(3);

        Cup cup5Before = t.getCupByNumber(5);
        Cup cup3Before = t.getCupByNumber(3);

        int y5Before = cup5Before.getYpo();
        int y3Before = cup3Before.getYpo();

        t.swap(new String[]{"cup", "5"}, new String[]{"cup", "3"});

        Cup cup5After = t.getCupByNumber(5);
        Cup cup3After = t.getCupByNumber(3);

        int y5After = cup5After.getYpo();
        int y3After = cup3After.getYpo();

        assertTrue(t.isOk());
        assertTrue(y3Before == y5After || y5Before == y3After);
        t.makeInvisible();
    }
    
    public void shouldSwapCupAndLid() {
        Tower t = new Tower(30, 30);
    
        t.pushCup(5);
        t.pushLid(6);
    
        int lidYBefore = t.getLidByNumber(6).getYpo();
        int cupYBefore = t.getCupByNumber(5).getYpo();
    
        t.swap(new String[]{"cup", "5"}, new String[]{"lid", "6"});
    
        int lidYAfter = t.getLidByNumber(6).getYpo();
        int cupYAfter = t.getCupByNumber(5).getYpo();
    
        assertTrue(t.isOk());
        assertTrue(lidYBefore != lidYAfter || cupYBefore != cupYAfter);
        t.makeInvisible();
    }
    
    @Test
    public void shouldFailSwapWhenFirstObjectDoesNotExist() {
        Tower t = new Tower(30, 30);

        t.pushCup(5);
        t.pushCup(3);

        int y5Before = t.getCupByNumber(5).getYpo();
        int y3Before = t.getCupByNumber(3).getYpo();

        t.swap(new String[]{"cup", "9"}, new String[]{"cup", "3"});

        assertFalse(t.isOk());
        assertEquals(y5Before, t.getCupByNumber(5).getYpo());
        assertEquals(y3Before, t.getCupByNumber(3).getYpo());
        t.makeInvisible();
    }
    
    @Test
    public void shouldFailSwapWhenSecondObjectDoesNotExist() {
        Tower t = new Tower(30, 30);
    
        t.pushCup(5);
        t.pushLid(6);
    
        int cupYBefore = t.getCupByNumber(5).getYpo();
        int lidYBefore = t.getLidByNumber(6).getYpo();
    
        t.swap(new String[]{"cup", "5"}, new String[]{"lid", "9"});
    
        assertFalse(t.isOk());
        assertEquals(cupYBefore, t.getCupByNumber(5).getYpo());
        assertEquals(lidYBefore, t.getLidByNumber(6).getYpo());
        t.makeInvisible();
    }
    
    @Test
    public void shouldCreateEmptyTowerWhenNumCupsIsZero() {
        Tower t = new Tower(0);
    
        assertEquals(0, t.getCupsSize());
        assertEquals(0, t.getLidsSize());
        assertTrue(t.isVisible());
        t.makeInvisible();
    }
    
    @Test
    public void shouldNotCoverWhenCupAndLidSizesDoNotMatch() {
        Tower t = new Tower(30, 30);
    
        t.pushCup(5);
        t.pushLid(3);
        t.cover();
    
        Cup c = t.getCupByNumber(5);
    
        assertNotNull(c);
        assertNull(c.getLid());
        t.makeInvisible();
    }
    
    @Test
    public void shouldNotAllowTowerWithZeroCups() {
        Tower t = new Tower(0);
    
        assertFalse(t.isVisible()); 
        t.makeInvisible();
    }
    
    @Test
    public void shouldPassSwapToReduceWhenReductionExists() {
    
        Tower t = new Tower(20,20);
    
        t.pushCup(3);
        t.pushCup(5);
        t.pushCup(1);
    
        String[][] result = t.swapToReduce();
    
        assertEquals(2, result.length);
    
        assertNotNull(result[0]);
        assertNotNull(result[1]);
        t.makeInvisible();
    }
    
    @Test
    public void shouldFailSwapToReduceWhenNoReductionExists() {
    
        Tower t = new Tower(20,20);
    
        t.pushCup(5);
        t.pushCup(3);
        t.pushCup(1);
    
        String[][] result = t.swapToReduce();
    
        assertEquals(0, result.length);
        t.makeInvisible();
    }
    
    @Test
    public void shouldFailWhenTowerTooSmall() {
    
        Tower t = new Tower(20,20);
    
        t.pushCup(5);
    
        String[][] result = t.swapToReduce();
    
        assertEquals(0, result.length);
        t.makeInvisible();
    }
    
    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @AfterEach
    public void tearDown()
    {
    }
}

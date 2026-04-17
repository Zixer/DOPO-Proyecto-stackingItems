package Test;

import static org.junit.Assert.*;
import org.junit.Test;
import tower.*;

public class TowerCC4test {

    @Test
    public void shouldBuildTowerWithMultipleTypes() throws towerException {
        Tower t = new Tower(10, 50);

        t.pushCup("normal", 7);
        t.pushCup("opener", 5);
        t.pushCup("hierarchical", 3);

        t.pushLid("normal", 7);
        t.pushLid("crazy", 5);

        assertTrue(t.getCupsSize() == 3);
        assertTrue(t.getLidsSize() >= 1);
    }

    @Test
    public void shouldMaintainOrderConsistency() throws towerException {
        Tower t = new Tower(10, 50);

        t.pushCup("normal", 7);
        t.pushCup("normal", 5);
        t.pushLid("crazy", 5);

        assertNotNull(t.getInsertionOrder());
    }
    
    @Test
    public void shouldSwapElements() throws towerException {
        Tower t = new Tower(10, 50);
        
        t.pushCup("normal", 7);
        t.pushCup("normal", 5);

        String[] o1 = {"cup", "7"};
        String[] o2 = {"cup", "5"};
        
        t.swap(o1, o2);

        assertTrue(t.isOk());
    }
}

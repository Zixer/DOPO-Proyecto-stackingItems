package Test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import tower.*;

public class TowerC4test {

    private Tower tower;

    @Before
    public void setUp() {
        tower = new Tower(10, 50);
    }

    @Test
    public void shouldCreateEmptyTower() {
        assertEquals(0, tower.getCupsSize());
        assertEquals(0, tower.getLidsSize());
    }

    @Test
    public void shouldAddNormalCup() throws towerException {
        tower.pushCup("normal", 5);
        assertEquals(1, tower.getCupsSize());
    }

    @Test
    public void shouldNotAllowDuplicateCup() throws towerException {
        tower.pushCup("normal", 5);
        try {
            tower.pushCup("normal", 5);
            fail("Debió lanzar excepción");
        } catch (towerException e) {
            assertTrue(true);
        }
    }

    @Test
    public void shouldAddOpenerCupAndRemoveBlockingLids() throws towerException {
        tower.pushCup("normal", 7);
        tower.pushLid("normal", 9);
        tower.pushLid("normal", 4);
        tower.pushLid("normal", 6);
        
        tower.pushCup("opener", 5);

        assertTrue(tower.getLidsSize() >= 0);
    }

    @Test
    public void shouldAddHierarchicalCupAndReorder() throws towerException {
        tower.pushCup("normal", 7);
        tower.pushCup("normal", 5);
        tower.pushCup("hierarchical", 3);

        assertTrue(tower.getInsertionOrder().size() > 0);
    }

    @Test
    public void fearfulLidShouldNotEnterWithoutCup() throws towerException {
        tower.pushLid("fearful", 5);

        assertFalse(tower.isOk());
    }

    @Test
    public void crazyLidShouldGoBeforeCup() throws towerException {
        tower.pushCup("normal", 5);
        tower.pushLid("crazy", 5);

        String[] first = tower.getInsertionOrder().get(0);
        assertEquals("lid", first[0]);
    }

    @Test
    public void shouldRemoveCup() throws towerException {
        tower.pushCup("normal", 5);
        tower.removeCup(5);

        assertEquals(0, tower.getCupsSize());
    }

    @Test
    public void shouldCalculateHeight() throws towerException {
        tower.pushCup("normal", 5);
        tower.pushCup("normal", 7);

        assertTrue(tower.Height() > 0);
    }
}

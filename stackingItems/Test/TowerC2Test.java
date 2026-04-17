package Test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import tower.*;

import java.util.ArrayList;

public class TowerC2Test {

    private Tower tower;

    @Before
    public void setUp() {
        tower = new Tower(100, 50);
        tower.makeInvisible(); // ciclo 2 pide pruebas de unidad en modo invisible
    }

    // =========================
    // Helpers
    // =========================

    private int indexOf(String type, String number) {
        ArrayList<String[]> order = tower.getInsertionOrder();
        for (int i = 0; i < order.size(); i++) {
            String[] item = order.get(i);
            if (item[0].equals(type) && item[1].equals(number)) {
                return i;
            }
        }
        return -1;
    }

    private boolean contains(String type, String number) {
        return indexOf(type, number) != -1;
    }

    private String[][] snapshotOrder() {
        ArrayList<String[]> order = tower.getInsertionOrder();
        String[][] copy = new String[order.size()][2];
        for (int i = 0; i < order.size(); i++) {
            copy[i][0] = order.get(i)[0];
            copy[i][1] = order.get(i)[1];
        }
        return copy;
    }

    private void assertOrderEquals(String[][] expected) {
        ArrayList<String[]> order = tower.getInsertionOrder();
        assertEquals("Tamaño incorrecto del insertionOrder", expected.length, order.size());

        for (int i = 0; i < expected.length; i++) {
            assertEquals("Tipo incorrecto en posición " + i, expected[i][0], order.get(i)[0]);
            assertEquals("Número incorrecto en posición " + i, expected[i][1], order.get(i)[1]);
        }
    }

    private void assertOnlyTheseTwoPositionsChanged(String[][] before, int pos1, int pos2) {
        ArrayList<String[]> after = tower.getInsertionOrder();

        for (int i = 0; i < before.length; i++) {
            if (i != pos1 && i != pos2) {
                assertEquals("Cambió el tipo en una posición no esperada: " + i, before[i][0], after.get(i)[0]);
                assertEquals("Cambió el número en una posición no esperada: " + i, before[i][1], after.get(i)[1]);
            }
        }
    }

    // =========================
    // Requisito 10:
    // crear torre indicando número de tazas
    // =========================

    @Test
    public void shouldCreateTowerWithGivenNumberOfCups() {
        Tower t = new Tower(4);
        t.makeInvisible();

        assertEquals(4, t.getCupsSize());
        assertEquals(0, t.getLidsSize());

        assertNotNull(t.getCupByNumber(1));
        assertNotNull(t.getCupByNumber(3));
        assertNotNull(t.getCupByNumber(5));
        assertNotNull(t.getCupByNumber(7));

        assertEquals(4, t.getInsertionOrder().size());
        assertEquals("cup", t.getInsertionOrder().get(0)[0]);
        assertEquals("1", t.getInsertionOrder().get(0)[1]);
        assertEquals("7", t.getInsertionOrder().get(3)[1]);
    }

    @Test
    public void shouldCreateEmptyTowerWhenCupsIsZero() {
        Tower t = new Tower(0);
        t.makeInvisible();

        assertEquals(0, t.getCupsSize());
        assertEquals(0, t.getLidsSize());
        assertEquals(0, t.getInsertionOrder().size());
    }

    // =========================
    // Requisito 11:
    // swap
    // =========================

    @Test
    public void shouldSwapTwoCupsAndOnlyThoseTwoShouldChangePlaces() {
        tower.pushCup(9);
        tower.pushCup(5);
        tower.pushCup(3);

        String[][] before = snapshotOrder();

        int posCup9 = indexOf("cup", "9");
        int posCup3 = indexOf("cup", "3");

        tower.swap(new String[]{"cup", "9"}, new String[]{"cup", "3"});

        assertEquals(posCup9, indexOf("cup", "3"));
        assertEquals(posCup3, indexOf("cup", "9"));
        assertOnlyTheseTwoPositionsChanged(before, posCup9, posCup3);
        assertTrue(tower.isOk());
    }

    @Test
    public void shouldSwapCupAndLidAndOnlyThoseTwoShouldChangePlaces() {
        tower.pushCup(9);
        tower.pushCup(5);
        tower.pushLid(5);

        String[][] before = snapshotOrder();

        int posCup9 = indexOf("cup", "9");
        int posLid5 = indexOf("lid", "5");

        tower.swap(new String[]{"cup", "9"}, new String[]{"lid", "5"});

        assertEquals(posCup9, indexOf("lid", "5"));
        assertEquals(posLid5, indexOf("cup", "9"));
        assertOnlyTheseTwoPositionsChanged(before, posCup9, posLid5);
        assertTrue(tower.isOk());
    }

    @Test
    public void shouldNotSwapWhenOneObjectDoesNotExist() {
        tower.pushCup(9);
        tower.pushCup(5);

        String[][] before = snapshotOrder();

        tower.swap(new String[]{"cup", "9"}, new String[]{"lid", "7"});

        assertOrderEquals(before);
        assertFalse(tower.isOk());
    }

    // =========================
    // Requisito 12:
    // cover
    // =========================

    @Test
    public void shouldCoverOnlyCupsThatHaveTheirLidsInTower() {
        tower.pushCup(9);
        tower.pushCup(5);
        tower.pushCup(3);

        tower.pushLid(9);
        tower.pushLid(5);

        tower.cover();

        // cups primero, lids después ordenadas ascendente
        assertOrderEquals(new String[][]{
            {"cup", "9"},
            {"cup", "5"},
            {"cup", "3"},
            {"lid", "5"},
            {"lid", "9"}
        });

        assertTrue(contains("cup", "3"));
        assertFalse(contains("lid", "3"));
        assertTrue(tower.isOk());
    }

    @Test
    public void shouldNotLoseAnyElementAfterCover() {
        tower.pushCup(9);
        tower.pushCup(5);
        tower.pushLid(5);
        tower.pushLid(9);

        int cupsBefore = tower.getCupsSize();
        int lidsBefore = tower.getLidsSize();

        tower.cover();

        assertEquals(cupsBefore, tower.getCupsSize());
        assertEquals(lidsBefore, tower.getLidsSize());
        assertTrue(contains("cup", "9"));
        assertTrue(contains("cup", "5"));
        assertTrue(contains("lid", "5"));
        assertTrue(contains("lid", "9"));
    }

    @Test
    public void shouldLeaveOrderConsistentAfterCoverWhenLidsAreUnordered() {
        tower.pushCup(9);
        tower.pushCup(5);
        tower.pushCup(3);
        tower.pushLid(9);
        tower.pushLid(3);
        tower.pushLid(5);

        tower.cover();

        assertOrderEquals(new String[][]{
            {"cup", "9"},
            {"cup", "5"},
            {"cup", "3"},
            {"lid", "3"},
            {"lid", "5"},
            {"lid", "9"}
        });
    }

    // =========================
    // Requisito 13:
    // swapToReduce
    // =========================

    @Test
    public void shouldReturnEmptySwapToReduceWhenThereAreLessThanTwoItems() {
        tower.pushCup(9);

        String[][] result = tower.swapToReduce();

        assertEquals(0, result.length);
        assertFalse(tower.isOk());
    }

    @Test
    public void shouldSuggestExistingObjectsInSwapToReduce() {
        tower.pushCup(9);
        tower.pushCup(5);
        tower.pushCup(3);
        tower.pushLid(5);

        String[][] result = tower.swapToReduce();

        assertEquals(2, result.length);
        assertEquals(2, result[0].length);
        assertEquals(2, result[1].length);

        assertTrue(contains(result[0][0], result[0][1]));
        assertTrue(contains(result[1][0], result[1][1]));
        assertTrue(tower.isOk());
    }

    @Test
    public void shouldReduceOrKeepHeightWhenApplyingSuggestedSwap() {
        tower.pushCup(9);
        tower.pushCup(3);
        tower.pushCup(7);
        tower.pushLid(3);
        tower.pushLid(9);

        tower.redraw();
        int originalHeight = tower.Height();

        String[][] suggestion = tower.swapToReduce();

        if (suggestion.length == 2) {
            tower.swap(suggestion[0], suggestion[1]);
            tower.redraw();
            int newHeight = tower.Height();

            assertTrue("La altura aumentó después del swap sugerido", newHeight <= originalHeight);
        } else {
            fail("swapToReduce no devolvió una sugerencia válida");
        }
    }

    @Test
    public void shouldNotModifyTowerWhenOnlyConsultingSwapToReduce() {
        tower.pushCup(9);
        tower.pushCup(5);
        tower.pushCup(3);
        tower.pushLid(5);

        String[][] before = snapshotOrder();
        int heightBefore = tower.Height();

        tower.swapToReduce();

        assertOrderEquals(before);
        assertEquals(heightBefore, tower.Height());
    }

    // =========================
    // Propuestas estilo TowerCC2Test
    // =========================

    @Test
    public void accordingXXShouldSwapFirstAndLastObjectsCorrectly() {
        tower.pushCup(9);
        tower.pushCup(7);
        tower.pushCup(5);
        tower.pushLid(5);

        tower.swap(new String[]{"cup", "9"}, new String[]{"lid", "5"});

        assertEquals(0, indexOf("lid", "5"));
        assertEquals(3, indexOf("cup", "9"));
    }

    @Test
    public void accordingXXShouldSuggestASwapThatUsesExistingItems() {
        tower.pushCup(9);
        tower.pushCup(5);
        tower.pushLid(5);

        String[][] suggestion = tower.swapToReduce();

        assertEquals(2, suggestion.length);
        assertTrue(contains(suggestion[0][0], suggestion[0][1]));
        assertTrue(contains(suggestion[1][0], suggestion[1][1]));
    }
}

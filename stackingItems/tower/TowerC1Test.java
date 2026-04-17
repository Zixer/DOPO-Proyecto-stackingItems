package tower;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import tower.*;

import java.util.ArrayList;

public class TowerC1Test {

    private Tower tower;

    @Before
    public void setUp() {
        tower = new Tower(100, 50);
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

    private void assertItemAt(int index, String type, String number) {
        ArrayList<String[]> order = tower.getInsertionOrder();
        assertTrue("Índice fuera de rango", index >= 0 && index < order.size());
        assertEquals(type, order.get(index)[0]);
        assertEquals(number, order.get(index)[1]);
    }

    private void assertOrderEquals(String[][] expected) {
        ArrayList<String[]> order = tower.getInsertionOrder();
        assertEquals("Tamaño del insertionOrder incorrecto", expected.length, order.size());

        for (int i = 0; i < expected.length; i++) {
            assertEquals("Tipo incorrecto en posición " + i, expected[i][0], order.get(i)[0]);
            assertEquals("Número incorrecto en posición " + i, expected[i][1], order.get(i)[1]);
        }
    }

    // =========================
    // Crear torre
    // =========================

    @Test
    public void shouldCreateAnEmptyTower() {
        assertEquals(0, tower.getCupsSize());
        assertEquals(0, tower.getLidsSize());
        assertEquals(0, tower.getInsertionOrder().size());
        assertTrue(tower.isOk());
    }

    // =========================
    // Manejo de cups
    // =========================

    @Test
    public void shouldAddOneCupAndRegisterItInInsertionOrder() {
        tower.pushCup(5);

        assertEquals(1, tower.getCupsSize());
        assertNotNull(tower.getCupByNumber(5));
        assertTrue(contains("cup", "5"));
        assertEquals(1, tower.getInsertionOrder().size());
        assertItemAt(0, "cup", "5");
        assertTrue(tower.isOk());
    }

    @Test
    public void shouldNotAddDuplicatedCup() {
        tower.pushCup(5);
        String[][] before = snapshotOrder();

        tower.pushCup(5);

        assertEquals(1, tower.getCupsSize());
        assertNotNull(tower.getCupByNumber(5));
        assertOrderEquals(before);
        assertFalse(tower.isOk());
    }

    @Test
    public void shouldPopLastCupAndRemoveItFromInsertionOrder() {
        tower.pushCup(5);
        tower.pushCup(7);

        Cup removed = tower.popCup();

        assertNotNull(removed);
        assertEquals(7, removed.getNumber());
        assertEquals(1, tower.getCupsSize());
        assertNull(tower.getCupByNumber(7));
        assertFalse(contains("cup", "7"));
        assertTrue(contains("cup", "5"));
        assertEquals(1, tower.getInsertionOrder().size());
        assertItemAt(0, "cup", "5");
        assertTrue(tower.isOk());
    }

    @Test
    public void shouldRemoveSpecificCupEverywhere() {
        tower.pushCup(5);
        tower.pushCup(7);
        tower.pushCup(9);

        tower.removeCup(7);

        assertEquals(2, tower.getCupsSize());
        assertNull(tower.getCupByNumber(7));
        assertFalse(contains("cup", "7"));
        assertTrue(contains("cup", "5"));
        assertTrue(contains("cup", "9"));
        assertOrderEquals(new String[][]{
            {"cup", "5"},
            {"cup", "9"}
        });
        assertTrue(tower.isOk());
    }

    // =========================
    // Manejo de lids
    // =========================

    @Test
    public void shouldAddOneLidAndRegisterItInInsertionOrder() {
        tower.pushLid(5);

        assertEquals(1, tower.getLidsSize());
        assertNotNull(tower.getLidByNumber(5));
        assertTrue(contains("lid", "5"));
        assertEquals(1, tower.getInsertionOrder().size());
        assertItemAt(0, "lid", "5");
        assertTrue(tower.isOk());
    }

    @Test
    public void shouldNotAddDuplicatedLid() {
        tower.pushLid(5);
        String[][] before = snapshotOrder();

        tower.pushLid(5);

        assertEquals(1, tower.getLidsSize());
        assertNotNull(tower.getLidByNumber(5));
        assertOrderEquals(before);
        assertFalse(tower.isOk());
    }

    @Test
    public void shouldPopLastLidAndRemoveItFromInsertionOrder() {
        tower.pushLid(5);
        tower.pushLid(7);

        Lid removed = tower.popLid();

        assertNotNull(removed);
        assertEquals(7, removed.getNumber());
        assertEquals(1, tower.getLidsSize());
        assertNull(tower.getLidByNumber(7));
        assertFalse(contains("lid", "7"));
        assertTrue(contains("lid", "5"));
        assertEquals(1, tower.getInsertionOrder().size());
        assertItemAt(0, "lid", "5");
        assertTrue(tower.isOk());
    }

    @Test
    public void shouldRemoveSpecificLidEverywhere() {
        tower.pushLid(5);
        tower.pushLid(7);
        tower.pushLid(9);

        tower.removeLid(7);

        assertEquals(2, tower.getLidsSize());
        assertNull(tower.getLidByNumber(7));
        assertFalse(contains("lid", "7"));
        assertTrue(contains("lid", "5"));
        assertTrue(contains("lid", "9"));
        assertOrderEquals(new String[][]{
            {"lid", "5"},
            {"lid", "9"}
        });
        assertTrue(tower.isOk());
    }

    // =========================
    // Relación cup-lid del mismo número
    // =========================

    @Test
    public void shouldLinkCupAndLidOfSameNumber() {
        tower.pushCup(5);
        tower.pushLid(5);

        Cup cup = tower.getCupByNumber(5);
        Lid lid = tower.getLidByNumber(5);

        assertNotNull(cup);
        assertNotNull(lid);
        assertEquals(lid, cup.getLid());
        assertEquals(cup, lid.getPartnerCup());
        assertTrue(contains("cup", "5"));
        assertTrue(contains("lid", "5"));
    }

    @Test
    public void shouldRemovePartnerReferenceWhenLidIsRemoved() {
        tower.pushCup(5);
        tower.pushLid(5);

        Cup cup = tower.getCupByNumber(5);
        assertNotNull(cup.getLid());

        tower.removeLid(5);

        assertNull(tower.getLidByNumber(5));
        assertFalse(contains("lid", "5"));
        assertNull(cup.getLid());
        assertTrue(contains("cup", "5"));
        assertEquals(1, tower.getCupsSize());
        assertEquals(0, tower.getLidsSize());
    }

    // =========================
    // orderTower
    // =========================

    @Test
    public void shouldOrderCupsFromGreaterToSmaller() {
        tower.pushCup(5);
        tower.pushCup(9);
        tower.pushCup(3);
        tower.pushCup(7);

        tower.orderTower();

        assertOrderEquals(new String[][]{
            {"cup", "9"},
            {"cup", "7"},
            {"cup", "5"},
            {"cup", "3"}
        });
        assertTrue(tower.isOk());
    }

    @Test
    public void shouldOrderCupsAndKeepLidsAfterCups() {
        tower.pushCup(5);
        tower.pushCup(9);
        tower.pushCup(3);
        tower.pushLid(5);
        tower.pushLid(9);

        tower.orderTower();

        assertOrderEquals(new String[][]{
            {"cup", "9"},
            {"cup", "5"},
            {"cup", "3"},
            {"lid", "5"},
            {"lid", "9"}
        });
        assertTrue(tower.isOk());
    }

    // =========================
    // reverseTower
    // =========================

    @Test
    public void shouldReverseInsertionOrderExactly() {
        tower.pushCup(9);
        tower.pushCup(5);
        tower.pushCup(3);

        String[][] before = snapshotOrder();

        tower.reverseTower();

        assertEquals(before[0][0], "cup");
        assertEquals(before[0][1], "9");
        assertEquals(before[2][1], "3");

        assertOrderEquals(new String[][]{
            {"cup", "3"},
            {"cup", "5"},
            {"cup", "9"}
        });
        assertTrue(tower.isOk());
    }

    @Test
    public void shouldSwapTwoElementsAndOnlyThoseElementsChangePlaces() {
        tower.pushCup(9);
        tower.pushCup(5);
        tower.pushCup(3);

        String[][] before = snapshotOrder();

        tower.swap(new String[]{"cup", "9"}, new String[]{"cup", "3"});

        assertOrderEquals(new String[][]{
            {"cup", "3"},
            {"cup", "5"},
            {"cup", "9"}
        });

        // El del medio debe quedarse igual
        assertEquals(before[1][0], tower.getInsertionOrder().get(1)[0]);
        assertEquals(before[1][1], tower.getInsertionOrder().get(1)[1]);
        assertTrue(tower.isOk());
    }

    // =========================
    // cover
    // =========================

    @Test
    public void shouldMoveLidsAfterCupsWhenCoverIsCalled() {
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
        assertTrue(tower.isOk());
    }

    // =========================
    // height
    // =========================

    @Test
    public void shouldReturnZeroHeightWhenTowerIsEmpty() {
        assertEquals(0, tower.Height());
    }

    @Test
    public void shouldIncreaseHeightWhenElementsAreAdded() {
        int initialHeight = tower.Height();

        tower.pushCup(9);
        int heightAfterCup = tower.Height();

        tower.pushLid(9);
        int heightAfterLid = tower.Height();

        assertEquals(0, initialHeight);
        assertTrue(heightAfterCup > initialHeight);
        assertTrue(heightAfterLid >= heightAfterCup);
    }

    // =========================
    // visibility
    // =========================

    @Test
    public void shouldBecomeVisible() {
        tower.makeVisible();
        assertTrue(tower.isVisible());
    }

    @Test
    public void shouldBecomeInvisible() {
        tower.makeVisible();
        tower.makeInvisible();
        assertFalse(tower.isVisible());
    }

    @Test
    public void shouldKeepDataWhenBecomingInvisible() {
        tower.pushCup(5);
        tower.pushLid(5);

        tower.makeInvisible();

        assertFalse(tower.isVisible());
        assertNotNull(tower.getCupByNumber(5));
        assertNotNull(tower.getLidByNumber(5));
        assertTrue(contains("cup", "5"));
        assertTrue(contains("lid", "5"));
    }

    // =========================
    // exit
    // =========================

    @Test
    public void shouldExitAndClearEverything() {
        tower.pushCup(5);
        tower.pushCup(7);
        tower.pushLid(5);
        tower.makeVisible();

        tower.exit();

        assertEquals(0, tower.getCupsSize());
        assertEquals(0, tower.getLidsSize());
        assertEquals(0, tower.getInsertionOrder().size());
        assertFalse(tower.isVisible());
        assertNull(tower.getCupByNumber(5));
        assertNull(tower.getLidByNumber(5));
    }
}

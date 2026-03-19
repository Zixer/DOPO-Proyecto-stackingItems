import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class TowerContestTest {

    private TowerContest TowerContest;

    @Before
    public void setUp() {
        TowerContest = new TowerContest();
    }

    @Test
    public void shouldPassSolveBaseCase() {
        assertEquals("1", TowerContest.solve(1, 1));
    }

    @Test
    public void shouldPassSolveMinimumHeightCase() {
        assertEquals("3 1", TowerContest.solve(2, 3));
    }

    @Test
    public void shouldPassSolveMaximumHeightCase() {
        assertEquals("1 3", TowerContest.solve(2, 4));
    }

    @Test
    public void shouldPassSimulateValidCase() {
        assertNotNull(TowerContest.simulate(2, 3));
    }

    @Test
    public void shouldFailIfHeightBelowMinimum() {
        assertEquals("impossible", TowerContest.solve(2, 2));
    }

    @Test
    public void shouldFailIfHeightAboveMaximum() {
        assertEquals("impossible", TowerContest.solve(2, 5));
    }

    @Test
    public void shouldFailIfNoSolutionExists() {
        assertEquals("impossible", TowerContest.solve(3, 7));
    }

    @Test
    public void shouldFailSimulateInvalidCase() {
        assertNull(TowerContest.simulate(2, 2));
    }
}
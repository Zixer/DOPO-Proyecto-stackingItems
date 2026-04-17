package Test;
import tower.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Write a description of class TowerContestTest here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class TowerContestTest {

    private TowerContest TowerContest;

    @BeforeEach
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

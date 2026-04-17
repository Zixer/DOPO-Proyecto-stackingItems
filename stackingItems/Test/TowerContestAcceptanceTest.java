package Test;
import tower.*;
import javax.swing.JOptionPane;

public class TowerContestAcceptanceTest {

    private long defaultDelay;

    public TowerContestAcceptanceTest(long defaultDelay) {
        this.defaultDelay = defaultDelay + 500;
    }

    private void waitMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean askUser(String message, String title) {
        int answer = JOptionPane.showConfirmDialog(
            null,
            message,
            title,
            JOptionPane.YES_NO_OPTION
        );
        return answer == JOptionPane.YES_OPTION;
    }

    private void printCaseHeader(String title, int n, int h) {
        System.out.println();
        System.out.println("======================================");
        System.out.println(title);
        System.out.println("simulate(" + n + ", " + h + ")");
        System.out.println("======================================");
    }

    /**
     * Caso 1:
     * Caso pequeño y simple.
     */
    public void acceptanceSimulateSmallCase() {
        TowerContest contest = new TowerContest();

        int n = 1;
        int h = 10;

        printCaseHeader("PRUEBA DE ACEPTACIÓN 1 - CASO PEQUEÑO", n, h);
        waitMillis(defaultDelay);

        contest.simulate(n, h);
        waitMillis(defaultDelay + 1500);

        boolean accepted = askUser(
            "¿La simulación del caso pequeño se ejecutó correctamente y se vio coherente?",
            "Aceptación - simulate caso pequeño"
        );
        System.out.println(accepted ? "Prueba aceptada" : "Prueba rechazada");
    }

    /**
     * Caso 2:
     * Caso medio para una demo normal.
     */
    public void acceptanceSimulateMediumCase() {
        TowerContest contest = new TowerContest();

        int n = 4;
        int h = 20;

        printCaseHeader("PRUEBA DE ACEPTACIÓN 2 - CASO MEDIO", n, h);
        waitMillis(defaultDelay);

        contest.simulate(n, h);
        waitMillis(defaultDelay + 2000);

        boolean accepted = askUser(
            "¿La simulación del caso medio se ejecutó correctamente y mostró una solución consistente?",
            "Aceptación - simulate caso medio"
        );

        System.out.println(accepted ? "Prueba aceptada" : "Prueba rechazada");
    }

    /**
     * Caso 3:
     * Caso borde n = 0.
     */
    public void acceptanceSimulateZeroCase() {
        TowerContest contest = new TowerContest();

        int n = 0;
        int h = 10;

        printCaseHeader("PRUEBA DE ACEPTACIÓN 3 - CASO BORDE", n, h);
        waitMillis(defaultDelay);

        contest.simulate(n, h);
        waitMillis(defaultDelay + 1000);

        boolean accepted = askUser(
            "¿La simulación del caso borde con n=0 fue manejada correctamente sin errores?",
            "Aceptación - simulate caso borde"
        );

        System.out.println(accepted ? "Prueba aceptada" : "Prueba rechazada");
    }

    /**
     * Caso 4:
     * Caso muy restringido o imposible.
     */
    public void acceptanceSimulateRestrictedCase() {
        TowerContest contest = new TowerContest();

        int n = 8;
        int h = 1;

        printCaseHeader("PRUEBA DE ACEPTACIÓN 4 - CASO RESTRINGIDO", n, h);
        waitMillis(defaultDelay);

        contest.simulate(n, h);
        waitMillis(defaultDelay + 1500);

        boolean accepted = askUser(
            "¿La simulación del caso restringido fue manejada correctamente, incluso si no había solución?",
            "Aceptación - simulate caso restringido"
        );

        System.out.println(accepted ? "Prueba aceptada" : "Prueba rechazada");
    }

    /**
     * Caso 5:
     * Comparación visual con otra altura para el mismo número de tazas.
     */
    public void acceptanceSimulateCompareHeights() {
        TowerContest contest = new TowerContest();

        int n = 5;
        int h1 = 10;
        int h2 = 30;

        printCaseHeader("PRUEBA DE ACEPTACIÓN 5A - ALTURA MENOR", n, h1);
        waitMillis(defaultDelay);
        contest.simulate(n, h1);
        waitMillis(defaultDelay + 1500);

        printCaseHeader("PRUEBA DE ACEPTACIÓN 5B - ALTURA MAYOR", n, h2);
        waitMillis(defaultDelay);
        contest.simulate(n, h2);
        waitMillis(defaultDelay + 2000);

        boolean accepted = askUser(
            "¿Se observó un comportamiento coherente al simular el mismo n con dos alturas distintas?",
            "Aceptación - simulate comparación de alturas"
        );

        System.out.println(accepted ? "Prueba aceptada" : "Prueba rechazada");
    }

    /**
     * Caso 6:
     * Caso más grande para mostrar estabilidad.
     */
    public void acceptanceSimulateLargeCase() {
        TowerContest contest = new TowerContest();

        int n = 7;
        int h = 40;

        printCaseHeader("PRUEBA DE ACEPTACIÓN 6 - CASO GRANDE", n, h);
        waitMillis(defaultDelay);

        contest.simulate(n, h);
        waitMillis(defaultDelay + 2500);

        boolean accepted = askUser(
            "¿La simulación del caso grande se ejecutó correctamente sin bloquearse ni verse inconsistente?",
            "Aceptación - simulate caso grande"
        );

        System.out.println(accepted ? "Prueba aceptada" : "Prueba rechazada");
    }
}

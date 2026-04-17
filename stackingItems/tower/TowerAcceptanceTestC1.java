package tower;

import javax.swing.JOptionPane;
import java.util.ArrayList;

public class TowerAcceptanceTestC1 {

    private long defaultDelay;

    public TowerAcceptanceTestC1(long defaultDelay) {
        this.defaultDelay = defaultDelay + 500;
    }

    private void waitMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Tower createTower(int maxHeight, int maxWidth) {
        Tower tower = new Tower(maxHeight, maxWidth);
        tower.makeVisible();
        waitMillis(defaultDelay);
        return tower;
    }

    public void pushCupStep(Tower tower, int size) {
        System.out.println("pushCup(" + size + ")");
        tower.pushCup(size);
        tower.makeVisible();
        printOrder(tower);
        waitMillis(defaultDelay);
    }

    public void pushLidStep(Tower tower, int size) {
        System.out.println("pushLid(" + size + ")");
        tower.pushLid(size);
        tower.makeVisible();
        printOrder(tower);
        waitMillis(defaultDelay);
    }

    public void removeCupStep(Tower tower, int size) {
        System.out.println("removeCup(" + size + ")");
        tower.removeCup(size);
        tower.makeVisible();
        printOrder(tower);
        waitMillis(defaultDelay);
    }

    public void removeLidStep(Tower tower, int size) {
        System.out.println("removeLid(" + size + ")");
        tower.removeLid(size);
        tower.makeVisible();
        printOrder(tower);
        waitMillis(defaultDelay);
    }

    public void orderTowerStep(Tower tower) {
        System.out.println("orderTower()");
        tower.orderTower();
        tower.makeVisible();
        printOrder(tower);
        waitMillis(defaultDelay);
    }

    public void reverseTowerStep(Tower tower) {
        System.out.println("reverseTower()");
        tower.reverseTower();
        tower.makeVisible();
        printOrder(tower);
        waitMillis(defaultDelay);
    }

    public void coverStep(Tower tower) {
        System.out.println("cover()");
        tower.cover();
        tower.makeVisible();
        printOrder(tower);
        waitMillis(defaultDelay);
    }

    public void visibleStep(Tower tower) {
        System.out.println("makeVisible()");
        tower.makeVisible();
        waitMillis(defaultDelay);
    }

    public void invisibleStep(Tower tower) {
        System.out.println("makeInvisible()");
        tower.makeInvisible();
        waitMillis(defaultDelay);
    }

    private void printOrder(Tower tower) {
        ArrayList<String[]> order = tower.getInsertionOrder();
        System.out.println("InsertionOrder actual:");
        for (String[] item : order) {
            System.out.println("{" + item[0] + ", " + item[1] + "}");
        }
        System.out.println("---------------------");
    }

    private boolean contains(Tower tower, String type, String number) {
        ArrayList<String[]> order = tower.getInsertionOrder();
        for (String[] item : order) {
            if (item[0].equals(type) && item[1].equals(number)) {
                return true;
            }
        }
        return false;
    }

    private int indexOf(Tower tower, String type, String number) {
        ArrayList<String[]> order = tower.getInsertionOrder();
        for (int i = 0; i < order.size(); i++) {
            if (order.get(i)[0].equals(type) && order.get(i)[1].equals(number)) {
                return i;
            }
        }
        return -1;
    }

    public boolean askUser(String message, String title) {
        int answer = JOptionPane.showConfirmDialog(
            null,
            message,
            title,
            JOptionPane.YES_NO_OPTION
        );
        return answer == JOptionPane.YES_OPTION;
    }

    /**
     * Aceptación 1:
     * gestión + reorganización + validación real del cambio de orden.
     */
    public Tower demoManagementAndReorganization() {
        Tower tower = createTower(100, 50);

        pushCupStep(tower, 9);
        pushCupStep(tower, 5);
        pushCupStep(tower, 3);
        pushLidStep(tower, 9);
        pushLidStep(tower, 5);

        int indexCup9Before = indexOf(tower, "cup", "9");
        int indexCup3Before = indexOf(tower, "cup", "3");

        reverseTowerStep(tower);

        int indexCup9AfterReverse = indexOf(tower, "cup", "9");
        int indexCup3AfterReverse = indexOf(tower, "cup", "3");

        boolean reverseChangedOrder =
            indexCup9Before != indexCup9AfterReverse &&
            indexCup3Before != indexCup3AfterReverse &&
            indexCup3AfterReverse < indexCup9AfterReverse;

        orderTowerStep(tower);

        boolean orderedCorrectly =
            indexOf(tower, "cup", "9") < indexOf(tower, "cup", "5") &&
            indexOf(tower, "cup", "5") < indexOf(tower, "cup", "3");

        coverStep(tower);

        boolean coverPlacedLidsAfterCups =
            indexOf(tower, "lid", "5") > indexOf(tower, "cup", "3") &&
            indexOf(tower, "lid", "9") > indexOf(tower, "cup", "3");

        waitMillis(defaultDelay + 1000);

        boolean accepted = askUser(
            "¿Visualmente la torre agregó elementos, invirtió el orden, ordenó de mayor a menor y reorganizó las lids correctamente?",
            "Aceptación 1 - Gestión y reorganización"
        );

        if (!reverseChangedOrder) {
            System.out.println("Fallo lógico: reverseTower no cambió realmente el orden esperado.");
        }
        if (!orderedCorrectly) {
            System.out.println("Fallo lógico: orderTower no dejó las cups de mayor a menor.");
        }
        if (!coverPlacedLidsAfterCups) {
            System.out.println("Fallo lógico: cover no dejó las lids después de las cups.");
        }

        System.out.println(
            (accepted && reverseChangedOrder && orderedCorrectly && coverPlacedLidsAfterCups)
                ? "Prueba aceptada"
                : "Prueba rechazada"
        );
        tower.makeInvisible();
        return tower;
    }

    /**
     * Aceptación 2:
     * eliminación + visibilidad + verificación real de desaparición.
     */
    public Tower demoRemovalVisibilityAndExit() {
        Tower tower = createTower(100, 50);

        pushCupStep(tower, 9);
        pushCupStep(tower, 5);
        pushLidStep(tower, 5);

        boolean existsBefore =
            contains(tower, "cup", "5") &&
            contains(tower, "lid", "5");

        removeLidStep(tower, 5);
        boolean lidRemoved =
            !contains(tower, "lid", "5") &&
            tower.getLidByNumber(5) == null;

        removeCupStep(tower, 5);
        boolean cupRemoved =
            !contains(tower, "cup", "5") &&
            tower.getCupByNumber(5) == null;

        invisibleStep(tower);
        boolean invisibleOk = !tower.isVisible();

        visibleStep(tower);
        boolean visibleOk = tower.isVisible();

        waitMillis(defaultDelay + 1000);

        boolean accepted = askUser(
            "¿Visualmente se eliminaron la lid y la cup, y el simulador cambió correctamente entre invisible y visible?",
            "Aceptación 2 - Eliminación y visibilidad"
        );

        if (!existsBefore) {
            System.out.println("Fallo lógico previo: los elementos iniciales no estaban donde debían.");
        }
        if (!lidRemoved) {
            System.out.println("Fallo lógico: la lid 5 no se eliminó completamente.");
        }
        if (!cupRemoved) {
            System.out.println("Fallo lógico: la cup 5 no se eliminó completamente.");
        }
        if (!invisibleOk || !visibleOk) {
            System.out.println("Fallo lógico: makeInvisible/makeVisible no cambió el estado correctamente.");
        }

        tower.exit();
        boolean exitOk =
            tower.getCupsSize() == 0 &&
            tower.getLidsSize() == 0 &&
            tower.getInsertionOrder().size() == 0 &&
            !tower.isVisible();

        if (!exitOk) {
            System.out.println("Fallo lógico: exit no limpió completamente la torre.");
        }

        System.out.println(
            (accepted && existsBefore && lidRemoved && cupRemoved && invisibleOk && visibleOk && exitOk)
                ? "Prueba aceptada"
                : "Prueba rechazada"
        );
        tower.makeInvisible();
        return tower;
    }
}
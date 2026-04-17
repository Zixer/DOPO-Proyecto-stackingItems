package tower;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class TowerAtest {

    private long defaultDelay;

    public TowerAtest(long defaultDelay) {
        this.defaultDelay = defaultDelay + 500;
    }

    public void setDefaultDelay(long delay) {
        this.defaultDelay = delay;
    }

    public long getDefaultDelay() {
        return defaultDelay;
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

    public void pushCupStep(Tower tower, String type, int size) {
        System.out.println("pushCup(\"" + type + "\", " + size + ")");
        try {
            tower.pushCup(type, size);
        } catch (towerException e) {
            System.out.println("Error al agregar cup: " + e.getMessage());
        }
        tower.makeVisible();
        waitMillis(defaultDelay);
    }

    public void pushLidStep(Tower tower, String type, int size) {
        System.out.println("pushLid(\"" + type + "\", " + size + ")");
        try {
            tower.pushLid(type, size);
        } catch (towerException e) {
            System.out.println("Error al agregar lid: " + e.getMessage());
        }
        tower.makeVisible();
        waitMillis(defaultDelay);
    }

    public void removeCupStep(Tower tower, int size) {
        System.out.println("removeCup(" + size + ")");
        tower.removeCup(size);
        tower.makeVisible();
        waitMillis(defaultDelay);
    }

    public void removeLidStep(Tower tower, int size) {
        System.out.println("removeLid(" + size + ")");
        tower.removeLid(size);
        tower.makeVisible();
        waitMillis(defaultDelay);
    }

    public void redrawStep(Tower tower) {
        System.out.println("redraw()");
        tower.redraw();
        waitMillis(defaultDelay);
    }

    public void coverStep(Tower tower) {
        System.out.println("cover()");
        tower.cover();
        tower.makeVisible();
        waitMillis(defaultDelay);
    }

    public void reverseTowerStep(Tower tower) {
        System.out.println("reverseTower()");
        tower.reverseTower();
        tower.makeVisible();
        waitMillis(defaultDelay);
    }

    public void orderTowerStep(Tower tower) {
        System.out.println("orderTower()");
        tower.orderTower();
        tower.makeVisible();
        waitMillis(defaultDelay);
        
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
     * Evidencia OpenerCup eliminando lids que impiden el paso.
     */
    public Tower demoOpenerCup() {
        Tower tower = createTower(100, 50);

        pushCupStep(tower, "normal", 9);
        pushLidStep(tower, "normal", 11);
        pushLidStep(tower, "normal", 13);
        pushCupStep(tower, "opener", 5);

        waitMillis(defaultDelay + 1000);

        boolean accepted = askUser(
            "¿La OpenerCup eliminó visualmente las lids que le impedían entrar?",
            "Prueba de aceptación - OpenerCup"
        );

        System.out.println(accepted ? "Prueba aceptada" : "Prueba rechazada");
        tower.makeInvisible();
        return tower;
    }

    /**
     * Aceptación 2:
     * Evidencia CrazyLid ubicándose como base y no como tapa tradicional.
     */
    public Tower demoCrazyLid() {
        Tower tower = createTower(100, 50);

        pushCupStep(tower, "normal", 9);
        pushCupStep(tower, "normal", 5);
        pushLidStep(tower, "crazy", 5);

        waitMillis(defaultDelay + 1000);

        boolean accepted = askUser(
            "¿La CrazyLid quedó ubicada como base de su cup y no como tapa normal?",
            "Prueba de aceptación - CrazyLid"
        );

        System.out.println(accepted ? "Prueba aceptada" : "Prueba rechazada");
        tower.makeInvisible();
        return tower;
    }

    /**
     * Demo adicional por si la quieres mostrar:
     * HierarchicalCup desplaza objetos menores.
     */
    public Tower demoHierarchicalCup() {
        Tower tower = createTower(100, 50);

        pushCupStep(tower, "normal", 9);
        pushCupStep(tower, "normal", 7);
        pushCupStep(tower, "normal", 5);
        pushCupStep(tower, "hierarchical", 6);

        waitMillis(defaultDelay + 1000);

        boolean accepted = askUser(
            "¿La HierarchicalCup desplazó los objetos menores al entrar?",
            "Prueba visual - HierarchicalCup"
        );

        System.out.println(accepted ? "Prueba aceptada" : "Prueba rechazada");
        tower.makeInvisible();
        return tower;
    }

    /**
     * Demo adicional por si la quieres mostrar:
     * FearfulLid no entra si su cup compañera no está.
     */
    public Tower demoFearfulLid() {
        Tower tower = createTower(100, 50);

        pushLidStep(tower, "fearful", 5);
        waitMillis(defaultDelay);

        pushCupStep(tower, "normal", 5);
        pushLidStep(tower, "fearful", 5);

        waitMillis(defaultDelay + 1000);

        boolean accepted = askUser(
            "¿La FearfulLid falló al entrar sin su cup y luego sí pudo entrar cuando la cup apareció?",
            "Prueba visual - FearfulLid"
        );

        System.out.println(accepted ? "Prueba aceptada" : "Prueba rechazada");
        tower.makeInvisible();
        return tower;
    }

    public void runSequence(Tower tower, List<String[]> actions) {
        for (String[] action : actions) {
            if (action.length == 0) continue;

            String command = action[0];

            if ("cup".equals(command)) {
                pushCupStep(tower, action[1], Integer.parseInt(action[2]));
            }
            else if ("lid".equals(command)) {
                pushLidStep(tower, action[1], Integer.parseInt(action[2]));
            }
            else if ("removeCup".equals(command)) {
                removeCupStep(tower, Integer.parseInt(action[1]));
            }
            else if ("removeLid".equals(command)) {
                removeLidStep(tower, Integer.parseInt(action[1]));
            }
            else if ("redraw".equals(command)) {
                redrawStep(tower);
            }
            else if ("cover".equals(command)) {
                coverStep(tower);
            }
            else if ("reverse".equals(command)) {
                reverseTowerStep(tower);
            }
            else if ("order".equals(command)) {
                orderTowerStep(tower);
            }
        }
    }
}
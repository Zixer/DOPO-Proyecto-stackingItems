package Test;
import tower.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class TowerAcceptanceTestC2 {

    private long defaultDelay;

    public TowerAcceptanceTestC2(long defaultDelay) {
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

    public Tower createTowerWithCups(int cups) {
        Tower tower = new Tower(cups);
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

    public void swapStep(Tower tower, String[] o1, String[] o2) {
        System.out.println("swap({" + o1[0] + "," + o1[1] + "}, {" + o2[0] + "," + o2[1] + "})");
        tower.swap(o1, o2);
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

    public void printOrder(Tower tower) {
        ArrayList<String[]> order = tower.getInsertionOrder();
        System.out.println("InsertionOrder actual:");
        for (String[] item : order) {
            System.out.println("{" + item[0] + ", " + item[1] + "}");
        }
        System.out.println("---------------------");
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

    private boolean contains(Tower tower, String type, String number) {
        return indexOf(tower, type, number) != -1;
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
     * constructor por número de cups + swap real.
     */
    public Tower demoCreateWithCupsAndSwap() {
        Tower tower = createTowerWithCups(4);

        boolean constructorOk =
            tower.getCupsSize() == 4 &&
            contains(tower, "cup", "1") &&
            contains(tower, "cup", "3") &&
            contains(tower, "cup", "5") &&
            contains(tower, "cup", "7");

        int posCup1Before = indexOf(tower, "cup", "1");
        int posCup7Before = indexOf(tower, "cup", "7");

        swapStep(tower, new String[]{"cup", "1"}, new String[]{"cup", "7"});

        boolean swapOk =
            indexOf(tower, "cup", "1") == posCup7Before &&
            indexOf(tower, "cup", "7") == posCup1Before;

        waitMillis(defaultDelay + 1000);

        boolean accepted = askUser(
            "¿Visualmente la torre se creó con 4 cups y luego la primera y la última intercambiaron su lugar?",
            "Aceptación 1 - Constructor y swap"
        );

        System.out.println(
            (accepted && constructorOk && swapOk)
                ? "Prueba aceptada"
                : "Prueba rechazada"
        );
        tower.makeInvisible();
        return tower;
    }

    /**
     * Aceptación 2:
     * cover + swapToReduce
     */
    public Tower demoCoverAndSwapToReduce() {
        Tower tower = createTower(100, 50);

        pushCupStep(tower, 9);
        pushCupStep(tower, 3);
        pushCupStep(tower, 7);
        pushLidStep(tower, 3);
        pushLidStep(tower, 9);

        coverStep(tower);

        boolean coverOk =
            indexOf(tower, "lid", "3") > indexOf(tower, "cup", "7") &&
            indexOf(tower, "lid", "9") > indexOf(tower, "cup", "7");

        tower.redraw();
        int originalHeight = tower.Height();

        String[][] suggestion = tower.swapToReduce();
        boolean suggestionOk =
            suggestion.length == 2 &&
            contains(tower, suggestion[0][0], suggestion[0][1]) &&
            contains(tower, suggestion[1][0], suggestion[1][1]);

        if (suggestionOk) {
            swapStep(tower, suggestion[0], suggestion[1]);
        }

        tower.redraw();
        int newHeight = tower.Height();
        boolean reduceOk = suggestionOk && newHeight <= originalHeight;

        waitMillis(defaultDelay + 1000);

        boolean accepted = askUser(
            "¿Visualmente la torre cubrió las cups con sus lids y el intercambio sugerido redujo o mejoró la altura?",
            "Aceptación 2 - Cover y swapToReduce"
        );

        System.out.println(
            (accepted && coverOk && suggestionOk && reduceOk)
                ? "Prueba aceptada"
                : "Prueba rechazada"
        );
        tower.makeInvisible();
        return tower;
    }
}

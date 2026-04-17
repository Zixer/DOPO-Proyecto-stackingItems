package tower;
import java.util.ArrayList;
import java.util.List;

public class TowerAcceptanceTest {

    private long defaultDelay;

    public TowerAcceptanceTest(long defaultDelay) {
        this.defaultDelay = defaultDelay+500;
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

    public void pushCupStep(Tower tower, int size) {
        System.out.println("pushCup(" + size + ")");
        tower.pushCup(size);
        tower.makeVisible();
        waitMillis(defaultDelay);
    }

    public void pushLidStep(Tower tower, int size) {
        System.out.println("pushLid(" + size + ")");
        tower.pushLid(size);
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

    public Tower demoOnlyCups() {
        Tower tower = createTower(100, 50);

        pushCupStep(tower, 9);
    
        pushCupStep(tower, 5);
        pushCupStep(tower, 3);
        pushCupStep(tower, 7);
        pushCupStep(tower, 11);

        tower.makeInvisible();
        return tower;
    }

    public Tower demoCupsAndLids() {
        Tower tower = createTower(100, 50);

        pushCupStep(tower, 9);
        pushCupStep(tower, 5);
        pushCupStep(tower, 3);

        pushLidStep(tower, 3);
        pushLidStep(tower, 5);
        pushLidStep(tower, 9);

        pushCupStep(tower, 7);
        pushLidStep(tower, 7);
        
        tower.makeInvisible();
        return tower;
    }

    public Tower demoCover() {
        Tower tower = createTower(100, 50);

        pushCupStep(tower, 9);
        pushCupStep(tower, 5);
        pushCupStep(tower, 3);

        pushLidStep(tower, 9);
        pushLidStep(tower, 3);
        pushLidStep(tower, 5);

        coverStep(tower);
        
        tower.makeInvisible();
        return tower;
    }

    public Tower demoReverse() {
        Tower tower = createTower(100, 50);

        pushCupStep(tower, 9);
        pushCupStep(tower, 5);
        pushCupStep(tower, 3);
        pushCupStep(tower, 7);

        reverseTowerStep(tower);
        
        tower.makeInvisible();
        return tower;
    }

    public Tower demoOrder() {
        Tower tower = createTower(100, 50);

        pushCupStep(tower, 5);
        pushCupStep(tower, 9);
        pushCupStep(tower, 3);
        pushCupStep(tower, 7);

        orderTowerStep(tower);
        
        tower.makeInvisible();
        return tower;
    }



    public void runSequence(Tower tower, List<String[]> actions) {
        for (String[] action : actions) {
            if (action.length == 0) continue;

            String command = action[0];

            if ("cup".equals(command)) {
                pushCupStep(tower, Integer.parseInt(action[1]));
            }
            else if ("lid".equals(command)) {
                pushLidStep(tower, Integer.parseInt(action[1]));
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
            else if ("removeCup".equals(command)) {
                removeCupStep(tower, Integer.parseInt(action[1]));
            }
            else if ("removeLid".equals(command)) {
                removeLidStep(tower, Integer.parseInt(action[1]));
            }
        }
    }

    public Tower demoCustomSequence() {
        Tower tower = createTower(100, 50);

        List<String[]> actions = new ArrayList<String[]>();
        actions.add(new String[]{"cup", "9"});
        actions.add(new String[]{"cup", "5"});
        actions.add(new String[]{"lid", "5"});
        actions.add(new String[]{"cup", "3"});
        actions.add(new String[]{"lid", "9"});
        actions.add(new String[]{"cover"});
        actions.add(new String[]{"reverse"});
        actions.add(new String[]{"redraw"});

        runSequence(tower, actions);

        return tower;
    }
}
import java.util.ArrayList;
import java.util.List;

/**
 * Clase auxiliar para ejecutar pruebas de aceptación visuales sobre Tower.
 *
 * La idea es construir secuencias paso a paso con pausas (delay)
 * para observar cómo redraw() reubica copas y tapas después de cada acción.
 */
public class TowerAcceptanceTest {

    private long defaultDelay;

    /**
     * Crea un ejecutor de pruebas con un delay por defecto.
     *
     * @param defaultDelay milisegundos de pausa entre pasos.
     */
    public TowerAcceptanceTest(long defaultDelay) {
        this.defaultDelay = defaultDelay + 200;
    }

    /**
     * Cambia el delay por defecto.
     *
     * @param delay nuevo delay en milisegundos.
     */
    public void setDefaultDelay(long delay) {
        this.defaultDelay = delay;
    }

    /**
     * Retorna el delay por defecto actual.
     */
    public long getDefaultDelay() {
        return defaultDelay;
    }

    /**
     * Pausa la ejecución.
     *
     * @param millis tiempo en milisegundos.
     */
    private void waitMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("La pausa fue interrumpida.");
        }
    }

    /**
     * Imprime un mensaje y espera el delay por defecto.
     *
     * @param message mensaje descriptivo del paso.
     */
    private void step(String message) {
        System.out.println(message);
        waitMillis(defaultDelay);
    }

    /**
     * Imprime un mensaje y espera un delay específico.
     *
     * @param message mensaje descriptivo del paso.
     * @param delay tiempo de espera en milisegundos.
     */
    private void step(String message, long delay) {
        System.out.println(message);
        waitMillis(delay);
    }

    /**
     * Crea una torre visible lista para pruebas.
     *
     * @param maxHeight altura máxima.
     * @param maxWidth ancho máximo.
     * @return la torre creada.
     */
    public Tower createTower(int maxHeight, int maxWidth) {
        Tower tower = new Tower(maxHeight, maxWidth);
        tower.makeVisible();
        tower.redraw();
        step("Torre creada y visible.");
        return tower;
    }

    /**
     * Inserta una copa y deja una pausa para observar el resultado.
     *
     * @param tower torre sobre la que se prueba.
     * @param size tamaño de la copa.
     */
    public void pushCupStep(Tower tower, int size) {
        System.out.println(">>> pushCup(" + size + ")");
        tower.pushCup(size);
    }

    /**
     * Inserta una copa y deja una pausa personalizada.
     *
     * @param tower torre sobre la que se prueba.
     * @param size tamaño de la copa.
     * @param delay pausa en milisegundos.
     */
    public void pushCupStep(Tower tower, int size, long delay) {
        System.out.println(">>> pushCup(" + size + ")");
        tower.pushCup(size);
    }

    /**
     * Inserta una tapa y deja una pausa para observar el resultado.
     *
     * @param tower torre sobre la que se prueba.
     * @param size tamaño de la tapa.
     * @param color color de la tapa.
     */
    public void pushLidStep(Tower tower, int size, String color) {
        System.out.println(">>> pushLid(" + size + ", \"" + color + "\")");
        tower.pushLid(size);
        waitMillis(defaultDelay);
    }

    /**
     * Inserta una tapa y deja una pausa personalizada.
     *
     * @param tower torre sobre la que se prueba.
     * @param size tamaño de la tapa.
     * @param color color de la tapa.
     * @param delay pausa en milisegundos.
     */
    public void pushLidStep(Tower tower, int size, String color, long delay) {
        System.out.println(">>> pushLid(" + size + ", \"" + color + "\")");
        tower.pushLid(size);
        waitMillis(delay);
    }

    /**
     * Fuerza redraw y pausa para observar.
     *
     * @param tower torre a redibujar.
     */
    public void redrawStep(Tower tower) {
        System.out.println(">>> redraw()");
        tower.redraw();
        waitMillis(defaultDelay);
    }

    /**
     * Fuerza redraw con pausa personalizada.
     *
     * @param tower torre a redibujar.
     * @param delay pausa en milisegundos.
     */
    public void redrawStep(Tower tower, long delay) {
        System.out.println(">>> redraw()");
        tower.redraw();
        waitMillis(delay);
    }

    /**
     * Elimina una copa específica y pausa.
     *
     * @param tower torre sobre la que se prueba.
     * @param size número de la copa a eliminar.
     */
    public void removeCupStep(Tower tower, int size) {
        System.out.println(">>> removeCup(" + size + ")");
        tower.removeCup(size);
        waitMillis(defaultDelay);
    }

    /**
     * Elimina una tapa específica y pausa.
     *
     * @param tower torre sobre la que se prueba.
     * @param size número de la tapa a eliminar.
     */
    public void removeLidStep(Tower tower, int size) {
        System.out.println(">>> removeLid(" + size + ")");
        tower.removeLid(size);
        waitMillis(defaultDelay);
    }

    /**
     * Ejecuta cover paso a paso.
     *
     * @param tower torre a cubrir.
     */
    public void coverStep(Tower tower) {
        System.out.println(">>> cover()");
        tower.cover();
        waitMillis(defaultDelay);
    }

    /**
     * Ejecuta reverseTower paso a paso.
     *
     * @param tower torre a invertir.
     */
    public void reverseTowerStep(Tower tower) {
        System.out.println(">>> reverseTower()");
        tower.reverseTower();
        waitMillis(defaultDelay);
    }

    /**
     * Ejecuta orderTower paso a paso.
     *
     * @param tower torre a ordenar.
     */
    public void orderTowerStep(Tower tower) {
        System.out.println(">>> orderTower()");
        tower.orderTower();
        waitMillis(defaultDelay);
    }

    /**
     * Pausa manual para que puedas observar el estado actual.
     *
     * @param message mensaje del punto de observación.
     */
    public void observe(String message) {
        step("[OBSERVAR] " + message);
    }

    /**
     * Pausa manual con tiempo específico.
     *
     * @param message mensaje del punto de observación.
     * @param delay pausa en milisegundos.
     */
    public void observe(String message, long delay) {
        step("[OBSERVAR] " + message, delay);
    }

    /**
     * Prueba visual simple solo con copas.
     * Sirve para observar cuándo una copa pasa a ir por fuera
     * o por dentro según el tamaño y el estado del layout.
     */
    public Tower demoOnlyCups() {
        Tower tower = createTower(100, 50);
        tower.makeVisible();
        waitMillis(defaultDelay);
        pushCupStep(tower, 9);
        tower.makeVisible();
        waitMillis(defaultDelay);
        pushCupStep(tower, 5);
        tower.makeVisible();
        waitMillis(defaultDelay);
        pushCupStep(tower, 3);
        tower.makeVisible();
        waitMillis(defaultDelay);
        pushCupStep(tower, 7);
        tower.makeVisible();
        waitMillis(defaultDelay);
        pushCupStep(tower, 11);
        tower.makeVisible();
        waitMillis(defaultDelay);
        tower.makeInvisible();
        return tower;
    }

    /**
     * Prueba visual con copas y tapas.
     *
     * Nota:
     * Como pushCup asigna color aleatorio, esta prueba es útil para observar
     * redraw, pero no garantiza relaciones exactas color-copa/tapa.
     */
    public Tower demoCupsAndLids() {
        Tower tower = createTower(100, 50);

        observe("Insertando copas");
        pushCupStep(tower, 9);
        tower.makeVisible();
        waitMillis(defaultDelay);
        pushCupStep(tower, 5);
        tower.makeVisible();
        waitMillis(defaultDelay);
        pushCupStep(tower, 3);
        tower.makeVisible();
        waitMillis(defaultDelay);

        observe("Insertando tapas");
        waitMillis(defaultDelay);
        tower.makeVisible();
        pushLidStep(tower, 3, "red");
        waitMillis(defaultDelay);
        tower.makeVisible();
        pushLidStep(tower, 5, "blue");
        waitMillis(defaultDelay);
        tower.makeVisible();
        pushLidStep(tower, 9, "green");
        waitMillis(defaultDelay);
        

        observe("Agregando más elementos para forzar nuevos redraw");
        pushCupStep(tower, 7);
        waitMillis(defaultDelay);
        pushLidStep(tower, 7, "yellow");
        waitMillis(defaultDelay);
        
        observe("Fin de demoCupsAndLids", 2000);
        return tower;
    }

    /**
     * Prueba de aceptación enfocada en cover().
     */
    public Tower demoCover() {
        Tower tower = createTower(100, 50);

        pushCupStep(tower, 9);
        pushCupStep(tower, 5);
        pushCupStep(tower, 3);

        pushLidStep(tower, 9, "black");
        pushLidStep(tower, 3, "cyan");
        pushLidStep(tower, 5, "orange");

        observe("Antes de cover()");
        coverStep(tower);
        observe("Después de cover()", 2000);

        return tower;
    }

    /**
     * Prueba de aceptación enfocada en reverseTower().
     */
    public Tower demoReverse() {
        Tower tower = createTower(100, 50);

        pushCupStep(tower, 9);
        pushCupStep(tower, 5);
        pushCupStep(tower, 3);
        pushCupStep(tower, 7);

        observe("Antes de reverseTower()");
        reverseTowerStep(tower);
        observe("Después de reverseTower()", 2000);

        return tower;
    }

    /**
     * Prueba de aceptación enfocada en orderTower().
     */
    public Tower demoOrder() {
        Tower tower = createTower(100, 50);

        pushCupStep(tower, 5);
        pushCupStep(tower, 9);
        pushCupStep(tower, 3);
        pushCupStep(tower, 7);

        observe("Antes de orderTower()");
        orderTowerStep(tower);
        observe("Después de orderTower()", 2000);

        return tower;
    }

    /**
     * Ejecuta una secuencia personalizada de acciones.
     *
     * Cada acción es un arreglo String[] con este formato:
     *
     * {"cup", "9"}
     * {"lid", "5", "red"}
     * {"redraw"}
     * {"cover"}
     * {"reverse"}
     * {"order"}
     * {"removeCup", "5"}
     * {"removeLid", "5"}
     *
     * @param tower torre sobre la cual ejecutar.
     * @param actions lista de acciones.
     */
    public void runSequence(Tower tower, List<String[]> actions) {
        for (String[] action : actions) {
            if (action.length == 0) {
                continue;
            }

            String command = action[0];

            if ("cup".equals(command)) {
                int size = Integer.parseInt(action[1]);
                pushCupStep(tower, size);

            } else if ("lid".equals(command)) {
                int size = Integer.parseInt(action[1]);
                String color = action[2];
                pushLidStep(tower, size, color);

            } else if ("redraw".equals(command)) {
                redrawStep(tower);

            } else if ("cover".equals(command)) {
                coverStep(tower);

            } else if ("reverse".equals(command)) {
                reverseTowerStep(tower);

            } else if ("order".equals(command)) {
                orderTowerStep(tower);

            } else if ("removeCup".equals(command)) {
                int size = Integer.parseInt(action[1]);
                removeCupStep(tower, size);

            } else if ("removeLid".equals(command)) {
                int size = Integer.parseInt(action[1]);
                removeLidStep(tower, size);
            }
        }
    }

    /**
     * Ejemplo de secuencia completamente personalizada.
     */
    public Tower demoCustomSequence() {
        Tower tower = createTower(100, 50);

        List<String[]> actions = new ArrayList<String[]>();
        actions.add(new String[]{"cup", "9"});
        actions.add(new String[]{"cup", "5"});
        actions.add(new String[]{"lid", "5", "red"});
        actions.add(new String[]{"cup", "3"});
        actions.add(new String[]{"lid", "9", "blue"});
        actions.add(new String[]{"cover"});
        actions.add(new String[]{"reverse"});
        actions.add(new String[]{"redraw"});

        runSequence(tower, actions);

        observe("Fin de demoCustomSequence", 2000);
        return tower;
    }
}
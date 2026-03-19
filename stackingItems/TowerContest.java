 import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TowerContest {
    /**
     * Genera una representación en texto de una secuencia válida de copas
     * que construye una torre cuya altura total sea exactamente h.
     *
     * Internamente utiliza buildSolution para calcular el orden de inserción.
     * Si existe una solución, retorna los tamaños de las copas separados por espacios.
     * Si no existe, retorna la cadena "impossible".
     *
     * @param n número de copas disponibles (1, 3, 5, ..., 2n-1).
     * @param h altura objetivo que se desea alcanzar.
     * @return un String con los tamaños de las copas en orden de inserción,
     *         o "impossible" si no existe solución.
     */
    public String solve(int n, long h) {
        List<Integer> order = buildSolution(n, h);
    
        if (order == null) {
            return "impossible";
        }
    
        String ans = "";
    
        for (int i = 0; i < order.size(); i++) {
            if (i > 0) {
                ans = ans + " ";
            }
            ans = ans + order.get(i);
        }
    
        return ans;
    }
    
    /**
     * Construye una torre real (objeto Tower) a partir de la solución generada
     * por el método solve.
     *
     * Si solve retorna "impossible", este método retorna null.
     * En caso contrario, crea una torre, inserta las copas en el orden indicado,
     * la hace visible y la dibuja.
     *
     * @param n número de copas disponibles.
     * @param h altura objetivo de la torre.
     * @return una instancia de Tower con la configuración correspondiente,
     *         o null si no existe solución.
     */
    public Tower simulate(int n, long h) {
        String solution = solve(n, h);
 
        if ("impossible".equals(solution)) {
            return null;
        }

        Tower tower = new Tower(100,50);
        String[] parts = solution.split(" ");

        for (String p : parts) {
            tower.pushCup(Integer.parseInt(p));
        }

        tower.makeVisible();
        tower.redraw();
        return tower;
    }

    /**
     * Construye una secuencia válida de tamaños de copas (1, 3, 5, ..., 2n-1)
     * cuya altura total sea exactamente h.
     *
     * Este método aplica una estrategia recursiva:
     * - Define límites mínimos y máximos posibles.
     * - Maneja casos especiales donde no existe solución.
     * - Selecciona subconjuntos de copas que aportan la altura requerida.
     * - Construye la solución combinando copas seleccionadas y restantes.
     *
     * @param n número de copas disponibles.
     * @param h altura objetivo.
     * @return una lista con los tamaños de copas en orden de inserción,
     *         o null si no existe solución.
     */
    private List<Integer> buildSolution(int n, long h) {
        long min = 2L * n - 1;
        long max = 1L * n * n;

        if (h < min || h > max || h == max - 2) {
            return null;
        }

        if (n == 1) {
            if (h == 1) {
                List<Integer> res = new ArrayList<>();
                res.add(1);
                return res;
            }
            return null;
        }

        long extra = h - (2L * n - 1);

        if (extra == 2) {
            List<Integer> sub = buildSolution(n - 1, h - 1);
            if (sub == null) return null;

            List<Integer> res = new ArrayList<>();
            res.add(sizeOf(n));
            res.addAll(sub);
            return res;
        }

        List<Integer> chosen = buildSubsetHeights(n - 1, extra);
        if (chosen == null) {
            return null;
        }

        Set<Integer> used = new HashSet<>(chosen);

        List<Integer> res = new ArrayList<>();
        res.addAll(chosen);          
        res.add(sizeOf(n));         

        for (int i = n - 1; i >= 1; i--) {
            int size = sizeOf(i);
            if (!used.contains(size)) {
                res.add(size);       
            }
        }

        return res;
    }

    /**
     * Construye un subconjunto de alturas impares:
     * 1, 3, 5, ..., 2m-1
     * cuya suma sea exactamente target.
     *
     * Utiliza un enfoque greedy desde los valores más grandes hacia los más pequeños,
     * evitando combinaciones inválidas conocidas (como casos que no tienen solución).
     *
     * El resultado se devuelve en orden ascendente.
     *
     * @param m número máximo de elementos disponibles (genera hasta 2m-1).
     * @param target suma objetivo que se desea alcanzar.
     * @return lista de alturas seleccionadas en orden ascendente,
     *         o null si no es posible construir la suma exacta.
     */
    private List<Integer> buildSubsetHeights(int m, long target) {
        List<Integer> chosenDescending = new ArrayList<>();

        while (target > 0 && m > 0) {
            long prevMax = 1L * (m - 1) * (m - 1);

            if (target <= prevMax && target != 2 && target != prevMax - 2) {
                m--;
            } else {
                int size = sizeOf(m);
                chosenDescending.add(size);
                target -= size;
                m--;
            }
        }

        if (target != 0) {
            return null;
        }

        List<Integer> chosenAscending = new ArrayList<>();
        for (int i = chosenDescending.size() - 1; i >= 0; i--) {
            chosenAscending.add(chosenDescending.get(i));
        }

        return chosenAscending;
    }

    /**
     * Calcula el tamaño (altura) de una copa a partir de su índice.
     *
     * La secuencia de tamaños es:
     * 1, 3, 5, ..., (2n - 1)
     *
     * @param cupIndex índice de la copa (1-based).
     * @return el tamaño correspondiente a la copa.
     */
    private int sizeOf(int cupIndex) {
        return 2 * cupIndex - 1;
    }
}
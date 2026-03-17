import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StackingItems {

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
     * Retorna las alturas elegidas en orden ascendente.
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

    private int sizeOf(int cupIndex) {
        return 2 * cupIndex - 1;
    }
}
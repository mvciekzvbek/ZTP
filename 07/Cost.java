import javax.ejb.Stateless;
import java.util.ArrayList;

/**
 * @author Maciej Ząbek
 * @version 1.0
 */
@Stateless
public class Cost implements ICostRemote {
    /**
     * calculate minimum cost of cut
     * @param x array of x costs
     * @param y array of y costs
     * @return cost
     */
    public double calculate(ArrayList<Double> x, ArrayList<Double> y) {
        double result = 0;
        int horizontal = 1;
        int vertical = 1;
        int xsize = x.size();
        int ysize = y.size();

        int i = 0, j = 0;
        while (i < xsize && j < ysize) {
            if (x.get(i) > y.get(j)) {
                result += x.get(i) * vertical;
                horizontal++;
                i++;
            } else {
                result += y.get(j) * horizontal;
                vertical++;
                j++;
            }
        }

        int total = 0;
        while (i < xsize) {
            total += x.get(i++);
        }
        result += total * vertical;

        total = 0;
        while (j < ysize) {
            total += y.get(j++);
        }
        result += total * horizontal;

        return result;
    }
}

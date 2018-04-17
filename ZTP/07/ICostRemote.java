import javax.ejb.Remote;
import java.util.ArrayList;

/**
 * @author Maciej Ząbek
 * @version 1.0
 */
@Remote
public interface ICostRemote {
    /**
     * calculate minimum cost of cut
     * @param x array of x costs
     * @param y array of y costs
     * @return cost
     */
    double calculate(ArrayList<Double> x, ArrayList<Double> y);
}

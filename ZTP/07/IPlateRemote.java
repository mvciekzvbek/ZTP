import javax.ejb.Remote;
import java.util.ArrayList;

/**
 * @author Maciej Ząbek
 * @version 1.0
 */
@Remote
public interface IPlateRemote {
    /**
     * add x cost to x array
     * @param x cost o cut
     */
    void addX(double x);

    /**
     * add y cost to x array
     * @param y cost o cut
     */
    void addY(double y);

    /**
     * Return sorted array
     * @return sorted arraylist of x's
     */
    ArrayList<Double> getXArray();

    /**
     * Return sorted array
     * @return sorted arraylist of y's
     */
    ArrayList<Double> getYArray();
}

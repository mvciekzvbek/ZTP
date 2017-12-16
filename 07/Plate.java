import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Maciej Ząbek
 * @version 1.0
 */
@Stateless
public class Plate implements IPlateRemote{
    private ArrayList<Double> xArray;
    private ArrayList<Double> yArray;

    /**
     * constructor, initialize arraylists
     */
    public Plate(){
        xArray = new ArrayList<>();
        yArray = new ArrayList<>();
    }

    /**
     * add x cost to x array
     * @param x cost o cut
     */
    public void addX (double x){
        if (x > 0) xArray.add(x);
    }

    /**
     * add x cost to x array
     * @param y cost of cut
     */
    public void addY (double y){
        if (y > 0) yArray.add(y);

    }

    /**
     * Return sorted array
     * @return sorted arraylist of x's
     */
    public ArrayList<Double> getXArray() {
        Collections.sort(xArray, Collections.reverseOrder());
        return xArray;
    }

    /**
     * Return sorted array
     * @return sorted arraylist of y's
     */
    public ArrayList<Double> getYArray() {
        Collections.sort(yArray, Collections.reverseOrder());
        return yArray;
    }
}

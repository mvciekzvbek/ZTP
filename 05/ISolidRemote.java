import java.util.ArrayList;
/**
 * @author Maciej Zabek
 * @version 1.0
 */
public interface ISolidRemote {
    /**
     * calculating figure area
     * @param points retrieved from database
     * @return String with calculated value
     */
    String calculateArea(ArrayList<Solid.Point> points);
}

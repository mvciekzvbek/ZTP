import javax.ejb.Remote;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Maciej Ząbek
 * @version 1.0
 */
@Remote
public interface ISearchRemote {
    /**
     * adjacency list setter
     * @param adj adjacency lsit
     */
    void setAdj(List<LinkedList<Integer>> adj);

    /**
     * size setter
     * @param v size
     */
    void setV(int v);

    /**
     * finds connected components
     * @return number of connected components
     */
    int connectedComponents();
}

import javax.ejb.Remote;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Maciej Ząbek
 * @version 1.0
 */
@Remote
public interface IGraphRemote {
    /**
     * graph size getter
     * @return graph size
     */
    int getV();

    /**
     * adjacency list getter
     * @return adjacency list
     */
    List<LinkedList<Integer>> getAdj();

    /**
     * graph size setter + graph initializer
     * @param v size
     */
    void setV(int v);

    /**
     * Adds edge to graph
     * @param v1 first vertex of edge
     * @param v2 second vertex of edge
     */
    void addEdge(int v1, int v2);
}

import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Maciej Ząbek
 */
@Stateless
public class Graph implements IGraphRemote {
    private int V;
    private List<LinkedList<Integer>> adj;

    /**
     * adjacency list getter
     * @return adjacency list
     */
    public List<LinkedList<Integer>> getAdj() {
        return adj;
    }

    /**
     * graph size getter
     * @return graph size
     */
    public int getV() {
        return V;
    }

    /**
     * graph size setter + graph initializer
     * @param v size
     */
    public void setV(int v) {
        V = v;
        adj = new ArrayList<>(v);
        for (int i = 0; i < v; ++i) adj.add(new LinkedList<>());
    }

    /**
     * Adds edge to adjacency list
     * @param v1 first vertex of edge
     * @param v2 second vertex of edge
     */
    public void addEdge(int v1, int v2) {
        adj.get(v1-1).add(v2-2);
    }
}

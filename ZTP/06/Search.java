import javax.ejb.Stateless;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Maciej Ząbek
 * @version 1.0
 */
@Stateless
public class Search implements ISearchRemote {

    private List<LinkedList<Integer>> adj;
    private int V;

    /**
     * adjacency list setter
     * @param adj adjacency list
     */
    public void setAdj(List<LinkedList<Integer>> adj) {
        this.adj = adj;
    }

    /**
     * size setter
     * @param v size
     */
    public void setV(int v) {
        V = v;
    }

    /**
     * Depth-first search
     * @param vertex to analyze
     * @param visited array of vertex
     */
    private void dfs(int vertex, boolean[] visited) {
        visited[vertex] = true;

        for (Object item: adj.get(vertex)) {
            if (!visited[(int) item]) {
                dfs((Integer) item, visited);
            }
        }
    }

    /**
     * finds connected components
     * @return number of connected components
     */
    public int connectedComponents() {
        boolean[] visited = new boolean[V];
        int connectedComponents = 0;

        for (int i = 0; i < V; ++i) {
            if (!visited[i]) {
                dfs(i, visited);
                connectedComponents += 1;
            }
        }
        return  connectedComponents;
    }
}

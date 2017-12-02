import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Maciej Ząbek
 * @version 1.0
 */
public class AppClient {
    private static IGraphRemote graph;
    private static ISearchRemote search;
    public static final long serialVersionUID = 1L;

    /**
     *  Creates edges list
     * @param text text line from file
     * @param edges list of edges
     */
    private static void parseLineFromFile(String text, List<int[]> edges){
        try {
            String[] graphEdges = text.trim().split("\\s+");
            for (int i = 0; i < graphEdges.length - 1; i += 2) {
                int[] edge = new int[2];
                int v = Integer.parseInt(graphEdges[i]);
                int u = Integer.parseInt(graphEdges[i + 1]);
                edge[0] = v;
                edge[1] = u;
                edges.add(edge);
            }
        } catch (Exception e) {
            System.out.println("Exeption! " + e);
        }
    }

    /**
     * finds unique value from edges array to define graph size
     * @param edges
     * @return set of unique values
     */
    private static Set<Integer> findUniqueVertices(List<int[]> edges) {
        List <Integer> vertices = new ArrayList<>();

        for(int[] edge : edges) {
            for(int vertexN : edge) {
                vertices.add(vertexN);
            }
        }

        return new HashSet<Integer>(vertices);
    }

    /**
     * get sources from JNDI
     */
    private static void lookup(){
        try {
            Context cont = new InitialContext();
            graph = (IGraphRemote) cont.lookup("java:global/124640/Graph!IGraphRemote");
            search = (ISearchRemote) cont.lookup("java:global/124640/Search!ISearchRemote");
        } catch (Exception e) { }
    }

    /**
     * main method of the client application
     * @param args
     */
    public static void main(String args[]) {
        try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
            String line;
            List<int[]> edges = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                parseLineFromFile(line, edges);
            }

            Set<Integer> unique = findUniqueVertices(edges);

            lookup();

            graph.setV(unique.size());

            for(int[] vertex : edges) graph.addEdge(vertex[0], vertex[1]);

            search.setV(graph.getV());
            search.setAdj(graph.getAdj());

            int result = search.connectedComponents();

            System.out.println("Ilosc skladowych: " + result);
        } catch (Exception e) {}
    }

}

import pl.jrj.dsm.IDSManagerRemote;
import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * @author Maciej Zabek
 * @version 1.0
 */
public class Solver extends HttpServlet {
    public static final long serialVersionUID = 1L;
    private ArrayList <Solid.Point> points = new ArrayList<>();

    @EJB
    private ISolidRemote solid;

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     *
     * @param request from servlet http methods
     * @param response from servlet http methods
     * @throws ServletException
     * @throws IOException
     */
    protected void processRequest(HttpServletRequest request,
                                  HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String tableName = request.getParameter("t");
            String result;
            PrintWriter out = response.getWriter();

            try {
                retrieveData(tableName);
            } catch (Exception e){}

            if (points.size() > 0) {
                result = solid.calculateArea(points);
            } else {
                result = String.format("%.5g%n", 0.0);
            }
            out.println(result);
        } catch (Exception e){}
    }


    /**
     * Retrieving data from database
     * @param tableName received from http request
     * @throws Exception
     */
    private void retrieveData(String tableName) throws Exception {
        DataSource dataSource = getDataSource();
        Connection connection = dataSource.getConnection();
        ResultSet resultSet = connection.createStatement().executeQuery("SELECT x, y, z from " + tableName + ";");
        while (resultSet.next()) {
            double x = resultSet.getFloat("x");
            double y = resultSet.getFloat("y");
            double z = resultSet.getFloat("z");
            Solid.Point point = new Solid.Point(x,y,z);
            points.add(point);
        }
    }

    /**
     * Getting name of data source
     * @return data source
     * @throws Exception
     */
    private DataSource getDataSource() throws Exception {
        Context context = new InitialContext();
        IDSManagerRemote remote = (IDSManagerRemote) context.lookup("java:global/ejb-project/DSManager!pl.jrj.dsm.IDSManagerRemote");
        String dbName = remote.getDS();
        return (DataSource) context.lookup(dbName);
    }
}

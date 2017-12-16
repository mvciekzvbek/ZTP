import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 * @author Maciej Ząbek
 * @version 1.0
 */
public class AppClient {
    private static ICostRemote cost;
    private static IPlateRemote plate;

    /**
     * Load ejbs from JNDI
     */
    private static void loadEJBs(){
        try {
            Context cont = new InitialContext();
            cost = (ICostRemote) cont.lookup(
                    "java:global/124640/Cost!ICostRemote");
            plate = (IPlateRemote) cont.lookup(
                    "java:global/124640/Plate!IPlateRemote");
        } catch (Exception e) { }
    }

    /**
     * Get data source from JNDI
     * @param ds name of data source in JNDI
     * @return Data Source
     * @throws Exception
     */
    private static DataSource getDataSource(String ds) throws Exception{
        Context context = new InitialContext();
        return (DataSource) context.lookup(ds);
    }

    /**
     * Retrieve data from database
     * @param ds data source
     * @param tableName name of the table
     * @throws Exception
     */
    private static void retrieveData(DataSource ds, String tableName)
            throws Exception {
        Connection connection = ds.getConnection();
        ResultSet resultSet = connection.createStatement().executeQuery(
                "SELECT x, y from " + tableName + ";");
        while (resultSet.next()) {
            double x = resultSet.getFloat("x");
            double y = resultSet.getFloat("y");
            plate.addX(x);
            plate.addY(y);
        }
    }

    /**
     * main method
     * @param args
     */
    public static void main(String args[]){
        loadEJBs();
        try {
            DataSource ds = getDataSource(args[0]);
            retrieveData(ds, args[1]);
            double result = cost.calculate(plate.getXArray(),
                    plate.getYArray());
            System.out.println("Koszt ciecia : " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

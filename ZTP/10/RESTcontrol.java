import pl.jrj.mdb.IMdbManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * @author Maciej Ząbek
 * @version 1.0
 */

@Path("/control")
public class RESTcontrol {
    private static int counter = 0;
    private static int errorCounter = 0;
    private static boolean isStarted = false;
    private static String sessionId;
    private static IMdbManager mdb;

    /**
     * start function responsible for change state
     */
    @GET
    @Path("/start")
    public void start() {
        try {
            getIMdbManager();
            if (!isStarted && isRegistered()) {
                isStarted = true;
            } else {
                errorCounter++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * clears counters
     */
    @GET
    @Path("/clr")
    public void clr() {
        counter = 0;
        errorCounter = 0;
    }

    /**
     * increment counter by one
     */
    @GET
    @Path("/icr")
    public void icr() {
        if (isStarted) {
            counter++;
        } else {
            errorCounter++;
        }
    }

    /**
     * decrement counter by one
     */
    @GET
    @Path("/dcr")
    public void dcr() {
        if (isStarted) {
            counter--;
        } else {
            errorCounter++;
        }
    }

    /**
     * stop counting
     */
    @GET
    @Path("/stop")
    public void stop() {
        if (isStarted) {
            isStarted = false;
        } else {
            errorCounter++;
        }
    }

    /**
     * increment counter with given number
     * @param number to increment with
     */
    @GET
    @Path("/icr/{n}")
    public void icrN(@PathParam(value = "n") String number) {
        if (isStarted) {
            try {
                counter += NumberFormat.getInstance().parse(number).intValue();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            errorCounter++;
        }
    }

    /**
     * decrement counter with given number
     * @param number to decrement with
     */
    @GET
    @Path("/dcr/{n}")
    public void dcrN(@PathParam(value = "n") String number) {
        if (isStarted) {
            try {
                counter -= NumberFormat.getInstance().parse(number).intValue();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            errorCounter++;
        }
    }

    /**
     * check counter
     * @return counter value
     */
    @GET
    @Path("/res")
    public int res() {
        if (isRegistered() && counter != 0) {
            return Integer.parseInt(sessionId) % counter;
        } else {
            return 0;
        }
    }

    /**
     * check errors
     * @return error counter
     */
    @GET
    @Path("/err")
    public int err() {
        if (isRegistered() && errorCounter != 0) {
            return Integer.parseInt(sessionId) % errorCounter;
        } else {
            return 0;
        }
    }


    /**
     * get IMdbManager from JNDI
     */
    private void getIMdbManager() {
        try {
            mdb = (IMdbManager) new InitialContext().
                    lookup("java:global/mdb-project/" +
                            "MdbManager!pl.jrj.mdb.IMdbManager");
            sessionId = mdb.sessionId("124640");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * check if session was registered
     * @return boolean
     */
    private boolean isRegistered(){
        return sessionId != null;
    }
}

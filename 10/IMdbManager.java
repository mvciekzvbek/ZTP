package pl.jrj.mdb;
import javax.ejb.Remote;

/**
 * @author Maciej Ząbek
 * @version 1.0
 */
@Remote
public interface IMdbManager {
    /**
     * retrieve sessionId
     * @param album student id number
     * @return session id
     */
    public String sessionId(String album);
}
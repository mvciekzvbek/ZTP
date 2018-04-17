package pl.jrj.dsm;
import javax.ejb.Remote;

/**
 * @author Maciej Zabek
 * @version 1.0
 */
@Remote
public interface IDSManagerRemote {
    /**
     * retrieving name of the data source
     * @return name of the data source
     */
    public String getDS();
}
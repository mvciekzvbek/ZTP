import javax.ejb.Remote;

@Remote
public interface NprimeRemote {
    /**
     *
     * @param n number
     * @return prime number
     */
    public int prime(int n);

    /**
     * function responsible for returning prime number to servlet
     * @param n number
     * @param method http method
     * @return prime number
     */
    public int prime(int n, String method);
}


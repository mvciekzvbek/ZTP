import javax.ejb.Stateless;

/**
 *
 * @author Maciej Zabek
 * @version 1.0
 */
@Stateless
public class NprimeBean implements NprimeRemote {
    /**
     *
     * @param n number
     * @return prime number
     */
    public int prime(int n){
        if (n>=0){
            int result = findBigger(n);
            return result;
        }
        return 0;
    }

    /**
     * function responsible for returning prime number to servlet
     * @param n number
     * @param method http method
     * @return prime number
     */
    public int prime(int n, String method){
        if (n>=0){
            int result = 0;
            if (method.equals("GET")){
                result = findSmaller(n);
            } else if (method.equals("POST")){
                result = findBigger(n);
            }
            return result;
        }
        return 0;
    }


    /**
     * checks whether an int is prime or not.
     * @param n analyzed number
     * @return true if prime, false if not
     */
    public boolean isPrime(int n){
        if (n==2) return true;
        if (n%2==0) return false;
        for (int i=3; i*i<=n; i+=2) {
            if (n%i==0)
                return false;
        }
        return true;
    }

    /**
     * function responsible for looking for bigger prime numbers
     * @param n number
     * @return first bigger prime number
     */
    public int findBigger(int n){
        int i = n;
        while (i < n*n){
            i++;
            boolean iP = isPrime(i);
            if (iP && (i-3)%4==0) return i;
        }
        return 0;
    }

    /**
     * function responsible for looking for smaller prime numbers
     * @param n number
     * @return first smaller prime number
     */
    public int findSmaller(int n){
        int i = n;
        while (i>0){
            boolean iP = isPrime(i);
            if (iP && (i-3)%4==0) return i;
            i--;
        }
        return 0;
    }
}


